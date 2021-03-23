package org.seng302.project.exceptions;

/**
 * Exception triggered when a phone number is invalid.
 *
 * @see org.seng302.project.controller.UserController
 */
public class InvalidPhoneNumberException extends RuntimeException {

    public InvalidPhoneNumberException() {
        super("InvalidPhoneNumberException: phone number format is invalid.");
    }

}
