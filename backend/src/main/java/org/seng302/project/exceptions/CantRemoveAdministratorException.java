package org.seng302.project.exceptions;

/**
 * Exception triggered when attempting to remove the primary administrator from administering the business.
 *
 * @see org.seng302.project.controller.UserController
 */
public class CantRemoveAdministratorException extends RuntimeException {

    public CantRemoveAdministratorException(Integer userId, Integer businessId) {
        super(String.format("CantRemoveAdministratorException: You can not remove user with id %d from administering business with id %d as they are the primary administrator.", userId, businessId));
    }

}
