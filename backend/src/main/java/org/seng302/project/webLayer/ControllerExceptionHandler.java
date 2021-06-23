package org.seng302.project.webLayer;

import org.seng302.project.serviceLayer.exceptions.NotAcceptableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Class that handles all exceptions that are thrown to controller classes.
 */
@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(NotAcceptableException.class)
    public ResponseEntity<String> notFound(NotAcceptableException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    }
}
