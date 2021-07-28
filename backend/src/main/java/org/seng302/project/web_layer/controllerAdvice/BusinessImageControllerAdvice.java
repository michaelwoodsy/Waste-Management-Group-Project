package org.seng302.project.web_layer.controllerAdvice;

import org.seng302.project.service_layer.exceptions.business.NoBusinessExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Class that BusinessImageController uses for handling exceptions.
 */
@ControllerAdvice
public class BusinessImageControllerAdvice {

    /**
     * Exception thrown by the addBusinessImage() method when the imageId
     * given doesn't match any of the business' images.
     *
     * @return a 406 response with an appropriate message
     */
    @ExceptionHandler(NoBusinessExistsException.class)
    public ResponseEntity<String> noBusinessWithId(NoBusinessExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    }
}
