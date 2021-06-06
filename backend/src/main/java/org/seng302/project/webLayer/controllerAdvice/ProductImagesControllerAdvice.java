package org.seng302.project.webLayer.controllerAdvice;

import org.seng302.project.serviceLayer.exceptions.productImages.NoProductImageWithIdException;
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
    @ExceptionHandler(NoProductImageWithIdException.class)
    public ResponseEntity<String> noProductImageWithId(NoProductImageWithIdException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    }
}
