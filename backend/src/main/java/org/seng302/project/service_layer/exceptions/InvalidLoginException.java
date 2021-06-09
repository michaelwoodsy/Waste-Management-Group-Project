package org.seng302.project.service_layer.exceptions;

import org.seng302.project.web_layer.controller.UserController;

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
