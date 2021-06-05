package org.seng302.project.serviceLayer.exceptions;


import org.seng302.project.webLayer.controller.ProductCatalogueController;

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