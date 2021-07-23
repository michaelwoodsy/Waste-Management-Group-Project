package org.seng302.project.serviceLayer.exceptions.notification;


/**
 * Exception for when someone tries getting/deleting a notification that is not assigned to them
 */
public class ForbiddenNotificationActionException extends RuntimeException {
    public ForbiddenNotificationActionException() {
        super("ForbiddenNotificationActionException: You are not allowed to retrieve or remove this notification");
    }
}
