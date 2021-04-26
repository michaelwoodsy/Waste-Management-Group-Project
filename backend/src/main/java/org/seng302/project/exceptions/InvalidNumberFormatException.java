package org.seng302.project.exceptions;

/**
 * Exception triggered when an administrator for a business already exists.
 *
 * @see org.seng302.project.controller.UserController
 */
public class InvalidNumberFormatException extends RuntimeException {

    public InvalidNumberFormatException(String field) {
        super(String.format("InvalidNumberFormatException: Invalid number format for input field %s.", field));
    }

}
