package org.seng302.project.controller;


import org.seng302.project.exceptions.InvalidMarketplaceSectionException;
import org.seng302.project.exceptions.NoCardExistsException;
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
     * Exception thrown by the getCard() function in CardController
     * when there is no matching card.
     *
     * @return a 406 response with an appropriate message
     */
    @ExceptionHandler(NoCardExistsException.class)
    public ResponseEntity<String> cardDoesNotExist(NoCardExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    }

    /**
     * Exception thrown by the getAllCards() function in CardController
     * when the section provided is invalid.
     *
     * @return a 400 response with an appropriate message
     */
    @ExceptionHandler(InvalidMarketplaceSectionException.class)
    public ResponseEntity<String> sectionNotValid(InvalidMarketplaceSectionException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
