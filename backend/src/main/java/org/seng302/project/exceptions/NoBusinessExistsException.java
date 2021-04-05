package org.seng302.project.exceptions;

/**
 * Exception triggered when business with id does not exist.
 *
 * @see org.seng302.project.controller.BusinessController
 */
public class NoBusinessExistsException extends RuntimeException {

    public NoBusinessExistsException(Integer id) {
        super(String.format("NoBusinessExistsException: no business with id %d exists.", id));
    }

}
