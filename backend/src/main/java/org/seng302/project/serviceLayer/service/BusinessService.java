package org.seng302.project.serviceLayer.service;

import org.seng302.project.repositoryLayer.model.Business;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.model.types.BusinessType;
import org.seng302.project.repositoryLayer.repository.AddressRepository;
import org.seng302.project.repositoryLayer.repository.BusinessRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.repositoryLayer.specification.BusinessSpecifications;
import org.seng302.project.serviceLayer.dto.business.AddOrRemoveBusinessAdminDTO;
import org.seng302.project.serviceLayer.dto.business.AddBusinessDTO;
import org.seng302.project.serviceLayer.dto.business.SearchBusinessDTO;
import org.seng302.project.serviceLayer.exceptions.InvalidDateException;
import org.seng302.project.serviceLayer.exceptions.business.BusinessNotFoundException;
import org.seng302.project.serviceLayer.exceptions.NoUserExistsException;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.AdministratorAlreadyExistsException;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.CantRemoveAdministratorException;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.ForbiddenPrimaryAdministratorActionException;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.UserNotAdministratorException;
import org.seng302.project.serviceLayer.exceptions.register.UserUnderageException;
import org.seng302.project.serviceLayer.util.DateArithmetic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class BusinessService {

    private static final Logger logger = LoggerFactory.getLogger(BusinessService.class.getName());
    private final BusinessRepository businessRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Autowired
    public BusinessService(BusinessRepository businessRepository,
                              AddressRepository addressRepository,
                              UserRepository userRepository) {
        this.businessRepository = businessRepository;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }


    /**
     * Checks if the user creating the business
     * is at least 16 years old
     * Throws an exception if they aren't
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
    public void createBusiness(AddBusinessDTO requestDTO) {
        logger.info("Request to create business");

        try {
            var userEmail = "";
            String businessName = requestDTO.getName();
            String description = requestDTO.getDescription();
            var address = requestDTO.getAddress();
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
                if(currUser.isPresent()) userEmail = currUser.get().getEmail();
            }

            var currentUser = userRepository.findByEmail(userEmail).get(0);
            checkBusinessCreatorAge(currentUser);

            addressRepository.save(address);
            businessRepository.save(newBusiness);
            logger.info("Successful creation of business {}", newBusiness.getId());
        } catch (NoUserExistsException | UserUnderageException | InvalidDateException handledException) {
            throw handledException;
        } catch (Exception unexpectedException) {
            logger.error(String.format("Unexpected error while creating business: %s", unexpectedException.getMessage()));
            throw unexpectedException;
        }
    }


    /**
     *
     * Retrieve a specific business account.
     *
     * @param id ID of business to get information from.
     * @return the retrieved Business
     */
    public Business getBusiness(Integer id) {
        logger.info("Request to get business {}", id);
        try {
            return businessRepository.findById(id).orElseThrow(() -> new BusinessNotFoundException(id));
        } catch (BusinessNotFoundException businessNotFoundException) {
            logger.warn(businessNotFoundException.getMessage());
            throw businessNotFoundException;
        } catch (Exception exception) {
            logger.error(String.format("Unexpected error while getting business: %s", exception.getMessage()));
            throw exception;
        }

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
            var exception = new ForbiddenPrimaryAdministratorActionException(currBusiness.getId());
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
    public void addAdministrator(AddOrRemoveBusinessAdminDTO requestDTO) {

        try {
            Integer userId = requestDTO.getUserId();
            Integer businessId = requestDTO.getBusinessId();
            logger.info("Request to add user with id {} as administrator for business with id {}",
                    userId, businessId);

            var currUser = userRepository.findById(userId).orElseThrow(() -> new NoUserExistsException(userId));
            var currBusiness = businessRepository.findById(requestDTO.getBusinessId()).orElseThrow(() -> new BusinessNotFoundException(businessId));
            checkAdminRequestMaker(requestDTO.getAppUser().getUsername(), currBusiness);

            //Checks if the user is already an administrator
            if (currBusiness.getAdministrators().contains(currUser)) {
                var exception = new AdministratorAlreadyExistsException(userId, businessId);
                logger.warn(exception.getMessage());
                throw exception;
            }
            currBusiness.addAdministrator(currUser);

            businessRepository.save(currBusiness);

            logger.info("Successfully added Administrator {} to business {}", currUser.getId(), currBusiness.getId());
        } catch (NoUserExistsException | BusinessNotFoundException | ForbiddenPrimaryAdministratorActionException |
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
    public void removeAdministrator(AddOrRemoveBusinessAdminDTO requestDTO) {
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

        } catch (BusinessNotFoundException | ForbiddenPrimaryAdministratorActionException | CantRemoveAdministratorException
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
     * Filters businesses based on a given business type
     * @param retrievedBusinesses list of businesses found by specifications
     * @param businessType the business type to filter by e.g. RETAIL_TRADE
     */
    public List<Business> filterBusinesses(List<Business> retrievedBusinesses, BusinessType businessType) {
        //In DTO, businessType is either a valid type or null
        if (businessType != null) {
            var filteredBusinesses = new ArrayList<Business>();
            for (Business business: retrievedBusinesses) {
                if (businessType.matchesType(business.getBusinessType())) {
                    filteredBusinesses.add(business);
                }
            }
            return filteredBusinesses;
        } else {
            return retrievedBusinesses;
        }
    }

    /**
     * Searches for business based on name and type
     *
     * @param requestDTO DTO with a searchQuery to match a business name
     *                   and a (possibly empty) business type
     *
     * Regular expression for splitting search query taken from linked website.
     * @see <a href="https://stackoverflow.com/questions/1757065/java-splitting-a-comma-separated-string-but-ignoring-commas-in-quotes">
     * https://stackoverflow.com/questions/1757065/java-splitting-a-comma-separated-string-but-ignoring-commas-in-quotes</a>
     */
    public List<Business> searchBusiness(SearchBusinessDTO requestDTO) {
        logger.info("Request to search businesses with searchQuery: {} and businessType: {}",
                requestDTO.getSearchQuery(), requestDTO.getBusinessType());

        try {
            String searchQuery = requestDTO.getSearchQuery();
            List<Business> retrievedBusinesses;

            if (searchQuery.equals("")) {
                retrievedBusinesses = businessRepository.findAll();
                logger.info("Retrieved {} businesses", retrievedBusinesses.size());
            } else {
                Set<Business> result = new LinkedHashSet<>();

                searchQuery = searchQuery.toLowerCase(); // Convert search query to all lowercase.
                String[] conjunctions = searchQuery.split(" or (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // Split by OR

                for (String conjunction : conjunctions) {
                    Specification<Business> hasSpec = Specification.where(null);
                    Specification<Business> containsSpec = Specification.where(null);
                    var searchContains = false;
                    String[] names = conjunction.split("( and |\\s)(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // Split by AND

                    for (String name : names) {
                        if (Pattern.matches("^\".*\"$", name)) {
                            name = name.replace("\"", "");
                            hasSpec = hasSpec.and(BusinessSpecifications.hasName(name));
                        } else {
                            hasSpec = hasSpec.and(BusinessSpecifications.hasName(name));
                            containsSpec = containsSpec.and(BusinessSpecifications.containsName(name));
                            searchContains = true;
                        }
                    }

                    result.addAll(businessRepository.findAll(hasSpec));
                    if (searchContains) {
                        result.addAll(businessRepository.findAll(containsSpec));
                    }
                }

                logger.info("Retrieved {} businesses", result.size());
                retrievedBusinesses = new ArrayList<>(result);
            }

            return filterBusinesses(retrievedBusinesses, requestDTO.getBusinessType());

        } catch (Exception exception) {
            logger.error(String.format("Unexpected error while searching businesses: %s", exception.getMessage()));
            throw exception;
        }
    }
}
