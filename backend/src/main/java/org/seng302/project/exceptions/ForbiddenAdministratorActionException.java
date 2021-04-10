package org.seng302.project.exceptions;

/**
 * Exception triggered when the user issuing the request is not the primary administrator.
 *
 * @see org.seng302.project.controller.UserController
 */
public class ForbiddenAdministratorActionException extends RuntimeException {

    public ForbiddenAdministratorActionException(Integer id) {
        super(String.format("ForbiddenAdministratorActionException: You can not perform this action as you are not the primary administrator of business with id %d.", id));
    }

}
