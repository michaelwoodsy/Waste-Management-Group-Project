package org.seng302.project.service_layer.exceptions.user;

/**
 * Exception triggered when a user tries to perform an action regarding a user
 * that is not them.
 */
public class ForbiddenUserException extends RuntimeException {
    public ForbiddenUserException(Integer id) {
        super(String.format("ForbiddenUserException: You can not perform this action as you are not the correct user with id %d.", id));
    }
}
