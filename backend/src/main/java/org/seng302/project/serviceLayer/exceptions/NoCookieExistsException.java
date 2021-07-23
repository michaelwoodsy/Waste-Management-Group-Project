package org.seng302.project.serviceLayer.exceptions;

import org.seng302.project.webLayer.controller.BusinessController;

/**
 * Exception triggered when business with id does not exist.
 *
 * @see BusinessController
 */
public class NoCookieExistsException extends RuntimeException {

    public NoCookieExistsException() {
        super("NoCookieExistsException: Cookie supplied does not exist.");
    }

}
