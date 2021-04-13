package org.seng302.project.controller;

import net.minidev.json.JSONObject;
import org.seng302.project.exceptions.*;
import org.seng302.project.model.Business;
import org.seng302.project.model.BusinessRepository;
import org.seng302.project.model.User;
import org.seng302.project.model.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.*;

/**
 * REST controller for handling requests to do with businesses.
 */
@RestController
public class BusinessController {

    private static final Logger logger = LoggerFactory.getLogger(BusinessController.class.getName());
    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;

    @Autowired
    public BusinessController(BusinessRepository businessRepository, UserRepository userRepository) {
        this.businessRepository = businessRepository;
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

        //If the primary administrator id is not an id of a user
        if(userRepository.findById(newBusiness.getPrimaryAdministratorId()).isEmpty()) {
            NoUserExistsException exception = new NoUserExistsException(newBusiness.getPrimaryAdministratorId());
            logger.error(exception.getMessage());
            throw exception;
        } else {
            Optional<User> currUser = userRepository.findById(newBusiness.getPrimaryAdministratorId());
            currUser.ifPresent(newBusiness::addAdministrator);
        }

        //If any of the required fields are empty
        if (    newBusiness.getName().equals("") ||
                newBusiness.getAddress().equals("") ||
                newBusiness.getBusinessType().equals("")) {
            RequiredFieldsMissingException exception = new RequiredFieldsMissingException();
            logger.error(exception.getMessage());
            throw exception;
        }

        //If business type is not one of the specified business types
        if(!newBusiness.getBusinessType().equals("Accommodation and Food Services") &&
                !newBusiness.getBusinessType().equals("Retail Trade") &&
                !newBusiness.getBusinessType().equals("Charitable organisation") &&
                !newBusiness.getBusinessType().equals("Non-profit organisation")) {
            NoBusinessTypeExistsException exception = new NoBusinessTypeExistsException(newBusiness.getBusinessType());
            logger.error(exception.getMessage());
            throw exception;
        }

        businessRepository.save(newBusiness);
        logger.info(String.format("Successful creation of business %d", newBusiness.getId()));
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
            Business currBusiness = businessRepository.findById(id).orElseThrow(() -> new NoBusinessExistsException(id));

            //Do this so the return is not an infinite loop of businesses and users
            for (User user: currBusiness.getAdministrators()) {
                user.setBusinessesAdministered(new ArrayList<>());
            }

            return currBusiness;
        } catch (NoBusinessExistsException exception) {
            logger.error(exception.getMessage());
            throw exception;
        }
    }

    /**
     * Adds an individual as an administrator for a business
     * Handles cases that may result in an error
     * @param id Id of the business to add an administrator to
     * @param json request body JSON. Contains the id of the user to make an administrator
     */
    @PutMapping("/businesses/{id}/makeAdministrator")
    @ResponseStatus(HttpStatus.OK)
    public void addNewAdministrator(@PathVariable int id, @RequestBody JSONObject json) {
        int userId = (int) json.getAsNumber("userId");
        logger.info(String.format("Request to add user with id %d as administrator for business with id %d", userId, id));

        User currUser = userRepository.findById(userId).orElseThrow(() -> new NoUserExistsException(userId));
        Business currBusiness = businessRepository.findById(id).orElseThrow(() -> new NoBusinessExistsException(id));

        //Checks if the user preforming the action is the primary administrator of the business
        if(CurrentUserController.GetInstance().getId(RequestContextHolder.currentRequestAttributes().getSessionId()) != currBusiness.getPrimaryAdministratorId()) {
            ForbiddenAdministratorActionException exception = new ForbiddenAdministratorActionException(id);
            logger.error(exception.getMessage());
            throw exception;
        }

        //Checks if the user us already an administrator
        if (currBusiness.getAdministrators().contains(currUser)) {
            AdministratorAlreadyExistsException exception = new AdministratorAlreadyExistsException(userId, id);
            logger.error(exception.getMessage());
            throw exception;
        }

        currBusiness.addAdministrator(currUser);

        businessRepository.save(currBusiness);

        logger.info(String.format("Successfully added Administrator %d to business %d", currUser.getId(), currBusiness.getId()));
    }

    /**
     * Removes an individual as an administrator for a business
     * Handles cases that may result in an error
     * @param id Id of the user to remove as administrator
     */
    @PutMapping("/businesses/{id}/removeAdministrator")
    @ResponseStatus(HttpStatus.OK)
    public void removeAdministrator(@PathVariable int id, @RequestBody JSONObject json) {
        logger.info(String.format("Request to remove user with id %d as administrator for business", id));

        int userId = (int) json.getAsNumber("userId");
        User currUser = userRepository.findById(userId).orElseThrow(() -> new NoUserExistsException(userId));


        Business currBusiness = businessRepository.findById(id).orElseThrow(() -> new NoBusinessExistsException(id));


        //Checks if the user preforming the action is the primary administrator of the business
        if(CurrentUserController.GetInstance().getId(RequestContextHolder.currentRequestAttributes().getSessionId()) != currBusiness.getPrimaryAdministratorId()) {
            ForbiddenAdministratorActionException exception = new ForbiddenAdministratorActionException(id);
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

        logger.info(String.format("Successfully removed Administrator %d from business %d", currUser.getId(), currBusiness.getId()));
    }

}
