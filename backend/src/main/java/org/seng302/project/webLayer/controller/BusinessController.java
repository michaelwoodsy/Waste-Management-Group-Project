package org.seng302.project.webLayer.controller;

import net.minidev.json.JSONObject;
import org.seng302.project.repositoryLayer.model.*;
import org.seng302.project.repositoryLayer.repository.AddressRepository;
import org.seng302.project.repositoryLayer.repository.BusinessRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.exceptions.*;
import org.seng302.project.serviceLayer.util.DateArithmetic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

/**
 * REST controller for handling requests to do with businesses.
 */
@RestController
public class BusinessController {

    private static final Logger logger = LoggerFactory.getLogger(BusinessController.class.getName());
    private final BusinessRepository businessRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Autowired
    public BusinessController(BusinessRepository businessRepository,
                              AddressRepository addressRepository,
                              UserRepository userRepository) {
        this.businessRepository = businessRepository;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }


    /**
     * Creates a new business account.
     * Handles cases that may result in an error.
     *
     * @param newBusiness request body in the form of a User object
     */
    @PostMapping("/businesses")
    @ResponseStatus(HttpStatus.CREATED)
    public void createBusiness(@RequestBody Business newBusiness) {
        logger.info("Request to create business");
        try {

            String userEmail = "";

            //If the primary administrator id is not an id of a user
            if (userRepository.findById(newBusiness.getPrimaryAdministratorId()).isEmpty()) {
                NoUserExistsException exception = new NoUserExistsException(newBusiness.getPrimaryAdministratorId());
                logger.error(exception.getMessage());
                throw exception;
            } else {
                Optional<User> currUser = userRepository.findById(newBusiness.getPrimaryAdministratorId());
                currUser.ifPresent(newBusiness::addAdministrator);
                if(currUser.isPresent()) userEmail = currUser.get().getEmail();
            }

            User currentUser = userRepository.findByEmail(userEmail).get(0);

            //If the current user is less than 16 years old
            Date dateOfBirthDate;
            Date currentDate = new Date();
            try {
                dateOfBirthDate = new SimpleDateFormat("yyyy-MM-dd").parse(currentUser.getDateOfBirth());
            } catch (ParseException parseException) {
                InvalidDateException invalidDateException = new InvalidDateException();
                logger.warn(invalidDateException.getMessage());
                throw invalidDateException;
            } catch (Exception exception) {
                logger.error(String.format("Unexpected error while parsing date: %s", exception.getMessage()));
                throw exception;
            }

            if (DateArithmetic.getDiffYears(dateOfBirthDate, currentDate) < 16) {
                UserUnderageException underageException = new UserUnderageException("a business", 16);
                logger.warn(underageException.getMessage());
                throw underageException;
            }


            //If any of the required fields are empty
            if (newBusiness.getName().equals("") ||
                    newBusiness.getAddress().getCountry().equals("") ||
                    newBusiness.getBusinessType().equals("")) {
                RequiredFieldsMissingException exception = new RequiredFieldsMissingException();
                logger.error(exception.getMessage());
                throw exception;
            }

            //Check if address has a street number with no street name
            if (    (newBusiness.getAddress().getStreetName() == null || newBusiness.getAddress().getStreetName().equals("")) &&
                    (newBusiness.getAddress().getStreetNumber() != null && !newBusiness.getAddress().getStreetNumber().equals(""))) {
                InvalidAddressException addressException = new InvalidAddressException();
                logger.error(addressException.getMessage());
                throw addressException;
            }

            // If business type is not one of the specified business types
            if (!newBusiness.getBusinessType().equals("Accommodation and Food Services") &&
                    !newBusiness.getBusinessType().equals("Retail Trade") &&
                    !newBusiness.getBusinessType().equals("Charitable organisation") &&
                    !newBusiness.getBusinessType().equals("Non-profit organisation")) {
                NoBusinessTypeExistsException exception = new NoBusinessTypeExistsException(newBusiness.getBusinessType());
                logger.error(exception.getMessage());
                throw exception;
            }
            addressRepository.save(newBusiness.getAddress());
            businessRepository.save(newBusiness);
            logger.info(String.format("Successful creation of business %d", newBusiness.getId()));
        } catch (NoUserExistsException | RequiredFieldsMissingException | NoBusinessTypeExistsException handledException) {
            throw handledException;
        } catch (Exception unexpectedException) {
            logger.error(String.format("Unexpected error while creating business: %s", unexpectedException.getMessage()));
            throw unexpectedException;
        }
    }

    /**
     * Retrieve a specific business account.
     * Handles cases that may result in an error
     *
     * @param id ID of business to get information from.
     * @return Response back to client encompassing status code, headers, and body.
     */
    @GetMapping("/businesses/{id}")
    public Business getBusiness(@PathVariable int id) {

        logger.info(String.format("Request to get business %d", id));
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
     * Adds an individual as an administrator for a business
     * Handles cases that may result in an error
     *
     * @param id   Id of the business to add an administrator to
     * @param json request body JSON. Contains the id of the user to make an administrator
     */
    @PutMapping("/businesses/{id}/makeAdministrator")
    @ResponseStatus(HttpStatus.OK)
    public void addNewAdministrator(@PathVariable int id, @RequestBody JSONObject json, Principal userAuth) {
        try {
            int userId = (int) json.getAsNumber("userId");
            logger.info(String.format("Request to add user with id %d as administrator for business with id %d", userId, id));

            User currUser = userRepository.findById(userId).orElseThrow(() -> new NoUserExistsException(userId));
            Business currBusiness = businessRepository.findById(id).orElseThrow(() -> new NoBusinessExistsException(id));
            User loggedInUser = userRepository.findByEmail(userAuth.getName()).get(0);

            //Checks if the user preforming the action is the primary administrator of the business or a GAA
            if (!loggedInUser.getId().equals(currBusiness.getPrimaryAdministratorId()) && !loggedInUser.isGAA()) {
                ForbiddenPrimaryAdministratorActionException exception = new ForbiddenPrimaryAdministratorActionException(id);
                logger.error(exception.getMessage());
                throw exception;
            }

            //Checks if the user us already an administrator
            if (currBusiness.getAdministrators().contains(currUser)) {
                AdministratorAlreadyExistsException exception = new AdministratorAlreadyExistsException(userId, id);
                logger.warn(exception.getMessage());
                throw exception;
            }

            currBusiness.addAdministrator(currUser);

            businessRepository.save(currBusiness);

            logger.info(String.format("Successfully added Administrator %d to business %d", currUser.getId(), currBusiness.getId()));
        } catch (NoUserExistsException | NoBusinessExistsException | ForbiddenPrimaryAdministratorActionException |
                AdministratorAlreadyExistsException handledException) {
            throw handledException;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while adding new business administrator: %s", unhandledException.getMessage()));
            throw unhandledException;
        }
    }

    /**
     * Removes an individual as an administrator for a business
     * Handles cases that may result in an error
     *
     * @param id Id of the user to remove as administrator
     */
    @PutMapping("/businesses/{id}/removeAdministrator")
    @ResponseStatus(HttpStatus.OK)
    public void removeAdministrator(@PathVariable int id, @RequestBody JSONObject json, Principal userAuth) {
        try {
            int userId = (int) json.getAsNumber("userId");
            logger.info(String.format("Request to remove user with id %d from administering business with id %d", userId, id));

            User currUser = userRepository.findById(userId).orElseThrow(() -> new NoUserExistsException(userId));
            Business currBusiness = businessRepository.findById(id).orElseThrow(() -> new NoBusinessExistsException(id));
            User loggedInUser = userRepository.findByEmail(userAuth.getName()).get(0);

            //Checks if the user preforming the action is the primary administrator of the business or a GAA
            if (!loggedInUser.getId().equals(currBusiness.getPrimaryAdministratorId()) && !loggedInUser.isGAA()) {
                ForbiddenPrimaryAdministratorActionException exception = new ForbiddenPrimaryAdministratorActionException(id);
                logger.error(exception.getMessage());
                throw exception;
            }

            //Checks if user trying to be removed is the primary administrator
            if (userId == currBusiness.getPrimaryAdministratorId()) {
                CantRemoveAdministratorException exception = new CantRemoveAdministratorException(userId, id);
                logger.error(exception.getMessage());
                throw exception;
            }

            //Checks if the user is not an administrator
            if (!currBusiness.getAdministrators().contains(currUser)) {
                UserNotAdministratorException exception = new UserNotAdministratorException(userId, id);
                logger.error(exception.getMessage());
                throw exception;
            }

            try {
                currBusiness.removeAdministrator(currUser);
            } catch (NoUserExistsException exception) {
                logger.error(exception.getMessage());
                throw exception;
            }

            businessRepository.save(currBusiness);

            logger.info(String.format("Successfully removed administrator %d from business %d", currUser.getId(), currBusiness.getId()));

        } catch (NoBusinessExistsException | ForbiddenPrimaryAdministratorActionException | CantRemoveAdministratorException
                | UserNotAdministratorException | NoUserExistsException handledException) {
            throw handledException;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while removing business administrator: %s", unhandledException.getMessage()));
            throw unhandledException;
        }
    }

}
