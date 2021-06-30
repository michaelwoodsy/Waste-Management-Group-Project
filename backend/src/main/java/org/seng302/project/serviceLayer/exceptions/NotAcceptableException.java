package org.seng302.project.serviceLayer.exceptions;

public class NotAcceptableException extends RuntimeException {

    /**
     * Exception for 406 responses
     */
    public NotAcceptableException(String message) {
        super(message);
    }

}
