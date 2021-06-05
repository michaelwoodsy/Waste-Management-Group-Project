package org.seng302.project.serviceLayer.exceptions;

import org.seng302.project.webLayer.controller.UserController;

/**
 * Exception triggered when attempting to remove the primary administrator from administering the business.
 *
 * @see UserController
 */
public class CantRemoveAdministratorException extends RuntimeException {

    public CantRemoveAdministratorException(Integer userId, Integer businessId) {
        super(String.format("CantRemoveAdministratorException: You can not remove user with id %d from administering business with id %d as they are the primary administrator.", userId, businessId));
    }

}
