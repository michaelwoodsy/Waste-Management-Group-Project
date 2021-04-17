package org.seng302.project.exceptions;


/**
 * Exception triggered when trying to create a product without an id.
 *
 * @see org.seng302.project.controller.ProductCatalogueController
 */
public class MissingProductNameException extends RuntimeException {
    public MissingProductNameException() {
        super("MissingProductNameException: product name is a mandatory field.");
    }
}

