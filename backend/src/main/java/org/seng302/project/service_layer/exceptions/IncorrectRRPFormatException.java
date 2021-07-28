package org.seng302.project.service_layer.exceptions;

import org.seng302.project.web_layer.controller.BusinessController;

/**
 * Exception triggered when business with id does not exist.
 *
 * @see BusinessController
 */
public class IncorrectRRPFormatException extends RuntimeException {

    public IncorrectRRPFormatException() {
        super("IncorrectRRPFormatException: Products recomended retail price is in an incorrect format. Must be a number");
    }

}
