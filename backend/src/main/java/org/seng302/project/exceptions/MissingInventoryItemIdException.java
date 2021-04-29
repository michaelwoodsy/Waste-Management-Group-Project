package org.seng302.project.exceptions;


/**
 * Exception triggered when trying to create a product without an id.
 *
 * @see org.seng302.project.controller.ProductCatalogueController
 */
public class MissingInventoryItemIdException extends RuntimeException {
    public MissingInventoryItemIdException() {
        super("MissingInventoryItemIdException: inventory item id is a mandatory field.");
    }
}