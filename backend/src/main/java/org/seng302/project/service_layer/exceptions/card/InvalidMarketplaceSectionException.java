package org.seng302.project.service_layer.exceptions.card;

/**
 * Exception triggered when the marketplace section is invalid
 */
public class InvalidMarketplaceSectionException extends RuntimeException {

    public InvalidMarketplaceSectionException() {
        super("InvalidMarketplaceSectionException: Marketplace section must be 'ForSale', 'Wanted' or 'Exchange'.");
    }

}
