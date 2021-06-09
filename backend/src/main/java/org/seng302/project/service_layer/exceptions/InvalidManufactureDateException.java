package org.seng302.project.service_layer.exceptions;

import org.seng302.project.web_layer.controller.UserController;

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
