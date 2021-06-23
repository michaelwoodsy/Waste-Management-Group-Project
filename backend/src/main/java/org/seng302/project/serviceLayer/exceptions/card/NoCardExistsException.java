package org.seng302.project.serviceLayer.exceptions.card;

import org.seng302.project.webLayer.controller.BusinessController;

/**
 * Exception triggered when business with id does not exist.
 *
 * @see BusinessController
 */
public class NoCardExistsException extends RuntimeException {

    public NoCardExistsException(Integer id) {
        super(String.format("NoCardExistsException: no card with id %d exists.", id));
    }

}
