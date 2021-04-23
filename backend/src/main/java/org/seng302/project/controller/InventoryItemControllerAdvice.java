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
public class InventoryItemControllerAdvice {

    /**
     * Exception thrown by the newInventoryItem() function in InventoryItemController
     * when there is no matching business.
     *
     * @return a 406 response with an appropriate message
     */
    @ExceptionHandler(NoBusinessExistsException.class)
    public ResponseEntity<String> businessDoesNotExist(NoBusinessExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    }

    /**
     * Exception thrown by the newInventoryItem() function in InventoryItemController
     * when a user tries to perform a function when they are not an administrator.
     *
     * @return a 403 response with an appropriate message
     */
    @ExceptionHandler(ForbiddenAdministratorActionException.class)
    public ResponseEntity<String> forbiddenAdministratorAction(ForbiddenAdministratorActionException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    /**
     * Exception thrown by the newInventoryItem() function in InventoryItemController
     * when a user tries create a inventory item without a product id
     *
     * @return a 400 response with an appropriate message
     */
    @ExceptionHandler(MissingProductIdException.class)
    public ResponseEntity<String> missingProductId(MissingProductIdException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Exception thrown by the newInventoryItem() function in InventoryItemController
     * when a user tries create a inventory item without a expiry date
     *
     * @return a 400 response with an appropriate message
     */
    @ExceptionHandler(MissingInventoryItemExpiryException.class)
    public ResponseEntity<String> missingInvenotryItemExpiry(MissingInventoryItemExpiryException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Exception thrown by the newInventoryItem() function in InventoryItemController
     * when the supplied product id does does not reference a product
     *
     * @return a 400 response with an appropriate message
     */
    @ExceptionHandler(NoProductExistsException.class)
    public ResponseEntity<String> productDoesNotExist(NoProductExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Exception thrown by the newInventoryItem() function in InventoryItemController
     * when a user tries create a inventory item without a quantity
     *
     * @return a 400 response with an appropriate message
     */
    @ExceptionHandler(MissingInventoryItemQuantityException.class)
    public ResponseEntity<String> missingQuantity(MissingInventoryItemQuantityException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Exception thrown by the newInventoryItem() function in InventoryItemController
     * when a date format is incorrect.
     *
     * @return a 400 response with an appropriate message
     */
    @ExceptionHandler(InvalidDateException.class)
    public ResponseEntity<String> invalidDateOfBirth(InvalidDateException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Exception thrown by the newInventoryItem() function in InventoryItemController
     * when an item has passed its expiry date.
     *
     * @return a 400 response with an appropriate message
     */
    @ExceptionHandler(ItemExpiredException.class)
    public ResponseEntity<String> itemExpired(ItemExpiredException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Exception thrown by the newInventoryItem() function in InventoryItemController
     * when an items manufacture date is in the future.
     *
     * @return a 400 response with an appropriate message
     */
    @ExceptionHandler(InvalidManufactureDateException.class)
    public ResponseEntity<String> invalidManufactureDate(InvalidManufactureDateException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Exception thrown by the newInventoryItem() function in InventoryItemController
     * when an item has passed its sell by.
     *
     * @return a 400 response with an appropriate message
     */
    @ExceptionHandler(InvalidSellByDateException.class)
    public ResponseEntity<String> invalidSellByDate(InvalidSellByDateException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Exception thrown by the newInventoryItem() function in InventoryItemController
     * when an item has passed its best before date.
     *
     * @return a 400 response with an appropriate message
     */
    @ExceptionHandler(InvalidBestBeforeDateException.class)
    public ResponseEntity<String> invalidBestBeforeDate(InvalidBestBeforeDateException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
