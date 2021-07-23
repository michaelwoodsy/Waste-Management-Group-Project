package org.seng302.project.serviceLayer.exceptions;

import org.seng302.project.webLayer.controller.BusinessController;

/**
 * Exception triggered when business type supplied is not a valid type.
 *
 * @see BusinessController
 */
public class NoBusinessTypeExistsException extends RuntimeException {

    public NoBusinessTypeExistsException(String type) {
        super(String.format("NoBusinessTypeExistsException: no business type '%s' exists.", type));
    }

}
