package org.seng302.project.exceptions;

/**
 * Exception triggered when birth date is invalid.
 *
 * @see org.seng302.project.controller.UserController
 */
public class InvalidClosesDateException extends RuntimeException {

    public InvalidClosesDateException() {
        super("InvalidClosesDateException: closing date must be in the future.");
    }

}
