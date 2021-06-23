package org.seng302.project.serviceLayer.exceptions;



/**
 * Exception triggered when trying give an InventoryItem
 * or SaleListing a negative price.
 *
 * Can be deleted once the InventoryItemController and
 * SaleListingController is DTOs with the @ValidPrice annotation
 */
public class InvalidPriceException extends RuntimeException {
    public InvalidPriceException(String field) {
        super(String.format("InvalidPriceException: %s can not be negative.", field));
    }
}