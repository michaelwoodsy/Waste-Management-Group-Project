package org.seng302.project.web_layer.controller_advice;

import org.seng302.project.service_layer.exceptions.BadRequestException;
import org.seng302.project.service_layer.exceptions.ForbiddenException;
import org.seng302.project.service_layer.exceptions.NoUserExistsException;
import org.seng302.project.service_layer.exceptions.NotAcceptableException;
import org.seng302.project.service_layer.exceptions.businessAdministrator.ForbiddenAdministratorActionException;
import org.seng302.project.service_layer.exceptions.user.ForbiddenUserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.regex.Pattern;

/**
 * Class that all controllers use for handling exceptions.
 */
@ControllerAdvice
public class SharedControllerAdvice {


    /**
     * Handles sending a 400 response when a request DTO is invalid.
     *
     * @param ex the MethodArgumentNotValidException thrown by the DTO
     * @return a 400 response with a message about why the request was invalid
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> invalidRequestDTO(MethodArgumentNotValidException ex) {

        var responseString = "";

        //Gets the useful bit of the error message e.g. "Product name is a mandatory field"
        var pattern = Pattern.compile("]]; default message \\[(.*?)]");
        var matcher = pattern.matcher(ex.getMessage());
        if (matcher.find()) {
            responseString = matcher.group(1);
        }

        return new ResponseEntity<>("MethodArgumentNotValidException: " + responseString, HttpStatus.BAD_REQUEST);
    }


    /**
     * Handles sending a 400 response when a request is invalid.
     *
     * @param ex the BadRequestException thrown by the controller/service
     * @return a 400 response with a message about why the request was invalid
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> badRequestDTO(BadRequestException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles sending a 403 response when a request is made by a user who is not authorised to make it
     *
     * @param ex the ForbiddenException thrown by the controller/service
     * @return a 403 response with a message about why the request was forbidden
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<String> forbidden(ForbiddenException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    /**
     * Handles sending a 406 response when a request has unacceptable path variables
     *
     * @param ex the NotAcceptableException thrown by the controller/service
     * @return a 406 response with a message about what variable was not found
     */
    @ExceptionHandler(NotAcceptableException.class)
    public ResponseEntity<String> notAcceptable(NotAcceptableException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    }

    /**
     * Handles sending a 403 response when a request is forbidden.
     *
     * @param ex the ForbiddenUserException thrown by the controller/service
     * @return a 403 response with a message about why they are the wrong user for the request
     */
    @ExceptionHandler(ForbiddenUserException.class)
    public ResponseEntity<String> forbiddenUserRequestDTO(ForbiddenUserException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    /**
     * Exception thrown by BusinessController, UserController and UserImageController
     * when a there is no matching user.
     *
     * @return a 406 response with an appropriate message
     */
    @ExceptionHandler(NoUserExistsException.class)
    public ResponseEntity<String> userDoesNotExist(NoUserExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    }

    /**
     * Exception thrown when a user tries to perform a function when they are not an administrator.
     *
     * @return a 403 response with an appropriate message
     */
    @ExceptionHandler(ForbiddenAdministratorActionException.class)
    public ResponseEntity<String> forbiddenAdministratorAction(ForbiddenAdministratorActionException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }
}
