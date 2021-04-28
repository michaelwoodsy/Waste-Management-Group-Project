package org.seng302.project.exceptions;

/**
 * Exception triggered when inventory item does not exist.
 *
 * @see org.seng302.project.controller.InventoryItemController
 */
public class NoInventoryItemExistsException extends RuntimeException {

    public NoInventoryItemExistsException(Integer itemId, Integer businessId) {
        super(String.format("NoInventoryItemExistsException: no inventory item with id %d exists in business with id %d.", itemId, businessId));
    }

}
