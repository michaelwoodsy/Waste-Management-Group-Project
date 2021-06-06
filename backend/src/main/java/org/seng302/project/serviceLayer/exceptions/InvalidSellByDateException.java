package org.seng302.project.serviceLayer.exceptions;

import org.seng302.project.webLayer.controller.UserController;

/**
 * Exception triggered when birth date is invalid.
 *
 * @see UserController
 */
public class InvalidSellByDateException extends RuntimeException {

    public InvalidSellByDateException() {
        super("InvalidSellByDateException: sell by date must be in the future.");
    }

}
