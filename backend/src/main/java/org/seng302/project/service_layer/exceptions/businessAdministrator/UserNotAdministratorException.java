package org.seng302.project.service_layer.exceptions.businessAdministrator;

import org.seng302.project.web_layer.controller.UserController;

/**
 * Exception triggered when someone tries to remove a user who is not an administrator from administering a business
 *
 * @see UserController
 */
public class UserNotAdministratorException extends RuntimeException {

    public UserNotAdministratorException(Integer userId, Integer businessId) {
        super(String.format("UserNotAdministratorException: You can not remove user with id %d from administering business with id %d as they are not an administrator.", userId, businessId));
    }

}
