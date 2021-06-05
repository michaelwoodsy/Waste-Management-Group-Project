package org.seng302.project.serviceLayer.exceptions;


import org.seng302.project.webLayer.controller.ProductCatalogueController;

/**
 * Exception triggered when trying to create a product without an id.
 *
 * @see ProductCatalogueController
 */
public class MissingInventoryItemIdException extends RuntimeException {
    public MissingInventoryItemIdException() {
        super("MissingInventoryItemIdException: inventory item id is a mandatory field.");
    }
}