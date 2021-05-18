package org.seng302.project.controller;

import org.seng302.project.exceptions.*;
import org.seng302.project.exceptions.dgaa.DGAARevokeAdminSelfException;
import org.seng302.project.exceptions.dgaa.ForbiddenDGAAActionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Class that UserController uses for handling exceptions.
 */
@ControllerAdvice
public class UserControllerAdvice {

    /**
     * Exception thrown by the authenticate() function in UserController
     * when a user tries logging in with incorrect username/password.
     *
     * @return a 400 response with an appropriate message
     */
    @ExceptionHandler(InvalidLoginException.class)
    public ResponseEntity<String> incorrectCredentials(InvalidLoginException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Exception thrown by the createUser() function in UserController
     * when a user tries to register with an invalid email.
     *
     * @return a 400 response with an appropriate message
     */
    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<String> invalidEmail(InvalidEmailException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Exception thrown by the createUser() function in UserController
     * when a user tries to register with an invalid date of birth.
     *
     * @return a 400 response with an appropriate message
     */
    @ExceptionHandler(InvalidDateException.class)
    public ResponseEntity<String> invalidDateOfBirth(InvalidDateException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Exception thrown by the createUser() function in UserController
     * when a user tries to register with an invalid phone number.
     *
     * @return a 400 response with an appropriate message
     */
    @ExceptionHandler(InvalidPhoneNumberException.class)
    public ResponseEntity<String> invalidPhoneNumber(InvalidPhoneNumberException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Exception thrown by the createUser() function in UserController
     * when a user tries to register with an invalid address.
     *
     * @return a 400 response with an appropriate message
     */
    @ExceptionHandler(InvalidAddressException.class)
    public ResponseEntity<String> invalidAddress(InvalidAddressException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Exception thrown by the createUser() function in UserController
     * when a user tries to register with a birth date less than 13 years ago.
     *
     * @return a 400 response with an appropriate message
     */
    @ExceptionHandler(UserUnderageException.class)
    public ResponseEntity<String> userUnderage(UserUnderageException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Exception thrown by the createUser() function in UserController
     * when a user tries to register with a required field that is an empty string.
     *
     * @return a 400 response with an appropriate message
     */
    @ExceptionHandler(RequiredFieldsMissingException.class)
    public ResponseEntity<String> requiredFieldsMissing(RequiredFieldsMissingException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Exception thrown by the createUser() function in UserController
     * when a user tries to register with an existing email.
     *
     * @return a 409 response with an appropriate message
     */
    @ExceptionHandler(ExistingRegisteredEmailException.class)
    public ResponseEntity<String> userEmailAlreadyInUse(ExistingRegisteredEmailException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    /**
     * Exception thrown by the getUser() function in UserController
     * when a there is no matching user.
     *
     * @return a 406 response with an appropriate message
     */
    @ExceptionHandler(NoUserExistsException.class)
    public ResponseEntity<String> userDoesNotExist(NoUserExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<String> invalidPassword(InvalidPasswordException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }


    /**
     * Exception thrown by the dgaaMakeAdmin() and dgaaRevokeAdmin() methods
     * when the user calling the endpoint is not the DGAA
     *
     * @return  a 403 response with an appropriate message
     */
    @ExceptionHandler(ForbiddenDGAAActionException.class)
    public ResponseEntity<String> forbiddenDGAAAction(ForbiddenDGAAActionException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    /**
     * Exception thrown by the dgaaRevokeAdmin() method
     * when DGAA tries to revoke their own admin rights
     *
     * @return  a 409 response with an appropriate message
     */
    @ExceptionHandler(DGAARevokeAdminSelfException.class)
    public ResponseEntity<String> dgaaRevokeAdminSelf(DGAARevokeAdminSelfException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

}
