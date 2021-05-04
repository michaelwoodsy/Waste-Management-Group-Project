package org.seng302.project.exceptions;


/**
 * Exception triggered when trying to create a product without an id.
 *
 * @see org.seng302.project.controller.ProductCatalogueController
 */
public class MissingInventoryItemExpiryException extends RuntimeException {
    public MissingInventoryItemExpiryException() {
        super("MissingInventoryItemExpiryException: inventory item expiry date is a mandatory field.");
    }
}