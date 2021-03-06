package org.seng302.project.service_layer.exceptions.register;

import org.seng302.project.web_layer.controller.UserController;

/**
 * Exception triggered when an email address is invalid format.
 *
 * @see UserController
 */
public class InvalidEmailException extends RuntimeException {

    public InvalidEmailException() {
        super("InvalidEmailException: email address format is invalid.");
    }

}
