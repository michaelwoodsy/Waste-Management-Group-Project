package org.seng302.project.serviceLayer.exceptions;

import org.seng302.project.webLayer.controller.UserController;

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