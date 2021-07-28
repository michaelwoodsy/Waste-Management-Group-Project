package org.seng302.project.service_layer.exceptions;

import org.seng302.project.web_layer.controller.InventoryItemController;

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
