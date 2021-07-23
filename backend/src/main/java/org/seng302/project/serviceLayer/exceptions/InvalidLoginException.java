package org.seng302.project.serviceLayer.exceptions;

import org.seng302.project.webLayer.controller.UserController;

/**
 * Exception triggered when username or password are incorrect.
 *
 * @see UserController
 */
public class InvalidLoginException extends RuntimeException {

    public InvalidLoginException() {
        super("InvalidLoginException: email or password are incorrect.");
    }

}
