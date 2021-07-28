package org.seng302.project.service_layer.exceptions;

import org.seng302.project.web_layer.controller.BusinessController;

/**
 * Exception triggered when business with id does not exist.
 *
 * @see BusinessController
 */
public class NotEnoughOfInventoryItemException extends RuntimeException {

    public NotEnoughOfInventoryItemException(Integer itemId, Integer quantity, Integer quantityUsed) {
        super(String.format("NotEnoughOfInventoryItemException: you do not have enough of item with id %d for this listing (you have %d, with %d used in other sale listings).", itemId, quantity, quantityUsed));
    }

}
