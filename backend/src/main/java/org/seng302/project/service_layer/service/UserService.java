package org.seng302.project.service_layer.service;

import net.minidev.json.JSONObject;
import org.seng302.project.repository_layer.model.Address;
import org.seng302.project.repository_layer.model.Sale;
import org.seng302.project.repository_layer.model.User;
import org.seng302.project.repository_layer.repository.AddressRepository;
import org.seng302.project.repository_layer.repository.LikedSaleListingRepository;
import org.seng302.project.repository_layer.repository.SaleHistoryRepository;
import org.seng302.project.repository_layer.repository.UserRepository;
import org.seng302.project.repository_layer.specification.SalesReportSpecifications;
import org.seng302.project.repository_layer.specification.UserSpecifications;
import org.seng302.project.service_layer.dto.sales_report.GetSaleDTO;
import org.seng302.project.service_layer.dto.user.*;
import org.seng302.project.service_layer.exceptions.BadRequestException;
import org.seng302.project.service_layer.exceptions.ForbiddenException;
import org.seng302.project.service_layer.exceptions.NoUserExistsException;
import org.seng302.project.service_layer.exceptions.NotAcceptableException;
import org.seng302.project.service_layer.exceptions.dgaa.DGAARevokeAdminSelfException;
import org.seng302.project.service_layer.exceptions.dgaa.ForbiddenDGAAActionException;
import org.seng302.project.service_layer.exceptions.register.ExistingRegisteredEmailException;
import org.seng302.project.web_layer.authentication.AppUserDetails;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Provides logic for User objects
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class.getName());
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final LikedSaleListingRepository likedSaleListingRepository;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder passwordEncoder;
    private final SaleHistoryRepository saleHistoryRepository;

    @Autowired
    public UserService(UserRepository userRepository,
                       AddressRepository addressRepository,
                       LikedSaleListingRepository likedSaleListingRepository,
                       AuthenticationManager authenticationManager,
                       BCryptPasswordEncoder passwordEncoder,
                       SaleHistoryRepository saleHistoryRepository) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.likedSaleListingRepository = likedSaleListingRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.saleHistoryRepository = saleHistoryRepository;
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

        Specification<User> spec = null;

        for (String conjunction : conjunctions) {
            Specification<User> newSpec = Specification.where(null);

            String[] names = conjunction.split("( and |\\s)(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // Split by AND
            // Iterate over the names in the search and check if they are quoted
            for (String name : names) {
                if (Pattern.matches("^\".*\"$", name)) {
                    name = name.replace("\"", "");
                    newSpec = newSpec.and(UserSpecifications.hasName(name));
                } else {
                    newSpec = newSpec.and(UserSpecifications.hasName(name))
                            .or(UserSpecifications.containsName(name));
                }
            }
            if (spec == null) {
                spec = newSpec;
            } else {
                spec = spec.or(newSpec);
            }
        }

        // query the repository and get a Page object, from which you can get the content by doing page.getContent()
        if (!sortBy.isEmpty()) {
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

        List<GetUserDTO> userDTOs = new ArrayList<>();
        for (User user : users) {
            GetUserDTO dto = new GetUserDTO(user);
            dto.attachBusinessesAdministered(user);
            dto.attachLikedSaleListings(user);
            userDTOs.add(dto);
        }
        return Arrays.asList(userDTOs, totalCount);
    }

    /**
     * Service method for loging in a user
     *
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
     *
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
     *
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
     * Checks if a user with given ID exists.
     * If the user exists, returns the user. If the user does not exist, throws an exception
     *
     * @param userId ID of the user to check
     * @return the user with given ID if it exists
     * @throws NotAcceptableException if user with ID does not exist
     */
    public User checkUser(Integer userId) throws NotAcceptableException {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return user.get();
        } else {
            String message = String.format("User with ID %d does not exist", userId);
            throw new NotAcceptableException(message);
        }
    }

    /**
     * Service method for retrieving a user
     *
     * @param userId ID of the user to retrieve
     * @return the user data inside a GetUserDTO
     * @throws NotAcceptableException if the given user does not exist
     */
    public GetUserDTO getUser(Integer userId) throws NotAcceptableException {
        User user = checkUser(userId);
        GetUserDTO getUserDTO = new GetUserDTO(user);
        getUserDTO.attachBusinessesAdministered(user);
        getUserDTO.attachLikedSaleListings(user);
        for (var like : getUserDTO.getLikedSaleListings()) {
            Integer likes = likedSaleListingRepository.findAllByListingId(like.getListing().getId()).size();
            like.getListing().attachLikeData(likes, true);
        }
        return getUserDTO;
    }

    /**
     * Gets a user object from the database based on a provided email address
     *
     * @param email Email address to search user by
     * @return User object if found, null otherwise
     */
    public User getUserByEmail(String email) {
        List<User> returnedUsers = userRepository.findByEmail(email);
        if (returnedUsers.isEmpty()) {
            return null;
        } else if (returnedUsers.size() > 1) {
            String message = "Multiple users with the same email address found";
            logger.warn(message);
            throw new IllegalStateException(message);
        } else {
            return returnedUsers.get(0);
        }
    }

    /**
     * Service method for making a user a GAA
     *
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
     *
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
     * Checks if the logged-in user is able to perform actions on the user with id userId's account
     * If the user logged in has the ID userId, or the logged-in user is a GAA.
     *
     * @param userId  ID of the user account wanting to preform actions on.
     * @param appUser Details of the logged-in user
     */
    public void checkForbidden(Integer userId, AppUserDetails appUser) {
        var loggedInUser = userRepository.findByEmail(appUser.getUsername()).get(0);

        if (!loggedInUser.getId().equals(userId) && !loggedInUser.isGAA()) {
            String message = String.format("You are not authorised to make changes/view information for user with ID %d's account", userId);
            logger.error(message);
            throw new ForbiddenException(message);
        }
    }

    /**
     * Checks if the logged in user has the role "defaultGlobalApplicationAdmin", if not throw an exception
     *
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
     *
     * @param spec    the specification used to search by
     * @param sortBy  the column that is to be sorted
     * @param sortASC the direction of the sort
     * @return the sorted list of users searched for
     */
    public Page<User> sortUserSearch(Specification<User> spec, String sortBy, boolean sortASC, Integer pageNumber) {
        if (sortASC) {
            sortBy = sortBy.substring(0, sortBy.lastIndexOf("A"));
            Pageable pageable = PageRequest.of(pageNumber, 10, Sort.by(Sort.Order.asc(sortBy).ignoreCase()));
            return userRepository.findAll(spec, pageable);
        } else {
            sortBy = sortBy.substring(0, sortBy.lastIndexOf("D"));
            Pageable pageable = PageRequest.of(pageNumber, 10, Sort.by(Sort.Order.desc(sortBy).ignoreCase()));
            return userRepository.findAll(spec, pageable);
        }
    }

    /**
     * Checks if the sort column for users is one of the valid ones, if not replaces it with the empty string and no sorting is done
     *
     * @param sortBy This is the string that will contain information about what column to sort by
     * @return A list that contains a boolean to describe if the sort is ascending or descending, and the sortby string
     * in case it has changed
     */
    public List<Object> checkSort(String sortBy) {
        switch (sortBy) {
            case "idASC":
            case "idDESC":
            case "firstNameASC":
            case "firstNameDESC":
            case "middleNameASC":
            case "middleNameDESC":
            case "lastNameASC":
            case "lastNameDESC":
            case "emailASC":
            case "emailDESC":
            case "homeAddressASC":
            case "homeAddressDESC":
                break;
            default:
                sortBy = "";
        }
        return List.of(sortBy.contains("ASC"), sortBy);
    }

    /**
     * Gets a list of a user's purchases
     *
     * @param userId ID of the user to get purchases for
     * @return list of the user's purchases
     */
    public List<Object> getPurchaseHistory(Integer userId, Integer pageNumber, String sortBy) {
        Specification<Sale> spec = SalesReportSpecifications.purchasedByUser(userId);

        var sort = buildPurchaseSort(sortBy);

        Pageable pageable;
        if (sort != null) {
            pageable = PageRequest.of(pageNumber, 10, sort);
        } else {
            pageable = PageRequest.of(pageNumber, 10);
        }

        Page<Sale> page = saleHistoryRepository.findAll(spec, pageable);
        long totalCount = page.getTotalElements();
        List<Sale> purchases = page.getContent();
        List<GetSaleDTO> saleList = new ArrayList<>();

        for (Sale sale : purchases) {
            GetSaleDTO dto = new GetSaleDTO(sale);
            if (sale.getReview() != null){
                dto.attachReview(sale.getReview());
            }
            saleList.add(dto);
        }

        logger.info("Retrieved {} Purchases, showing {}", totalCount, purchases.size());
        return Arrays.asList(saleList, totalCount);
    }

    /**
     * Given a sort query string, returns a Sort object used to sort purchases by.
     *
     * @param sortQuery String query to sort by.
     * @return Sort object used to sort entries retrieved from the Sale History Repository
     */
    public Sort buildPurchaseSort(String sortQuery) {
        Sort sort = null;
        switch (sortQuery) {
            case "datePurchasedASC":
                sort = Sort.by(Sort.Order.asc("dateSold"));
                break;
            case "datePurchasedDESC":
                sort = Sort.by(Sort.Order.desc("dateSold"));
                break;
            case "productNameASC":
                sort = Sort.by(Sort.Order.asc("inventoryItem.product.name"));
                break;
            case "productNameDESC":
                sort = Sort.by(Sort.Order.desc("inventoryItem.product.name"));
                break;
            case "quantityASC":
                sort = Sort.by(Sort.Order.asc("quantity"));
                break;
            case "quantityDESC":
                sort = Sort.by(Sort.Order.desc("quantity"));
                break;
            case "priceASC":
                sort = Sort.by(Sort.Order.asc("price"));
                break;
            case "priceDESC":
                sort = Sort.by(Sort.Order.desc("price"));
                break;
            case "businessASC":
                sort = Sort.by(Sort.Order.asc("business.name"));
                break;
            case "businessDESC":
                sort = Sort.by(Sort.Order.desc("business.name"));
                break;
            default:
                break;
        }
        return sort;
    }

}
