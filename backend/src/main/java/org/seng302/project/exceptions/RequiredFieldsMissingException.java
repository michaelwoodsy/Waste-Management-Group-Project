package org.seng302.project.exceptions;

/**
 * Exception triggered by UserController.createUser() when required fields are missing.
 *
 * @see org.seng302.project.controller.UserController
 */
public class RequiredFieldsMissingException extends RuntimeException {

    public RequiredFieldsMissingException() {
        super("RequiredFieldsMissingException: required fields are missing.");
    }

}
