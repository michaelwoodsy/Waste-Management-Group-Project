package org.seng302.project.controller;

import org.seng302.project.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Class that InventoryItemController uses for handling exceptions.
 */
@ControllerAdvice
public class SaleListingControllerAdvice {

    /**
     * Exception thrown by the getBusinessesListings() and  newBusinessesListing() functions in SalesListingController
     * when there is no matching business.
     *
     * @return a 406 response with an appropriate message
     */
    @ExceptionHandler(NoBusinessExistsException.class)
    public ResponseEntity<String> businessDoesNotExist(NoBusinessExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    }

    /**
     * Exception thrown by the getBusinessesListings() and  newBusinessesListing() functions in SalesListingController
     * when a user tries to perform a function when they are not an administrator.
     *
     * @return a 403 response with an appropriate message
     */
    @ExceptionHandler(ForbiddenAdministratorActionException.class)
    public ResponseEntity<String> forbiddenAdministratorAction(ForbiddenAdministratorActionException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    /**
     * Exception thrown by the newBusinessesListing() function in SalesListingController
     * when a user tries to create a sales listing without the inventoryItemId field
     *
     * @return a 400 response with an appropriate message
     */
    @ExceptionHandler(MissingInventoryItemIdException.class)
    public ResponseEntity<String> missingInventoryItem(MissingInventoryItemIdException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Exception thrown by the newBusinessesListing() function in SalesListingController
     * when a user tries to create a sales listing with an inventory item that doesn't exist
     *
     * @return a 400 response with an appropriate message
     */
    @ExceptionHandler(NoInventoryItemExistsException.class)
    public ResponseEntity<String> missingInventoryItem(NoInventoryItemExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

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

    /**
     * Exception thrown by the newBusinessesListing() function in SalesListingController
     * when a user tries to create a sales listing with a closing date that is in the past
     *
     * @return a 400 response with an appropriate message
     */
    @ExceptionHandler(InvalidClosesDateException.class)
    public ResponseEntity<String> pastClosingDate(InvalidClosesDateException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
