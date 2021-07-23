package org.seng302.project.serviceLayer.exceptions;

import org.seng302.project.webLayer.controller.UserController;

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
