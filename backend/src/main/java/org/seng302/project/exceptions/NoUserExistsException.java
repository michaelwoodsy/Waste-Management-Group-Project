package org.seng302.project.exceptions;

/**
 * Exception triggered when an email address is already in use.
 *
 * @see org.seng302.project.controller.UserController
 */
public class NoUserExistsException extends RuntimeException {

    public NoUserExistsException(Integer id) {
        super(String.format("NoUserExistsException: no user with id %d exists.", id));
    }

}
