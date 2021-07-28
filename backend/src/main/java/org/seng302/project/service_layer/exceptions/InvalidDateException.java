package org.seng302.project.service_layer.exceptions;

import org.seng302.project.web_layer.controller.UserController;

/**
 * Exception triggered when birth date is invalid.
 *
 * @see UserController
 */
public class InvalidDateException extends RuntimeException {

    public InvalidDateException() {
        super("InvalidDateException: date format is incorrect.");
    }

    public InvalidDateException(String message) {
        super(message);
    }

}
