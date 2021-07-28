package org.seng302.project.service_layer.exceptions;

public class NoNotificationExistsException extends NotAcceptableException{
    public NoNotificationExistsException(Integer id) {
        super(String.format("Notification with ID: %d does not exist", id));
    }
}
