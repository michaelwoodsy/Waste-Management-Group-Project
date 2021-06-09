package org.seng302.project.service_layer.exceptions.businessAdministrator;

import org.seng302.project.web_layer.controller.UserController;

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
