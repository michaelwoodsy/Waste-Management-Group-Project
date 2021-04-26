package org.seng302.project.exceptions;

/**
 * Exception triggered when address is invalid
 *
 * @see org.seng302.project.controller.UserController
 */
public class InvalidAddressException extends RuntimeException {

    public InvalidAddressException() {
        super("InvalidAddressException: address format is incorrect.");
    }

}