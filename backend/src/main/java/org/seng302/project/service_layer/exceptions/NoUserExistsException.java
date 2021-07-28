package org.seng302.project.service_layer.exceptions;

import org.seng302.project.web_layer.controller.UserController;

/**
 * Exception triggered when an email address is already in use.
 *
 * @see UserController
 */
public class NoUserExistsException extends RuntimeException {

    public NoUserExistsException(Integer id) {
        super(String.format("NoUserExistsException: no user with id %d exists.", id));
    }

}
