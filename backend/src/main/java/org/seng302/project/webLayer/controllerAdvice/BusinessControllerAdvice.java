package org.seng302.project.webLayer.controllerAdvice;


import org.seng302.project.serviceLayer.exceptions.*;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.AdministratorAlreadyExistsException;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.CantRemoveAdministratorException;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.ForbiddenPrimaryAdministratorActionException;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.UserNotAdministratorException;
import org.seng302.project.serviceLayer.exceptions.register.InvalidAddressException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Class that BusinessController uses for handling exceptions.
 */
@ControllerAdvice
public class BusinessControllerAdvice {

    /**
     * Exception thrown by the createBusiness(), addNewAdministrator() and removeAdministrator() functions in BusinessController
     * when a there is no matching user.
     *
     * @return a 406 response with an appropriate message
     */
    @ExceptionHandler(NoUserExistsException.class)
    public ResponseEntity<String> userDoesNotExist(NoUserExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    }

    /**
     * Exception thrown by the createBusiness(), addNewAdministrator() and removeAdministrator() functions in BusinessController
     * when a there is no matching business.
     *
     * @return a 406 response with an appropriate message
     */
    @ExceptionHandler(NoBusinessExistsException.class)
    public ResponseEntity<String> businessDoesNotExist(NoBusinessExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    }

    /**
     * Exception thrown by the createBusiness() function in BusinessController
     * when a user tries to create a business with a required field that is an empty string.
     *
     * @return a 400 response with an appropriate message
     */
    @ExceptionHandler(RequiredFieldsMissingException.class)
    public ResponseEntity<String> requiredFieldsMissing(RequiredFieldsMissingException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Exception thrown by the createBusiness() function in BusinessController
     * when a user tries to register with an invalid address.
     *
     * @return a 400 response with an appropriate message
     */
    @ExceptionHandler(InvalidAddressException.class)
    public ResponseEntity<String> invalidAddress(InvalidAddressException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Exception thrown by the createBusiness() function in BusinessController
     * when a user tries to create a business with a business type that is not valid.
     *
     * @return a 400 response with an appropriate message
     */
    @ExceptionHandler(NoBusinessTypeExistsException.class)
    public ResponseEntity<String> invalidBusinessType(NoBusinessTypeExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Exception thrown by the addNewAdministrator() function in BusinessController
     * when a user tries to add an already existing administrator to a business.
     *
     * @return a 400 response with an appropriate message
     */
    @ExceptionHandler(AdministratorAlreadyExistsException.class)
    public ResponseEntity<String> administratorAlreadyExists(AdministratorAlreadyExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Exception thrown by the addNewAdministrator() and removeAdministrator() functions in BusinessController
     * when a user tries to perform a function when they are not the primary administrator.
     *
     * @return a 403 response with an appropriate message
     */
    @ExceptionHandler(ForbiddenPrimaryAdministratorActionException.class)
    public ResponseEntity<String> forbiddenPrimaryAdministratorAction(ForbiddenPrimaryAdministratorActionException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    /**
     * Exception thrown by the removeAdministrator() function in BusinessController
     * when a user tries to remove the primary administrator from administering a business.
     *
     * @return a 400 response with an appropriate message
     */
    @ExceptionHandler(CantRemoveAdministratorException.class)
    public ResponseEntity<String> cantRemoveAdministrator(CantRemoveAdministratorException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Exception thrown by the removeAdministrator() function in BusinessController
     * when a user tries to remove an administrator when they dont exist.
     *
     * @return a 400 response with an appropriate message
     */
    @ExceptionHandler(UserNotAdministratorException.class)
    public ResponseEntity<String> userNotAdministrator(UserNotAdministratorException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
