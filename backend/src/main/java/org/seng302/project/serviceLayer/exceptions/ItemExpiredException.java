package org.seng302.project.serviceLayer.exceptions;

import org.seng302.project.webLayer.controller.UserController;

/**
 * Exception triggered by UserController.createUser() when a user is age is less than
 * minimum age requirement.
 *
 * @see UserController
 */
public class ItemExpiredException extends RuntimeException {

    public ItemExpiredException() {
        super("ItemExpiredException: Inventory item expiry date can't be in the past.");
    }

}
