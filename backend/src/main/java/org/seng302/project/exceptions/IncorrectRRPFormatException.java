package org.seng302.project.exceptions;

/**
 * Exception triggered when business with id does not exist.
 *
 * @see org.seng302.project.controller.BusinessController
 */
public class IncorrectRRPFormatException extends RuntimeException {

    public IncorrectRRPFormatException() {
        super("IncorrectRRPFormatException: Products recomended retail price is in an incorrect format. Must be a number");
    }

}
