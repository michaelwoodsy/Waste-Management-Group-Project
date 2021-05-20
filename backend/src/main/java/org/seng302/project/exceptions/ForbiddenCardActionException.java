package org.seng302.project.exceptions;

/**
 * Exception triggered when the user issuing the request is not the creator of the card.
 *
 * @see org.seng302.project.controller.CardController
 */
public class ForbiddenCardActionException extends RuntimeException {

    public ForbiddenCardActionException(Integer id) {
        super(String.format("ForbiddenCardActionException: You can not perform this action as you are not the creator of card with id %d.", id));
    }

}
