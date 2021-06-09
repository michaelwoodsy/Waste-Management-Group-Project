package org.seng302.project.service_layer.exceptions;


import org.seng302.project.web_layer.controller.ProductCatalogueController;

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