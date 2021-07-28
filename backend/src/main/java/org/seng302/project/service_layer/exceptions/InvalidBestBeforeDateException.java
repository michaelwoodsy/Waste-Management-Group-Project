package org.seng302.project.service_layer.exceptions;

import org.seng302.project.web_layer.controller.UserController;

/**
 * Exception triggered when birth date is invalid.
 *
 * @see UserController
 */
public class InvalidBestBeforeDateException extends RuntimeException {

    public InvalidBestBeforeDateException() {
        super("InvalidBestBeforeDateException: best before date must be in the future.");
    }

}
