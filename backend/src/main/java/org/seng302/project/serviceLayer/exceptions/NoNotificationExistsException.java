package org.seng302.project.serviceLayer.exceptions;

public class NoNotificationExistsException extends NotAcceptableException{
    public NoNotificationExistsException(Integer id) {
        super(String.format("Notification with ID: %d does not exist", id));
    }
}
