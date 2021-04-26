package org.seng302.project.exceptions;

/**
 * Exception triggered when birth date is invalid.
 *
 * @see org.seng302.project.controller.UserController
 */
public class InvalidManufactureDateException extends RuntimeException {

    public InvalidManufactureDateException() {
        super("InvalidManufactureDateException: manufacture date must be in the past.");
    }

}
