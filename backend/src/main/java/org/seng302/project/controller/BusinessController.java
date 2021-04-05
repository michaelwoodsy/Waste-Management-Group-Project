package org.seng302.project.controller;

import net.minidev.json.JSONObject;
import org.seng302.project.exceptions.*;
import org.seng302.project.model.Business;
import org.seng302.project.model.BusinessRepository;
import org.seng302.project.model.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
        if(userRepository.findById(newBusiness.getPrimaryAdministratorId()).isPresent()) {
            NoUserExistsException exception = new NoUserExistsException(newBusiness.getPrimaryAdministratorId());
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

        //If any of the required fields are empty
        if (    newBusiness.getName().equals("") ||
                newBusiness.getAddress().equals("") ||
                newBusiness.getBusinessType().equals("")) {
            RequiredFieldsMissingException exception = new RequiredFieldsMissingException();
            logger.error(exception.getMessage());
            throw exception;
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
        } catch (NoBusinessExistsException exception) {
            logger.error(exception.getMessage());
            throw exception;
        }
    }

    /**
     * Adds an individual as an administrator for a business
     * Handles cases that may result in an error
     * @param id Id of the user to make an administrator
     */
    @PutMapping("/businesses/{id}/makeAdministrator")
    @ResponseStatus(HttpStatus.OK)
    public JSONObject addNewAdministrator(@PathVariable int id) {
        logger.info(String.format("Request to add user with id %d as administrator for business", id));

        //returns empty JSON object for now
        return new JSONObject();
    }

    /**
     * Removes an individual as an administrator for a business
     * Handles cases that may result in an error
     * @param id Id of the user to remove as administrator
     */
    @PutMapping("/businesses/{id}/removeAdministrator")
    @ResponseStatus(HttpStatus.OK)
    public JSONObject removeAdministrator(@PathVariable int id) {
        logger.info(String.format("Request to remove user with id %d as administrator for business", id));

        //returns empty JSON object for now
        return new JSONObject();
    }

}
