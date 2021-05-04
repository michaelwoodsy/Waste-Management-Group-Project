package org.seng302.project.exceptions;

/**
 * Exception triggered when birth date is invalid.
 *
 * @see org.seng302.project.controller.UserController
 */
public class InvalidDateException extends RuntimeException {

    public InvalidDateException() {
        super("InvalidDateException: date format is incorrect.");
    }

}
