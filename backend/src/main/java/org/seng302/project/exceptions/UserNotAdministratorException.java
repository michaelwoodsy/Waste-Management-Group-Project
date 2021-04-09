package org.seng302.project.exceptions;

/**
 * Exception triggered someone tries to remove a user who is not an administrator from administering a business
 *
 * @see org.seng302.project.controller.UserController
 */
public class UserNotAdministratorException extends RuntimeException {

    public UserNotAdministratorException(Integer userId, Integer businessId) {
        super(String.format("UserNotAdministratorException: You can not remove user with id %d from administering business with id %d as they not an administrator.", userId, businessId));
    }

}
