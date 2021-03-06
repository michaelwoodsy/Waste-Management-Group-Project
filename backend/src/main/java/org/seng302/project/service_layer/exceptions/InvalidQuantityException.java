package org.seng302.project.service_layer.exceptions;


import org.seng302.project.web_layer.controller.ProductCatalogueController;

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