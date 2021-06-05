package org.seng302.project.serviceLayer.exceptions;

/**
 * Exception triggered when the marketplace section is invalid
 */
public class InvalidMarketplaceSectionException extends RuntimeException {

    public InvalidMarketplaceSectionException() {
        super("InvalidMarketplaceSectionException: Marketplace section must be 'ForSale', 'Wanted' or 'Exchange'.");
    }

}
