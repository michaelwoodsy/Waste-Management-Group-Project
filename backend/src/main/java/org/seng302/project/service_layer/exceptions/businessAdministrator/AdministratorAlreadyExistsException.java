package org.seng302.project.service_layer.exceptions.businessAdministrator;

import org.seng302.project.web_layer.controller.UserController;

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
