package org.seng302.project.serviceLayer.exceptions.card;

/**
 * Exception for when someone tries editing/deleting a card that is not theirs
 */
public class ForbiddenCardActionException extends RuntimeException{
    public ForbiddenCardActionException() {
        super("ForbiddenCardActionException: You are not allowed to edit or delete this card");
    }
}
