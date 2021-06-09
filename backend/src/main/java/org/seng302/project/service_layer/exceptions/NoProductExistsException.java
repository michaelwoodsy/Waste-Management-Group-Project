package org.seng302.project.service_layer.exceptions;

import org.seng302.project.web_layer.controller.BusinessController;

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
