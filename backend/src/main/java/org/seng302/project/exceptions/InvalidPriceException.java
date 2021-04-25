package org.seng302.project.exceptions;


/**
 * Exception triggered when trying to create a product without an id.
 *
 * @see org.seng302.project.controller.ProductCatalogueController
 */
public class InvalidPriceException extends RuntimeException {
    public InvalidPriceException(String field) {
        super(String.format("InvalidPriceException: %s can not be negative.", field));
    }
}