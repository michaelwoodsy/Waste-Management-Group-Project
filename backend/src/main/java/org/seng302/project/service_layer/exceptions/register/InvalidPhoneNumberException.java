package org.seng302.project.service_layer.exceptions.register;

import org.seng302.project.web_layer.controller.UserController;

/**
 * Exception triggered when a phone number is invalid.
 *
 * @see UserController
 */
public class InvalidPhoneNumberException extends RuntimeException {

    public InvalidPhoneNumberException() {
        super("InvalidPhoneNumberException: phone number format is invalid.");
    }

}
