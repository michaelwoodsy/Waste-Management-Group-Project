package org.seng302.project.service_layer.exceptions.business;

/**
 * Exception thrown by addImage() method in BusinessImageController
 * when request is made with an image file that is invalid
 */
public class BusinessImageInvalidException extends RuntimeException {
    public BusinessImageInvalidException() {
        super("BusinessImageInvalidException: Image supplied is of invalid type");
    }
}
