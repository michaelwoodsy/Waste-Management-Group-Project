package org.seng302.project.exceptions;

/**
 * Exception triggered when business with id does not exist.
 *
 * @see org.seng302.project.controller.BusinessController
 */
public class NoCookieExistsException extends RuntimeException {

    public NoCookieExistsException() {
        super("NoCookieExistsException: Cookie supplied does not exist.");
    }

}
