package org.seng302.project.service_layer.exceptions;


import org.seng302.project.web_layer.controller.ProductCatalogueController;

/**
 * Exception triggered when trying to create a product without an id.
 *
 * @see ProductCatalogueController
 */
public class MissingInventoryItemExpiryException extends RuntimeException {
    public MissingInventoryItemExpiryException() {
        super("MissingInventoryItemExpiryException: inventory item expiry date is a mandatory field.");
    }
}