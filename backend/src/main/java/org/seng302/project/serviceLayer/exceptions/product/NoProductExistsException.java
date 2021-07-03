package org.seng302.project.serviceLayer.exceptions.product;

import org.seng302.project.webLayer.controller.BusinessController;

/**
 * Exception triggered when business with id does not exist.
 *
 * @see BusinessController
 */
public class NoProductExistsException extends RuntimeException {

    public NoProductExistsException(String productId, Integer businessId) {
        super(String.format("NoProductExistsException: no product with id '%s' exists in business with id %d.", productId, businessId));
    }

}
