package org.seng302.project.serviceLayer.exceptions;

import org.seng302.project.webLayer.controller.InventoryItemController;

/**
 * Exception triggered when inventory item does not exist.
 *
 * @see InventoryItemController
 */
public class NoInventoryItemExistsException extends RuntimeException {

    public NoInventoryItemExistsException(Integer itemId, Integer businessId) {
        super(String.format("NoInventoryItemExistsException: no inventory item with id %d exists in business with id %d.", itemId, businessId));
    }

}
