package org.seng302.project.exceptions;


/**
 * Exception triggered when trying to create a product without an id.
 *
 * @see org.seng302.project.controller.ProductCatalogueController
 */
public class MissingPriceException extends RuntimeException {
    public MissingPriceException() {
        super("MissingPriceException: price is missing or is invalid.");
    }
}