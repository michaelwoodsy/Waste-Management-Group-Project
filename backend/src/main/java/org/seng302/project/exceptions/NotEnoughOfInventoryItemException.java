package org.seng302.project.exceptions;

/**
 * Exception triggered when business with id does not exist.
 *
 * @see org.seng302.project.controller.BusinessController
 */
public class NotEnoughOfInventoryItemException extends RuntimeException {

    public NotEnoughOfInventoryItemException(Integer itemId, Integer quantity, Integer quantityUsed) {
        super(String.format("NotEnoughOfInventoryItemException: you do not have enough of item with id %d for this listing (you have %d, with %d used in other sale listings).", itemId, quantity, quantityUsed));
    }

}
