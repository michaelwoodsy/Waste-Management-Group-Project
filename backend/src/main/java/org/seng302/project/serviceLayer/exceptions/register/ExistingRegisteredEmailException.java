package org.seng302.project.serviceLayer.exceptions.register;

import org.seng302.project.webLayer.controller.UserController;

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
