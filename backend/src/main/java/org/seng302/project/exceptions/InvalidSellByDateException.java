package org.seng302.project.exceptions;

/**
 * Exception triggered when birth date is invalid.
 *
 * @see org.seng302.project.controller.UserController
 */
public class InvalidSellByDateException extends RuntimeException {

    public InvalidSellByDateException() {
        super("InvalidSellByDateException: sell by date must be in the future.");
    }

}
