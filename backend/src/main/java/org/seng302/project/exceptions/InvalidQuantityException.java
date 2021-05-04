package org.seng302.project.exceptions;


/**
 * Exception triggered when trying to create a product without an id.
 *
 * @see org.seng302.project.controller.ProductCatalogueController
 */
public class InvalidQuantityException extends RuntimeException {
    public InvalidQuantityException() {
        super("InvalidQuantityException: quantity is missing, or is invalid.");
    }
}