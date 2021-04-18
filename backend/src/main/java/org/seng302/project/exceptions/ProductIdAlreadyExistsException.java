package org.seng302.project.exceptions;

public class ProductIdAlreadyExistsException extends RuntimeException {

    public ProductIdAlreadyExistsException() {
        super("ProductIdAlreadyExistsException: This product id is not unique and already exists.");
    }
}
