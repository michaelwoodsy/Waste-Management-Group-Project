package org.seng302.project.serviceLayer.exceptions;

public class ForbiddenActionException extends RuntimeException {

    /**
     * Exception for 403 responses
     */
    public ForbiddenActionException(String message) {
        super("ForbiddenActionException: " + message);
    }

}
