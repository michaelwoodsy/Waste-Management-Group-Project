package org.seng302.project.service_layer.exceptions;

import org.seng302.project.web_layer.controller.BusinessController;

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
