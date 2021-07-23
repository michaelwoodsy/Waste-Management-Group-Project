package org.seng302.project.serviceLayer.exceptions;

/**
 * Exception for 403 responses
 */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super("ForbiddenException: " + message);
    }
}
