package org.seng302.project.exceptions;

/**
 * Exception triggered when business with id does not exist.
 *
 * @see org.seng302.project.controller.BusinessController
 */
public class NoProductExistsException extends RuntimeException {

    public NoProductExistsException(String productId, Integer businessId) {
        super(String.format("NoProductExistsException: no product with id '%s' exists in business with id %d.", productId, businessId));
    }

}
