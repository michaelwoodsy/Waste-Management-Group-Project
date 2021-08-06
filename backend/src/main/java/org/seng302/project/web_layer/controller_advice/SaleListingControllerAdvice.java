package org.seng302.project.web_layer.controller_advice;

import org.seng302.project.service_layer.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Class that SaleListingController uses for handling exceptions.
 */
@ControllerAdvice
public class SaleListingControllerAdvice {

    /**
     * Exception thrown by the newBusinessesListing() function in SalesListingController
     * when a user tries to create a sales listing with a quantity that is higher than what they have
     *
     * @return a 400 response with an appropriate message
     */
    @ExceptionHandler(NotEnoughOfInventoryItemException.class)
    public ResponseEntity<String> notEnoughItems(NotEnoughOfInventoryItemException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
