package org.seng302.project.serviceLayer.exceptions;

import org.seng302.project.webLayer.controller.UserController;

/**
 * Exception triggered by UserController.createUser() when required fields are missing.
 *
 * @see UserController
 */
public class RequiredFieldsMissingException extends RuntimeException {

    public RequiredFieldsMissingException() {
        super("RequiredFieldsMissingException: required fields are missing.");
    }

}
