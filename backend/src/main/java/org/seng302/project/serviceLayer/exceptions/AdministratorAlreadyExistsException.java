package org.seng302.project.serviceLayer.exceptions;

import org.seng302.project.webLayer.controller.UserController;

/**
 * Exception triggered when an administrator for a business already exists.
 *
 * @see UserController
 */
public class AdministratorAlreadyExistsException extends RuntimeException {

    public AdministratorAlreadyExistsException(Integer userId, Integer businessId) {
        super(String.format("AdministratorAlreadyExistsException: the user with id %d is already an administrator of business with id %d.", userId, businessId));
    }

}
