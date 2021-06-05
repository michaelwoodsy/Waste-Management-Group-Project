package org.seng302.project.serviceLayer.exceptions;

import org.seng302.project.webLayer.controller.BusinessController;

/**
 * Exception triggered when business with id does not exist.
 *
 * @see BusinessController
 */
public class NoBusinessExistsException extends RuntimeException {

    public NoBusinessExistsException(Integer id) {
        super(String.format("NoBusinessExistsException: no business with id %d exists.", id));
    }

}
