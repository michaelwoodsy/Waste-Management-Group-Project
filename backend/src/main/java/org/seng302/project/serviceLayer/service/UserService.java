package org.seng302.project.serviceLayer.service;

import net.minidev.json.JSONObject;
import org.seng302.project.repositoryLayer.model.Address;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.AddressRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.repositoryLayer.specification.UserSpecifications;
import org.seng302.project.serviceLayer.dto.user.*;
import org.seng302.project.serviceLayer.exceptions.BadRequestException;
import org.seng302.project.serviceLayer.exceptions.ForbiddenException;
import org.seng302.project.serviceLayer.exceptions.NoUserExistsException;
import org.seng302.project.serviceLayer.exceptions.dgaa.DGAARevokeAdminSelfException;
import org.seng302.project.serviceLayer.exceptions.dgaa.ForbiddenDGAAActionException;
import org.seng302.project.serviceLayer.exceptions.register.ExistingRegisteredEmailException;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Provides logic for User objects
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class.getName());
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, AddressRepository addressRepository,
                       AuthenticationManager authenticationManager, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Searches for users based on a search query string.
     * Regular expression for splitting search query taken from linked website.
     *
     * @param searchQuery Query to search users by.
     * @return List of users matching the search query.
     * @see <a href="https://stackoverflow.com/questions/1757065/java-splitting-a-comma-separated-string-but-ignoring-commas-in-quotes">
     * https://stackoverflow.com/questions/1757065/java-splitting-a-comma-separated-string-but-ignoring-commas-in-quotes</a>
     */
    public List<Object> searchUsers(String searchQuery, Integer pageNumber, String sortBy) {
        List<User> users;
        long totalCount;
        boolean sortASC;
        List<Object> sortChecker = checkSort(sortBy);
        sortASC = (boolean) sortChecker.get(0);
        sortBy = (String) sortChecker.get(1);
        searchQuery = searchQuery.toLowerCase(); // Convert search query to all lowercase.
        String[] conjunctions = searchQuery.split(" or (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // Split by OR

        Specification<User> hasSpec = Specification.where(null);
        Specification<User> containsSpec = Specification.where(null);

        for (String conjunction : conjunctions) {
            Specification<User> newHasSpec = Specification.where(null);
            Specification<User> newContainsSpec = Specification.where(null);
            String[] names = conjunction.split("( and |\\s)(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // Split by AND
            // Iterate over the names in the search and check if they are quoted
            for (String name : names) {
                if (Pattern.matches("^\".*\"$", name)) {
                    name = name.replace("\"", "");
                    newHasSpec = newHasSpec.and(UserSpecifications.hasName(name));
                } else {
                    newHasSpec = newHasSpec.and(UserSpecifications.hasName(name));
                    newContainsSpec = newContainsSpec.and(UserSpecifications.containsName(name));
                }
            }
            hasSpec = hasSpec.or(newHasSpec);
            containsSpec = containsSpec.or(newContainsSpec);
        }

        Specification<User> spec = hasSpec.or(containsSpec);

        // query the repository and get a Page object, from which you can get the content by doing page.getContent()
        if(!sortBy.isEmpty()){
            Page<User> page = sortUserSearch(spec, sortBy, sortASC, pageNumber);
            totalCount = page.getTotalElements();
            users = page.getContent();
        } else {
            Pageable pageable = PageRequest.of(pageNumber, 10);
            Page<User> page = userRepository.findAll(spec, pageable);
            totalCount = page.getTotalElements();
            users = page.getContent();
        }

        logger.info("Retrieved {} users, showing {}", totalCount, users.size());

        // Map user objects to GetUserDTO objects and return
        return Arrays.asList(users.stream().map(GetUserDTO::new).collect(Collectors.toList()), totalCount);
    }

    /**
     * Service method for loging in a user
     * @param loginCredentials login credentials of a user
     * @return the userId of the user logged in inside of a JSONObject
     */
    public JSONObject login(LoginCredentialsDTO loginCredentials) {
        var token = new UsernamePasswordAuthenticationToken(
                loginCredentials.getEmail(), loginCredentials.getPassword());
        var auth = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(auth);
        Integer userId = userRepository.findByEmail(loginCredentials.getEmail()).get(0).getId();
        logger.info("Login successful!");

        JSONObject returnJSON = new JSONObject();
        returnJSON.put("userId", userId);
        return returnJSON;
    }

    /**
     * Service method for creating a user
     * @param dto Validated dto containing the users information
     * @return the userId of the user logged in inside of a JSONObject
     */
    public LoginCredentialsDTO createUser(PostUserDTO dto) {
        var address = new Address(dto.getHomeAddress());
        var newUser = new User(dto, address);

        // If email address is already registered
        if (!userRepository.findByEmail(newUser.getEmail()).isEmpty()) {
            throw new ExistingRegisteredEmailException();
        }

        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        addressRepository.save(newUser.getHomeAddress());
        userRepository.save(newUser);

        logger.info("Successful registration of user with ID: {}", newUser.getId());
        return new LoginCredentialsDTO(dto.getEmail(), dto.getPassword());
    }

    /**
     * Service method for editing a user
     * @param dto Validated dto containing the users information
     */
    public void editUser(PutUserDTO dto) {
        var user = userRepository.findById(dto.getId()).orElseThrow(() -> new NoUserExistsException(dto.getId()));
        var address = user.getHomeAddress();

        // If email address is already registered
        var users = userRepository.findByEmail(dto.getEmail());
        if (!users.isEmpty() && !user.getId().equals(users.get(0).getId())) {
            throw new ExistingRegisteredEmailException();
        }

        //Check if the user wants to change their email
        if (!dto.getEmail().equals(user.getEmail()) &&
                (dto.getCurrentPassword() == null || dto.getCurrentPassword().equals("") ||
                        !passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword()))) {
            //If current password does not match
            throw new BadRequestException("Incorrect Password.");
        }

        //Check if user wants to change their password
        if (dto.getNewPassword() != null && !dto.getNewPassword().equals("")) {
            //If current password does not match
            if (dto.getCurrentPassword() == null || dto.getCurrentPassword().equals("") ||
                    !passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
                throw new BadRequestException("Incorrect Password.");
            }
            user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        }

        //Change fields
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setMiddleName(dto.getMiddleName());
        user.setNickname(dto.getNickname());
        user.setBio(dto.getBio());
        user.setEmail(dto.getEmail());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setPhoneNumber(dto.getPhoneNumber());

        //Change address
        address.setStreetNumber(dto.getHomeAddress().getStreetNumber());
        address.setStreetName(dto.getHomeAddress().getStreetName());
        address.setCity(dto.getHomeAddress().getCity());
        address.setRegion(dto.getHomeAddress().getRegion());
        address.setCountry(dto.getHomeAddress().getCountry());
        address.setPostcode(dto.getHomeAddress().getPostcode());

        user.setHomeAddress(address);

        addressRepository.save(user.getHomeAddress());
        userRepository.save(user);
    }

    /**
     * Service method for retrieving a user
     * @param id ID of the user to retrieve
     * @return the user data inside of a GetUserDTO
     */
    public GetUserDTO getUser(Integer id) {
        return new GetUserDTO(userRepository.findById(id).orElseThrow(() -> new NoUserExistsException(id)));
    }

    /**
     * Service method for making a user a GAA
     * @param dto dto containing the ID of the user and the details of the logged in user
     */
    public void dgaaMakeAdmin(DGAAMakeRevokeAdminDTO dto) {
        //Check if user exists
        var user = userRepository.findById(dto.getId()).orElseThrow(() -> new NoUserExistsException(dto.getId()));

        //Update user's role
        user.setRole("globalApplicationAdmin");
        userRepository.save(user);
    }

    /**
     * Service method for revoking a users GAA privileges
     * @param dto dto containing the ID of the user and the details of the logged in user
     */
    public void dgaaRevokeAdmin(DGAAMakeRevokeAdminDTO dto) {
        //Check if user exists
        var user = userRepository.findById(dto.getId()).orElseThrow(() -> new NoUserExistsException(dto.getId()));

        //Check that DGAA is not revoking themselves
        if (user.getEmail().equals(dto.getAppUser().getUsername())) {
            throw new DGAARevokeAdminSelfException();
        }

        //Update user's role
        user.setRole("user");
        userRepository.save(user);
    }


    /**
     * Checks if the logged in user is able to perform actions on the user with id userId's account
     * If the user logged in has the ID userId, or the logged in user is a GAA.
     * @param userId ID of the user account wanting to preform actions on.
     * @param appUser Details of the logged in user
     */
    public void checkForbidden(Integer userId, AppUserDetails appUser) {
        var loggedInUser = userRepository.findByEmail(appUser.getUsername()).get(0);

        if (!loggedInUser.getId().equals(userId) && !loggedInUser.isGAA()) {
            var exception = new ForbiddenException(
                    String.format("You are not Authorised to make changes/view information for user with ID %d's account", userId));
            logger.error(exception.getMessage());
            throw exception;
        }
    }

    /**
     * Checks if the logged in user has the role "defaultGlobalApplicationAdmin", if not throw an exception
     * @param appUser logged in users details
     */
    public void checkRequesterIsDGAA(AppUserDetails appUser) {
        User requestMaker = userRepository.findByEmail(appUser.getUsername()).get(0);
        if (!requestMaker.getRole().equals("defaultGlobalApplicationAdmin")) {
            throw new ForbiddenDGAAActionException();
        }
    }

    /**
     * Helper function for user search, does the sorting
     * @param spec the specification used to search by
     * @param sortBy the column that is to be sorted
     * @param sortASC the direction of the sort
     * @return the sorted list of users searched for
     */
    public Page<User> sortUserSearch(Specification<User> spec, String sortBy, boolean sortASC, Integer pageNumber){
        if(sortASC){
            sortBy = sortBy.substring(0, sortBy.lastIndexOf("A"));
            Pageable pageable = PageRequest.of(pageNumber, 10, Sort.by(Sort.Order.asc(sortBy).ignoreCase()));
            return userRepository.findAll(spec, pageable);
        } else {
            sortBy = sortBy.substring(0, sortBy.lastIndexOf("D"));
            Pageable pageable = PageRequest.of(pageNumber, 10, Sort.by(Sort.Order.desc(sortBy).ignoreCase()));
            return userRepository.findAll(spec, pageable);
        }
    }

    public List<Object> checkSort(String sortBy){
        switch(sortBy){
            case "idASC": case "idDESC": case "firstNameASC": case "firstNameDESC": case "middleNameASC": case "middleNameDESC":
            case "lastNameASC": case "lastNameDESC": case "emailASC": case "emailDESC": case "homeAddressASC": case "homeAddressDESC":
                break;
            default:
                sortBy = "";
        }
        return List.of(sortBy.contains("ASC"), sortBy);
    }


}
