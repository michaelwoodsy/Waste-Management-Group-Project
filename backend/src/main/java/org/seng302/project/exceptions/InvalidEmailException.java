package org.seng302.project.exceptions;

/**
 * Exception triggered when an email address is invalid format.
 *
 * @see org.seng302.project.controller.UserController
 */
public class InvalidEmailException extends RuntimeException {

    public InvalidEmailException() {
        super("InvalidEmailException: email address format is invalid.");
    }

}
