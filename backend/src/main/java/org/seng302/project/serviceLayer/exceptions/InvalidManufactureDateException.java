package org.seng302.project.serviceLayer.exceptions;

import org.seng302.project.webLayer.controller.UserController;

/**
 * Exception triggered when birth date is invalid.
 *
 * @see UserController
 */
public class InvalidManufactureDateException extends RuntimeException {

    public InvalidManufactureDateException() {
        super("InvalidManufactureDateException: manufacture date must be in the past.");
    }

}
