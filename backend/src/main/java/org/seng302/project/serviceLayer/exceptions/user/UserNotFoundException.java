package org.seng302.project.serviceLayer.exceptions.user;

import org.seng302.project.serviceLayer.exceptions.NotAcceptableException;
/**
 * Exception triggered when user with id does not exist.
 */
public class UserNotFoundException extends NotAcceptableException {
    public UserNotFoundException(Integer id) {
        super(String.format("UserNotFoundException: no business with id %d exists.", id));
    }
}
