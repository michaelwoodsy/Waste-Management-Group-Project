package org.seng302.project.serviceLayer.exceptions;


import org.seng302.project.webLayer.controller.ProductCatalogueController;

/**
 * Exception triggered when trying to create a product without an id.
 *
 * @see ProductCatalogueController
 */
public class MissingPriceException extends RuntimeException {
    public MissingPriceException() {
        super("MissingPriceException: price is missing or is invalid.");
    }
}