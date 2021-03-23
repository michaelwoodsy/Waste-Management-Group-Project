package org.seng302.project.exceptions;

/**
 * Exception triggered when username or password are incorrect.
 *
 * @see org.seng302.project.controller.UserController
 */
public class InvalidLoginException extends RuntimeException {

    public InvalidLoginException() {
        super("InvalidLoginException: email or password are incorrect.");
    }

}
