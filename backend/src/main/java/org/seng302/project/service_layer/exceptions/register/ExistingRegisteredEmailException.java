package org.seng302.project.service_layer.exceptions.register;

import org.seng302.project.web_layer.controller.UserController;

/**
 * Exception triggered when an email address is already in use.
 *
 * @see UserController
 */
public class ExistingRegisteredEmailException extends RuntimeException {

    public ExistingRegisteredEmailException() {
        super("ExistingRegisteredEmailException: email address is already registered.");
    }

}
