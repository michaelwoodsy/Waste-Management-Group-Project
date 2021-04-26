package org.seng302.project.exceptions;

/**
 * Exception triggered by UserController.createUser() when a user is age is less than
 * minimum age requirement.
 *
 * @see org.seng302.project.controller.UserController
 */
public class ItemExpiredException extends RuntimeException {

    public ItemExpiredException() {
        super("ItemExpiredException: Inventory item expiry date can't be in the past.");
    }

}
