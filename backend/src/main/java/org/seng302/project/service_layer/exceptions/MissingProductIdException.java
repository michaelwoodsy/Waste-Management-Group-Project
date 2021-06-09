package org.seng302.project.service_layer.exceptions;


import org.seng302.project.web_layer.controller.ProductCatalogueController;

/**
 * Exception triggered when trying to create a product without an id.
 *
 * @see ProductCatalogueController
 */
public class MissingProductIdException extends RuntimeException {
    public MissingProductIdException() {
        super("MissingProductIdException: product id is a mandatory field.");
    }
}