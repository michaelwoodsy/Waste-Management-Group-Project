package org.seng302.project.webLayer.controller;

import net.minidev.json.JSONObject;
import org.seng302.project.repositoryLayer.model.*;
import org.seng302.project.repositoryLayer.repository.BusinessRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.dto.AddBusinessDTO;
import org.seng302.project.serviceLayer.exceptions.*;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.AdministratorAlreadyExistsException;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.CantRemoveAdministratorException;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.ForbiddenPrimaryAdministratorActionException;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.UserNotAdministratorException;
import org.seng302.project.serviceLayer.service.BusinessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;


/**
 * REST controller for handling requests to do with businesses.
 */
@RestController
public class BusinessController {

    private final BusinessService businessService;

    private static final Logger logger = LoggerFactory.getLogger(BusinessController.class.getName());
    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;

    @Autowired
    public BusinessController(BusinessService businessService,
            BusinessRepository businessRepository,
                              UserRepository userRepository) {
        this.businessService = businessService;
        this.businessRepository = businessRepository;
        this.userRepository = userRepository;
    }


    /**
     * Creates a new business account.
     *
     * @param requestDTO DTO with fields for Business to be created
     */
    @PostMapping("/businesses")
    @ResponseStatus(HttpStatus.CREATED)
    public void createBusiness(@Valid @RequestBody AddBusinessDTO requestDTO) {
        businessService.createBusiness(requestDTO);
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
