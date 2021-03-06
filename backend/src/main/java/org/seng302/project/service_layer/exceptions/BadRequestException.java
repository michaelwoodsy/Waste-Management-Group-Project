package org.seng302.project.service_layer.exceptions;

/**
 * Exception for 400 responses
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super("BadRequestException: " + message);
    }
}
