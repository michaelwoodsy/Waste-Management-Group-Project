package org.seng302.project.exceptions;


/**
 * Exception triggered when trying to create a product without an id.
 *
 * @see org.seng302.project.controller.ProductCatalogueController
 */
public class InvalidInventoryItemQuantityException extends RuntimeException {
    public InvalidInventoryItemQuantityException() {
        super("InvalidInventoryItemQuantityException: inventory item quantity is missing, or is invalid.");
    }
}