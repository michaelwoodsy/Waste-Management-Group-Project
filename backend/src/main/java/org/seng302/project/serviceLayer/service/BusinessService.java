package org.seng302.project.serviceLayer.service;

import org.seng302.project.repositoryLayer.model.Business;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.AddressRepository;
import org.seng302.project.repositoryLayer.repository.BusinessRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.dto.AddOrRemoveBusinessAdminDTO;
import org.seng302.project.serviceLayer.dto.AddBusinessDTO;
import org.seng302.project.serviceLayer.exceptions.InvalidDateException;
import org.seng302.project.serviceLayer.exceptions.NoBusinessExistsException;
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
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

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

            //If the current user is less than 16 years old
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
            return businessRepository.findById(id).orElseThrow(() -> new NoBusinessExistsException(id));
        } catch (NoBusinessExistsException noBusinessExistsException) {
            logger.warn(noBusinessExistsException.getMessage());
            throw noBusinessExistsException;
        } catch (Exception exception) {
            logger.error(String.format("Unexpected error while getting business: %s", exception.getMessage()));
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
            var currBusiness = businessRepository.findById(requestDTO.getBusinessId()).orElseThrow(() -> new NoBusinessExistsException(businessId));
            var loggedInUser = userRepository.findByEmail(requestDTO.getAppUser().getUsername()).get(0);

            //Checks if the user preforming the action is the primary administrator of the business or a GAA
            if (!loggedInUser.getId().equals(currBusiness.getPrimaryAdministratorId()) && !loggedInUser.isGAA()) {
                var exception = new ForbiddenPrimaryAdministratorActionException(businessId);
                logger.error(exception.getMessage());
                throw exception;
            }

            //Checks if the user us already an administrator
            if (currBusiness.getAdministrators().contains(currUser)) {
                var exception = new AdministratorAlreadyExistsException(userId, businessId);
                logger.warn(exception.getMessage());
                throw exception;
            }
            currBusiness.addAdministrator(currUser);

            businessRepository.save(currBusiness);

            logger.info("Successfully added Administrator {} to business {}", currUser.getId(), currBusiness.getId());
        } catch (NoUserExistsException | NoBusinessExistsException | ForbiddenPrimaryAdministratorActionException |
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
            var currBusiness = businessRepository.findById(businessId).orElseThrow(() -> new NoBusinessExistsException(businessId));
            var loggedInUser = userRepository.findByEmail(requestDTO.getAppUser().getUsername()).get(0);

            //Checks if the user preforming the action is the primary administrator of the business or a GAA
            if (!loggedInUser.getId().equals(currBusiness.getPrimaryAdministratorId()) && !loggedInUser.isGAA()) {
                var exception = new ForbiddenPrimaryAdministratorActionException(businessId);
                logger.error(exception.getMessage());
                throw exception;
            }

            //Checks if user trying to be removed is the primary administrator
            if (userId.equals(currBusiness.getPrimaryAdministratorId())) {
                var exception = new CantRemoveAdministratorException(userId, businessId);
                logger.error(exception.getMessage());
                throw exception;
            }

            //Checks if the user is not an administrator
            if (!currBusiness.getAdministrators().contains(currUser)) {
                var exception = new UserNotAdministratorException(userId, businessId);
                logger.error(exception.getMessage());
                throw exception;
            }

            try {
                currBusiness.removeAdministrator(currUser);
            } catch (UserNotAdministratorException exception) {
                logger.error(exception.getMessage());
                throw exception;
            }

            businessRepository.save(currBusiness);

            logger.info("Successfully removed administrator {} from business {}", currUser.getId(), currBusiness.getId());

        } catch (NoBusinessExistsException | ForbiddenPrimaryAdministratorActionException | CantRemoveAdministratorException
                | UserNotAdministratorException | NoUserExistsException handledException) {
            throw handledException;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while removing business administrator: %s",
                    unhandledException.getMessage()));
            throw unhandledException;
        }

    }
}
