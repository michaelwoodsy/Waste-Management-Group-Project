package org.seng302.project.serviceLayer.exceptions;

import org.seng302.project.webLayer.controller.UserController;

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
