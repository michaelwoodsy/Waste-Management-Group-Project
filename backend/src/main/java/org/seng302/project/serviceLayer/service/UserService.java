package org.seng302.project.serviceLayer.service;

import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.AddressRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.repositoryLayer.specification.UserSpecifications;
import org.seng302.project.serviceLayer.dto.user.*;
import org.seng302.project.serviceLayer.exceptions.ForbiddenActionException;
import org.seng302.project.serviceLayer.exceptions.NoUserExistsException;
import org.seng302.project.serviceLayer.exceptions.dgaa.DGAARevokeAdminSelfException;
import org.seng302.project.serviceLayer.exceptions.dgaa.ForbiddenDGAAActionException;
import org.seng302.project.serviceLayer.exceptions.register.ExistingRegisteredEmailException;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<UserResponseDTO> searchUsers(String searchQuery) {
        List<User> users;

        if (searchQuery.equals("")) {
            // Search query is for all results
            users = userRepository.findAll();

        } else {
            Set<User> result = new LinkedHashSet<>();

            searchQuery = searchQuery.toLowerCase(); // Convert search query to all lowercase.
            String[] conjunctions = searchQuery.split(" or (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // Split by OR

            for (String conjunction : conjunctions) {
                Specification<User> hasSpec = Specification.where(null);
                Specification<User> containsSpec = Specification.where(null);
                var searchContains = false;
                String[] names = conjunction.split("( and |\\s)(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // Split by AND

                // Iterate over the names in the search and check if they are quoted
                for (String name : names) {
                    if (Pattern.matches("^\".*\"$", name)) {
                        name = name.replace("\"", "");
                        hasSpec = hasSpec.and(UserSpecifications.hasName(name));
                    } else {
                        hasSpec = hasSpec.and(UserSpecifications.hasName(name));
                        containsSpec = containsSpec.and(UserSpecifications.containsName(name));
                        searchContains = true;
                    }
                }

                // query the repository and add to results set
                result.addAll(userRepository.findAll(hasSpec));
                if (searchContains) {
                    result.addAll(userRepository.findAll(containsSpec));
                }

            }

            // convert set to a list
            users = new ArrayList<>(result);
        }

        logger.info("Retrieved {} users", users.size());

        // Map user objects to UserResponseDTO objects and return
        return users.stream().map(UserResponseDTO::new).collect(Collectors.toList());
    }

    /**
     * Service method for loging in a user
     * @param loginCredentials login credentials of a user
     * @return the userId of the user logged in inside of a UserLoginResponseDTO
     */
    public UserLoginResponseDTO login(LoginCredentialsDTO loginCredentials) {
        var token = new UsernamePasswordAuthenticationToken(
                loginCredentials.getEmail(), loginCredentials.getPassword());
        var auth = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(auth);
        Integer userId = userRepository.findByEmail(loginCredentials.getEmail()).get(0).getId();
        logger.info("Login successful!");
        return new UserLoginResponseDTO(userId);
    }

    /**
     * Service method for creating a user
     * @param dto Validated dto containing the users information
     * @return the userId of the user logged in inside of a UserLoginResponseDTO
     */
    public LoginCredentialsDTO createUser(CreateUserDTO dto) {
        var loginCredentials = new LoginCredentialsDTO(dto.getEmail(), dto.getPassword());
        var newUser = new User(dto);

        // If email address is already registered
        if (!userRepository.findByEmail(newUser.getEmail()).isEmpty()) {
            throw new ExistingRegisteredEmailException();
        }

        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        addressRepository.save(newUser.getHomeAddress());
        userRepository.save(newUser);

        logger.info("Successful registration of user with ID: {}", newUser.getId());
        return loginCredentials;
    }

    /**
     * Service method for retrieving a user
     * @param id ID of the user to retrieve
     * @return the user data inside of a UserResponseDTO
     */
    public UserResponseDTO getUser(Integer id) {
        return new UserResponseDTO(userRepository.findById(id).orElseThrow(() -> new NoUserExistsException(id)));
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
            var exception = new ForbiddenActionException(
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


}
