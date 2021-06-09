package org.seng302.project.web_layer.controllerAdvice;

import org.seng302.project.service_layer.exceptions.product.ProductImageNotFoundException;
import org.seng302.project.service_layer.exceptions.product.ProductNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


/**
 * Class that ProductImagesController uses for handling exceptions.
 */
@ControllerAdvice
public class ProductImagesControllerAdvice {

    /**
     * Exception thrown by the setPrimaryImage() method when the imageId
     * given doesn't match any of the product's images.
     *
     * @return a 406 response with an appropriate message
     */
    @ExceptionHandler(ProductImageNotFoundException.class)
    public ResponseEntity<String> noProductImageWithId(ProductImageNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    }


    /**
     * Exception thrown by the setPrimaryImage() method when
     * product is not found
     * @return a 406 response with an appropriate message
     */
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<String> productNotFound(ProductNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    }
}
