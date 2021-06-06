package org.seng302.project.serviceLayer.exceptions;


import org.seng302.project.webLayer.controller.ProductCatalogueController;

/**
 * Exception triggered when trying to create a product without an id.
 *
 * @see ProductCatalogueController
 */
public class InvalidPriceException extends RuntimeException {
    public InvalidPriceException(String field) {
        super(String.format("InvalidPriceException: %s can not be negative.", field));
    }
}