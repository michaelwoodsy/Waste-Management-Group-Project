package org.seng302.project.serviceLayer.exceptions.register;

import org.seng302.project.webLayer.controller.UserController;

/**
 * Exception triggered by UserController.createUser() when a user is age is less than
 * minimum age requirement.
 *
 * @see UserController
 */
public class UserUnderageException extends RuntimeException {

    public UserUnderageException(String type, Integer age) {
        super(String.format("UserUnderageException: user does not meet the required age to register %s. Must be %d years old.", type, age));
    }

}
