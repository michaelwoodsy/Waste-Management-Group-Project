package org.seng302.project.serviceLayer.exceptions;


import org.seng302.project.webLayer.controller.ProductCatalogueController;

/**
 * Exception triggered when trying to create a product without an id.
 *
 * @see ProductCatalogueController
 */
public class InvalidQuantityException extends RuntimeException {
    public InvalidQuantityException() {
        super("InvalidQuantityException: quantity is missing, or is invalid.");
    }
}