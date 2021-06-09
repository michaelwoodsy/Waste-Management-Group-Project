package org.seng302.project.service_layer.exceptions.card;

import org.seng302.project.web_layer.controller.BusinessController;

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
