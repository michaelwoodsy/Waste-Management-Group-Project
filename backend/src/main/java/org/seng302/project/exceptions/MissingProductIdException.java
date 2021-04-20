package org.seng302.project.exceptions;


/**
 * Exception triggered when trying to create a product without an id.
 *
 * @see org.seng302.project.controller.ProductCatalogueController
 */
public class MissingProductIdException extends RuntimeException {
    public MissingProductIdException() {
        super("MissingProductIdException: product id is a mandatory field.");
    }
}