package org.seng302.project.service_layer.exceptions;

public class NotAcceptableException extends RuntimeException {

    /**
     * Exception for 406 responses
     */
    public NotAcceptableException(String message) {
        super("NotAcceptableException: " + message);
    }

}
