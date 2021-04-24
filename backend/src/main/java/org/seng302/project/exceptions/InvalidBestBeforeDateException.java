package org.seng302.project.exceptions;

/**
 * Exception triggered when birth date is invalid.
 *
 * @see org.seng302.project.controller.UserController
 */
public class InvalidBestBeforeDateException extends RuntimeException {

    public InvalidBestBeforeDateException() {
        super("InvalidBestBeforeDateException: best before date must be in the future.");
    }

}
