package org.seng302.project.exceptions;

/**
 * Exception triggered when business type supplied is not a valid type.
 *
 * @see org.seng302.project.controller.BusinessController
 */
public class NoBusinessTypeExistsException extends RuntimeException {

    public NoBusinessTypeExistsException(String type) {
        super(String.format("NoBusinessTypeExistsException: no business type '%s' exists.", type));
    }

}
