package org.seng302.project.serviceLayer.exceptions;

/**
 * Exception for 400 responses
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
