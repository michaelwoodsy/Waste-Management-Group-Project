package org.seng302.project.service_layer.exceptions.product;

/**
 * Exception thrown by addImage() method in ProductImagesController
 * when request is made with an image file that is invalid
 */
public class ProductImageInvalidException extends RuntimeException {
    public ProductImageInvalidException() {
        super(String.format("ProductImageInvalidException: Image supplied is of invalid type"));
    }
}