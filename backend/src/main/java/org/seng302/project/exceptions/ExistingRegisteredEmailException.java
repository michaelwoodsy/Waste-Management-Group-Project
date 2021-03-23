package org.seng302.project.exceptions;

/**
 * Exception triggered when an email address is already in use.
 *
 * @see org.seng302.project.controller.UserController
 */
public class ExistingRegisteredEmailException extends RuntimeException {

    public ExistingRegisteredEmailException() {
        super("ExistingRegisteredEmailException: email address is already registered.");
    }

}
