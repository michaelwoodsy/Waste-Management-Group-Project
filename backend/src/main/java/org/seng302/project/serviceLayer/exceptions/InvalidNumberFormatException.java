package org.seng302.project.serviceLayer.exceptions;

import org.seng302.project.webLayer.controller.UserController;

/**
 * Exception triggered when an administrator for a business already exists.
 *
 * @see UserController
 */
public class InvalidNumberFormatException extends RuntimeException {

    public InvalidNumberFormatException(String field) {
        super(String.format("InvalidNumberFormatException: Invalid number format for input field %s.", field));
    }

}
