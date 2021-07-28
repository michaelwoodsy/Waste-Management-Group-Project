package org.seng302.project.web_layer.controllerAdvice;

import org.seng302.project.service_layer.exceptions.*;
import org.seng302.project.service_layer.exceptions.business.BusinessNotFoundException;
import org.seng302.project.service_layer.exceptions.businessAdministrator.ForbiddenAdministratorActionException;
import org.seng302.project.service_layer.exceptions.product.NoProductExistsException;
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
    @ExceptionHandler(BusinessNotFoundException.class)
    public ResponseEntity<String> businessDoesNotExist(BusinessNotFoundException ex) {
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
     * Exception thrown by newProduct() and editProduct() in ProductCatalogueController
     * when a user tries create a product with an existing product id
     *
     * @return a 400 response with an appropriate message
     */
    @ExceptionHandler(ProductIdAlreadyExistsException.class)
    public ResponseEntity<String> productIdAlreadyExists(ProductIdAlreadyExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
