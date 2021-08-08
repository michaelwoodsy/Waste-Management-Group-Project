package org.seng302.project.service_layer.service;

import org.seng302.project.repository_layer.model.Address;
import org.seng302.project.repository_layer.model.Business;
import org.seng302.project.repository_layer.model.User;
import org.seng302.project.repository_layer.model.enums.BusinessType;
import org.seng302.project.repository_layer.repository.AddressRepository;
import org.seng302.project.repository_layer.repository.BusinessRepository;
import org.seng302.project.repository_layer.repository.UserRepository;
import org.seng302.project.repository_layer.specification.BusinessSpecifications;
import org.seng302.project.service_layer.dto.business.GetBusinessDTO;
import org.seng302.project.service_layer.dto.business.PostBusinessDTO;
import org.seng302.project.service_layer.dto.business.PutBusinessAdminDTO;
import org.seng302.project.service_layer.exceptions.*;
import org.seng302.project.service_layer.exceptions.business.BusinessNotFoundException;
import org.seng302.project.service_layer.exceptions.businessAdministrator.AdministratorAlreadyExistsException;
import org.seng302.project.service_layer.exceptions.businessAdministrator.CantRemoveAdministratorException;
import org.seng302.project.service_layer.exceptions.businessAdministrator.UserNotAdministratorException;
import org.seng302.project.service_layer.exceptions.register.UserUnderageException;
import org.seng302.project.service_layer.util.DateArithmetic;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class BusinessService {

    private static final Logger logger = LoggerFactory.getLogger(BusinessService.class.getName());
    private final BusinessRepository businessRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final ProductCatalogueService productCatalogueService;

    @Autowired
    public BusinessService(BusinessRepository businessRepository,
                           AddressRepository addressRepository,
                           UserRepository userRepository,
                           ProductCatalogueService productCatalogueService) {
        this.businessRepository = businessRepository;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.productCatalogueService = productCatalogueService;
    }


    /**
     * Checks if the user creating the business
     * is at least 16 years old
     * Throws an exception if they aren't
     *
     * @param currentUser the user we are checking the age of
     */
    private void checkBusinessCreatorAge(User currentUser) {
        Date dateOfBirthDate;
        var currentDate = new Date();
        try {
            dateOfBirthDate = new SimpleDateFormat("yyyy-MM-dd").parse(currentUser.getDateOfBirth());
        } catch (ParseException parseException) {
            var invalidDateException = new InvalidDateException();
            logger.warn(invalidDateException.getMessage());
            throw invalidDateException;
        } catch (Exception exception) {
            logger.error(String.format("Unexpected error while parsing date: %s", exception.getMessage()));
            throw exception;
        }

        if (DateArithmetic.getDiffYears(dateOfBirthDate, currentDate) < 16) {
            var underageException = new UserUnderageException("a business", 16);
            logger.warn(underageException.getMessage());
            throw underageException;
        }
    }

    /**
     * Creates a new business account.
     *
     * @param requestDTO DTO with fields for Business to be created
     */
    public Integer createBusiness(PostBusinessDTO requestDTO) {
        logger.info("Request to create business");

        try {
            var userEmail = "";
            String businessName = requestDTO.getName();
            String description = requestDTO.getDescription();
            var address = new Address(requestDTO.getAddress());
            String businessType = requestDTO.getBusinessType();
            Integer primaryAdministratorId = requestDTO.getPrimaryAdministratorId();

            var newBusiness = new Business(businessName, description, address, businessType, primaryAdministratorId);

            //If the primary administrator id is not an id of a user
            if (userRepository.findById(primaryAdministratorId).isEmpty()) {
                var exception = new NoUserExistsException(primaryAdministratorId);
                logger.error(exception.getMessage());
                throw exception;
            } else {
                Optional<User> currUser = userRepository.findById(primaryAdministratorId);
                currUser.ifPresent(newBusiness::addAdministrator);
                if (currUser.isPresent()) userEmail = currUser.get().getEmail();
            }

            var currentUser = userRepository.findByEmail(userEmail).get(0);
            checkBusinessCreatorAge(currentUser);

            addressRepository.save(address);
            businessRepository.save(newBusiness);
            logger.info("Successful creation of business {}", newBusiness.getId());
            return newBusiness.getId();
        } catch (NoUserExistsException | UserUnderageException | InvalidDateException handledException) {
            throw handledException;
        } catch (Exception unexpectedException) {
            logger.error(String.format("Unexpected error while creating business: %s", unexpectedException.getMessage()));
            throw unexpectedException;
        }
    }

    /**
     * Retrieve a specific business account.
     *
     * @param businessId ID of business to get information from.
     * @return the retrieved Business
     */
    public GetBusinessDTO getBusiness(Integer businessId) {
        logger.info("Request to get business {}", businessId);
        try {
            Optional<Business> business = businessRepository.findById(businessId);
            if (business.isEmpty()) {
                throw new BusinessNotFoundException(businessId);
            } else {
                return new GetBusinessDTO(business.get());
            }
        } catch (BusinessNotFoundException businessNotFoundException) {
            logger.warn(businessNotFoundException.getMessage());
            throw businessNotFoundException;
        } catch (Exception exception) {
            logger.error(String.format("Unexpected error while getting business: %s", exception.getMessage()));
            throw exception;
        }

    }

    /**
     * Checks a business exists with the ID given.
     * Throws and exception if no business exists.
     *
     * @param businessId Business Id to check.
     * @throws NotAcceptableException thrown if there is no business matching the ID
     * @return The business found with the matching ID.
     */
    public Business checkBusiness(Integer businessId) throws NotAcceptableException {
        Optional<Business> retrievedBusiness = businessRepository.findById(businessId);
        if (retrievedBusiness.isEmpty()) {
            String message = String.format("Business with ID %d does not exist", businessId);
            logger.warn(message);
            throw new NotAcceptableException(message);
        }
        return retrievedBusiness.get();
    }

    /**
     * Checks a user can perform admin actions on a business.
     * Throws an exception if they can't.
     *
     * @param appUser The current logged in user.
     * @param business The business to check the user can perform admin actions on.
     */
    public void checkUserCanDoBusinessAction(AppUserDetails appUser, Business business) throws ForbiddenException {
        User user = userRepository.findByEmail(appUser.getUsername()).get(0);
        if (!business.userCanDoAction(user)) {
            String message = String.format("User with id %d can not perform this action as they are not an administrator of business with id %d.",
                    user.getId(), business.getId());
            logger.warn(message);
            throw new ForbiddenException(message);
        }
    }

    /**
     * Service method to update business details.
     * The businesses products currencies will remain the same.
     *
     * @param requestDTO            DTO containing new business information
     * @param businessId            ID of the business to update
     * @param appUser               the currently logged in user
     */
    public void editBusiness(PostBusinessDTO requestDTO, Integer businessId, AppUserDetails appUser) {
        editBusiness(requestDTO, businessId, appUser, false);
    }

    /**
     * Service method to update business details
     *
     * @param requestDTO            DTO containing new business information
     * @param businessId            ID of the business to update
     * @param appUser               the currently logged in user
     * @param updateProductCurrency Determines if the currency of the businesses products should be updated to the new country
     */
    public void editBusiness(PostBusinessDTO requestDTO, Integer businessId, AppUserDetails appUser,
                             Boolean updateProductCurrency) {
        Business business = checkBusiness(businessId);
        checkUserCanDoBusinessAction(appUser, business);

        Integer newPrimaryAdminId = requestDTO.getPrimaryAdministratorId();
        if (!business.getPrimaryAdministratorId().equals(newPrimaryAdminId)) {
            checkAdminRequestMaker(appUser.getUsername(), business);
            Optional<User> newAdmin = userRepository.findById(newPrimaryAdminId);
            if (newAdmin.isEmpty()) {
                String message = String.format("No user with ID %d exists", newPrimaryAdminId);
                logger.warn(message);
                throw new BadRequestException(message);
            } else if (!business.userIsAdmin(newPrimaryAdminId)) {
                String message = String.format(
                        "User with ID %s must already be an admin of the business to be made primary admin",
                        newPrimaryAdminId
                );
                logger.warn(message);
                throw new BadRequestException(message);
            }
        }

        // Check if the country is changed
        if (requestDTO.getAddress() != null &&
                requestDTO.getAddress().getCountry() != null) {
            if (Boolean.TRUE.equals(updateProductCurrency)) {
                productCatalogueService.updateProductCurrency(businessId, requestDTO.getAddress().getCountry(), true);
            } else {
                productCatalogueService.updateProductCurrency(businessId, business.getAddress().getCountry(), false);
            }
        }

        business.updateBusiness(requestDTO);
        addressRepository.save(business.getAddress());
        businessRepository.save(business);
    }

    /**
     * Finds the person making a request to add/remove
     * a business admin
     * Throws an exception if they are not a primary admin of the business
     * or if they are not a GAA/DGAA
     */
    private void checkAdminRequestMaker(String appUserEmail, Business currBusiness) {
        var loggedInUser = userRepository.findByEmail(appUserEmail).get(0);

        if (!loggedInUser.getId().equals(currBusiness.getPrimaryAdministratorId()) && !loggedInUser.isGAA()) {
            var exception = new ForbiddenException(String.format("You can not perform this action as you are " +
                    "not the primary administrator of business with id %d.", currBusiness.getId()));
            logger.error(exception.getMessage());
            throw exception;
        }
    }

    /**
     * Adds an admin to a business
     *
     * @param requestDTO DTO with businessId, new admin userId
     *                   and AppUserDetails of person making request
     */
    public void addAdministrator(PutBusinessAdminDTO requestDTO) {

        try {
            Integer userId = requestDTO.getUserId();
            Integer businessId = requestDTO.getBusinessId();
            AppUserDetails appUser = requestDTO.getAppUser();
            logger.info("Request to add user with id {} as administrator for business with id {}",
                    userId, businessId);

            var currUser = userRepository.findById(userId).orElseThrow(() -> new NoUserExistsException(userId));
            var currBusiness = businessRepository.findById(businessId).orElseThrow(() -> new BusinessNotFoundException(businessId));
            checkAdminRequestMaker(appUser.getUsername(), currBusiness);

            //Checks if the user is already an administrator
            if (currBusiness.getAdministrators().contains(currUser)) {
                var exception = new AdministratorAlreadyExistsException(userId, businessId);
                logger.warn(exception.getMessage());
                throw exception;
            }
            currBusiness.addAdministrator(currUser);

            businessRepository.save(currBusiness);

            logger.info("Successfully added Administrator {} to business {}", currUser.getId(), currBusiness.getId());
        } catch (NoUserExistsException | BusinessNotFoundException |
                AdministratorAlreadyExistsException handledException) {
            throw handledException;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while adding new business administrator: %s", unhandledException.getMessage()));
            throw unhandledException;
        }
    }

    /**
     * Removes an admin from a business
     *
     * @param requestDTO DTO with businessId, userId of admin to remove
     *                   and AppUserDetails of person making request
     */
    public void removeAdministrator(PutBusinessAdminDTO requestDTO) {
        try {
            Integer userId = requestDTO.getUserId();
            Integer businessId = requestDTO.getBusinessId();
            logger.info("Request to remove user with id {} from administering business with id {}", userId, businessId);

            var currUser = userRepository.findById(userId).orElseThrow(() -> new NoUserExistsException(userId));
            var currBusiness = businessRepository.findById(businessId).orElseThrow(() -> new BusinessNotFoundException(businessId));
            checkAdminRequestMaker(requestDTO.getAppUser().getUsername(), currBusiness);

            //Checks if user trying to be removed is the primary administrator
            if (userId.equals(currBusiness.getPrimaryAdministratorId())) {
                throw new CantRemoveAdministratorException(userId, businessId);
            }

            //Checks if the user is not an administrator
            if (!currBusiness.getAdministrators().contains(currUser)) {
                throw new UserNotAdministratorException(userId, businessId);
            }

            currBusiness.removeAdministrator(currUser);
            businessRepository.save(currBusiness);

            logger.info("Successfully removed administrator {} from business {}", currUser.getId(), currBusiness.getId());

        } catch (BusinessNotFoundException | CantRemoveAdministratorException
                | UserNotAdministratorException | NoUserExistsException handledException) {
            logger.error(handledException.getMessage());
            throw handledException;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while removing business administrator: %s",
                    unhandledException.getMessage()));
            throw unhandledException;
        }
    }

    /**
     * Searches for business based on name and type
     * Regular expression for splitting search query taken from linked website
     *
     * @param searchQuery  query to match business by name
     * @param businessType the type of business to filter by
     * @see <a href="https://stackoverflow.com/questions/1757065/java-splitting-a-comma-separated-string-but-ignoring-commas-in-quotes">
     * https://stackoverflow.com/questions/1757065/java-splitting-a-comma-separated-string-but-ignoring-commas-in-quotes</a>
     */
    public List<Object> searchBusiness(String searchQuery, BusinessType businessType, Integer pageNumber, String sortBy) {
        List<Business> businesses;
        long totalCount;
        boolean sortASC;
        String checkedBusinessType;
        List<Object> sortChecker = checkSort(sortBy);
        sortASC = (boolean) sortChecker.get(0);
        sortBy = (String) sortChecker.get(1);
        searchQuery = searchQuery.toLowerCase(); // Convert search query to all lowercase.
        String[] conjunctions = searchQuery.split(" or (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // Split by OR
        Specification<Business> spec = null;

        for (String conjunction : conjunctions) {
            Specification<Business> newSpec = Specification.where(null);
            String[] names = conjunction.split("( and |\\s)(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // Split by AND
            // Iterate over the names in the search and check if they are quoted
            for (String name : names) {
                if (Pattern.matches("^\".*\"$", name)) {
                    name = name.replace("\"", "");
                    newSpec = newSpec.and(BusinessSpecifications.hasName(name));
                } else {
                    newSpec = newSpec.and(BusinessSpecifications.hasName(name))
                            .or(BusinessSpecifications.containsName(name));
                }
            }
            if (spec == null) {
                spec = newSpec;
            } else {
                spec = spec.or(newSpec);
            }
        }


        //Convert business type to string to be able to search database with it
        if(businessType != null && spec != null){
            checkedBusinessType = checkBusinessType(businessType);
            spec = spec.and(Specification.where(BusinessSpecifications.hasBusinessType(checkedBusinessType)));
        }
        // query the repository and get a Page object, from which you can get the content by doing page.getContent()
        if(!sortBy.isEmpty()){
            Page<Business> page = sortBusinessSearch(spec, sortBy, sortASC, pageNumber);
            totalCount = page.getTotalElements();
            businesses = page.getContent();
        } else {
            Pageable pageable = PageRequest.of(pageNumber, 10);
            Page<Business> page = businessRepository.findAll(spec, pageable);
            totalCount = page.getTotalElements();
            businesses = page.getContent();
        }

        logger.info("Retrieved {} businesses, showing {}", totalCount, businesses.size());
        return Arrays.asList(businesses.stream().map(GetBusinessDTO::new).collect(Collectors.toList()), totalCount);
    }

    /**
     * Helper function for business search, does the sorting
     * @param spec the specification used to search by
     * @param sortBy the column that is to be sorted
     * @param sortASC the direction of the sort
     * @return the sorted list of users searched for
     */
    public Page<Business> sortBusinessSearch(Specification<Business> spec, String sortBy, boolean sortASC, Integer pageNumber){
        if(sortASC){
            sortBy = sortBy.substring(0, sortBy.lastIndexOf("A"));
            Pageable pageable = PageRequest.of(pageNumber, 10, Sort.by(Sort.Order.asc(sortBy).ignoreCase()));
            return businessRepository.findAll(spec, pageable);
        } else {
            sortBy = sortBy.substring(0, sortBy.lastIndexOf("D"));
            Pageable pageable = PageRequest.of(pageNumber, 10, Sort.by(Sort.Order.desc(sortBy).ignoreCase()));
            return businessRepository.findAll(spec, pageable);
        }
    }

    /**
     * Checks if the sort column is one of the valid ones, if not replaces it with the empty string and no sorting is done
     * @param sortBy This is the string that will contain information about what column to sort by
     * @return A list that contains a boolean to describe if the sort is ascending or descending, and the sortby string
     * in case it has changed
     */
    public List<Object> checkSort(String sortBy){
        switch(sortBy){
            case "idASC": case "idDESC": case "nameASC": case "nameDESC": case "businessTypeASC": case "businessTypeDESC":
            case "addressASC": case  "addressDESC":
                break;
            default:
                sortBy = "";
        }
        return List.of(sortBy.contains("ASC"), sortBy);
    }

    public String checkBusinessType(BusinessType businessType){
        String returnedBusinessType;
        switch (businessType){
            case RETAIL_TRADE:
                returnedBusinessType = "Retail Trade";
                break;
            case CHARITABLE_ORG:
                returnedBusinessType = "Charitable organisation";
                break;
            case NON_PROFIT_ORG:
                returnedBusinessType = "Non-profit organisation";
                break;
            case ACCOMMODATION_AND_FOOD:
                returnedBusinessType = "Accommodation and Food Services";
                break;
            default:
                returnedBusinessType = "";
        }
        return returnedBusinessType;
    }
}


