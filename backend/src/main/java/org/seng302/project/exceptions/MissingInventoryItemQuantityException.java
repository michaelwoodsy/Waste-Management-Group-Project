package org.seng302.project.exceptions;


/**
 * Exception triggered when trying to create a product without an id.
 *
 * @see org.seng302.project.controller.ProductCatalogueController
 */
public class MissingInventoryItemQuantityException extends RuntimeException {
    public MissingInventoryItemQuantityException() {
        super("MissingInventoryItemQuantityException: inventory item quantity is a mandatory field.");
    }
}