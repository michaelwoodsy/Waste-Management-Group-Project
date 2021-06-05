package org.seng302.project.serviceLayer.exceptions;

import org.seng302.project.webLayer.controller.UserController;

/**
 * Exception triggered when the user issuing the request is not the primary administrator.
 *
 * @see UserController
 */
public class ForbiddenPrimaryAdministratorActionException extends RuntimeException {

    public ForbiddenPrimaryAdministratorActionException(Integer id) {
        super(String.format("ForbiddenAdministratorActionException: You can not perform this action as you are not the primary administrator of business with id %d.", id));
    }

}
