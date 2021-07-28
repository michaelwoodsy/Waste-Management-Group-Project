package org.seng302.project.service_layer.exceptions;

public class ProductIdAlreadyExistsException extends RuntimeException {

    public ProductIdAlreadyExistsException() {
        super("ProductIdAlreadyExistsException: This product id is not unique and already exists.");
    }
}
