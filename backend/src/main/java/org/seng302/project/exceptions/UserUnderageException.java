package org.seng302.project.exceptions;

/**
 * Exception triggered by UserController.createUser() when a user is age is less than
 * minimum age requirement.
 *
 * @see org.seng302.project.controller.UserController
 */
public class UserUnderageException extends RuntimeException {

    public UserUnderageException() {
        super("UserUnderageException: user does not meet the required age to register account. Must be 13 years old.");
    }

}
