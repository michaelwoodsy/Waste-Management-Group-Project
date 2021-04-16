package org.seng302.project.controller;

import org.seng302.project.exceptions.ForbiddenAdministratorActionException;
import org.seng302.project.exceptions.MissingProductIdException;
import org.seng302.project.exceptions.MissingProductNameException;
import org.seng302.project.exceptions.NoBusinessExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Class that ProductCatalogueController uses for handling exceptions.
 */
@ControllerAdvice
public class ProductCatalogueControllerAdvice {

    /**
     * Exception thrown by the getBusinessesProducts() and newProduct() functions
     * in ProductCatalogueController when a there is no matching business.
     *
     * @return a 406 response with an appropriate message
     */
    @ExceptionHandler(NoBusinessExistsException.class)
    public ResponseEntity<String> businessDoesNotExist(NoBusinessExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    }

    /**
     * Exception thrown by the getBusinessesProducts() and newProduct() functions in BusinessController
     * when a user tries to perform a function when they are not the primary administrator.
     *
     * @return a 403 response with an appropriate message
     */
    @ExceptionHandler(ForbiddenAdministratorActionException.class)
    public ResponseEntity<String> forbiddenAdministratorAction(ForbiddenAdministratorActionException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    /**
     * Exception thrown by the newProduct() in BusinessController
     * when a user tries create a product without a product id
     *
     * @return a 400 response with an appropriate message
     */
    @ExceptionHandler(MissingProductIdException.class)
    public ResponseEntity<String> missingProductId(MissingProductIdException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Exception thrown by the newProduct() in BusinessController
     * when a user tries create a product without a product id
     *
     * @return a 400 response with an appropriate message
     */
    @ExceptionHandler(MissingProductNameException.class)
    public ResponseEntity<String> missingProductName(MissingProductNameException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
