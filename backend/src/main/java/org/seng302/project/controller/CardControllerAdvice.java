package org.seng302.project.controller;


import org.seng302.project.exceptions.card.ForbiddenCardActionException;
import org.seng302.project.exceptions.card.NoCardExistsException;
import org.seng302.project.exceptions.NoUserExistsException;
import org.seng302.project.exceptions.RequiredFieldsMissingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Class that CardController uses for handling exceptions.
 */
@ControllerAdvice
public class CardControllerAdvice {

    /**
     * Exception thrown by the createCard() function in CardController
     * when a user tries to create a card with a required field that is an empty string.
     *
     * @return a 400 response with an appropriate message
     */
    @ExceptionHandler(RequiredFieldsMissingException.class)
    public ResponseEntity<String> requiredFieldsMissing(RequiredFieldsMissingException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Exception thrown by the createCard() function in CardController
     * when there is no matching user that has created card.
     *
     * @return a 400 response with an appropriate message
     */
    @ExceptionHandler(NoUserExistsException.class)
    public ResponseEntity<String> userDoesNotExist(NoUserExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Exception thrown by the getCard(), deleteCard() and extendCardDisplayPeriod() functions in CardController
     * when there is no matching card.
     *
     * @return a 406 response with an appropriate message
     */
    @ExceptionHandler(NoCardExistsException.class)
    public ResponseEntity<String> cardDoesNotExist(NoCardExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    }

    /**
     * Exception thrown by the extendCardDisplayPeriod() and deleteCard() functions in CardController
     * when a user tries to perform an action on a card when they are not the card creator or GAA.
     *
     * @return a 403 response with an appropriate message
     */
    @ExceptionHandler(ForbiddenCardActionException.class)
    public ResponseEntity<String> forbiddenCardAction(ForbiddenCardActionException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

}
