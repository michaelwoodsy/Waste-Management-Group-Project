package org.seng302.project.service_layer.exceptions.register;

import org.seng302.project.web_layer.controller.UserController;

/**
 * Exception triggered when address is invalid
 *
 * @see UserController
 */
public class InvalidAddressException extends RuntimeException {

    public InvalidAddressException() {
        super("InvalidAddressException: address format is incorrect.");
    }

}