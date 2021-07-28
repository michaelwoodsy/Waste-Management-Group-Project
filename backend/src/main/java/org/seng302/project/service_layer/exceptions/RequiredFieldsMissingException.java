package org.seng302.project.service_layer.exceptions;

import org.seng302.project.web_layer.controller.UserController;

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
