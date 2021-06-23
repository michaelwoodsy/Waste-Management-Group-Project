package org.seng302.project.serviceLayer.exceptions;

import org.seng302.project.webLayer.controller.UserController;

/**
 * Exception triggered when birth date is invalid.
 *
 * @see UserController
 */
public class InvalidClosesDateException extends RuntimeException {

    public InvalidClosesDateException() {
        super("InvalidClosesDateException: closing date must be in the future.");
    }

}
