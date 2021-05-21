package org.seng302.project.exceptions.card;

/**
 * Exception triggered when business with id does not exist.
 *
 * @see org.seng302.project.controller.BusinessController
 */
public class NoCardExistsException extends RuntimeException {

    public NoCardExistsException(Integer id) {
        super(String.format("NoCardExistsException: no card with id %d exists.", id));
    }

}
