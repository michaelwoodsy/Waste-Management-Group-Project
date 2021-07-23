package org.seng302.project.serviceLayer.exceptions.dgaa;

public class ForbiddenDGAAActionException extends RuntimeException {
    public ForbiddenDGAAActionException() {
        super("ForbiddenDGAAActionException: This action requires DGAA role.");
    }
}
