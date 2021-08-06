package org.seng302.project.web_layer.controller_advice;

import org.seng302.project.service_layer.exceptions.notification.ForbiddenNotificationActionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Class that NotificationController uses for handling exceptions.
 */
@ControllerAdvice
public class NotificationControllerAdvice {

    /**
     * Exception thrown by the getNotifications() method when someone
     * tries getting/deleting a notification that is not assigned to them
     *
     * @return a 403 response with an appropriate message
     */
    @ExceptionHandler(ForbiddenNotificationActionException.class)
    public ResponseEntity<String> forbiddenNotificationAction(ForbiddenNotificationActionException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }
}
