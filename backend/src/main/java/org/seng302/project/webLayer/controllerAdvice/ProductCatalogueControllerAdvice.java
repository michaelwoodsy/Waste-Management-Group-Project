package org.seng302.project.webLayer.controllerAdvice;

import org.seng302.project.serviceLayer.exceptions.*;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.ForbiddenAdministratorActionException;
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
     * in ProductCatalogueController when there is no matching business.
     *
     * @return a 406 response with an appropriate message
     */
    @ExceptionHandler(NoBusinessExistsException.class)
    public ResponseEntity<String> businessDoesNotExist(NoBusinessExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    }

    /**
     * Exception thrown by the editProduct() function
     * in ProductCatalogueController when a there is no matching product.
     *
     * @return a 400 response with an appropriate message
     */
    @ExceptionHandler(NoProductExistsException.class)
    public ResponseEntity<String> productDoesNotExist(NoProductExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Exception thrown by the getBusinessesProducts() and newProduct() functions in ProductCatalogueController
     * when a user tries to perform a function when they are not an administrator.
     *
     * @return a 403 response with an appropriate message
     */
    @ExceptionHandler(ForbiddenAdministratorActionException.class)
    public ResponseEntity<String> forbiddenAdministratorAction(ForbiddenAdministratorActionException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    /**
     * Exception thrown by the newProduct() in ProductCatalogueController
     * when a user tries create a product without a product id
     *
     * @return a 400 response with an appropriate message
     */
    @ExceptionHandler(MissingProductIdException.class)
    public ResponseEntity<String> missingProductId(MissingProductIdException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Exception thrown by the newProduct() in ProductCatalogueController
     * when a user tries create a product without a product name
     *
     * @return a 400 response with an appropriate message
     */
    @ExceptionHandler(MissingProductNameException.class)
    public ResponseEntity<String> missingProductName(MissingProductNameException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Exception thrown by newProduct() and editProduct() in ProductCatalogueController
     * when a user tries create a product with an existing product id
     *
     * @return a 400 response with an appropriate message
     */
    @ExceptionHandler(ProductIdAlreadyExistsException.class)
    public ResponseEntity<String> productIdAlreadyExists(ProductIdAlreadyExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Exception thrown by the editProduct() in ProductCatalogueController
     * when a user tries to edit a product with a rrp that is not a number
     *
     * @return a 400 response with an appropriate message
     */
    @ExceptionHandler(IncorrectRRPFormatException.class)
    public ResponseEntity<String> productIdAlreadyExists(IncorrectRRPFormatException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Exception thrown by newProduct() and editProduct() in ProductCatalogueController
     * when a user tries to use a product id with invalid characters
     *
     * @return a 400 response with an appropriate message
     */
    @ExceptionHandler(InvalidProductIdCharactersException.class)
    public ResponseEntity<String> invalidProductIdCharacters(InvalidProductIdCharactersException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Exception thrown by the newProduct() and editProduct() functions in ProductCatalogueController
     * when a price field has a negative number in it.
     *
     * @return a 400 response with an appropriate message
     */
    @ExceptionHandler(InvalidPriceException.class)
    public ResponseEntity<String> invalidPrice(InvalidPriceException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
