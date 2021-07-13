package org.seng302.project.webLayer.controller;

import org.seng302.project.repositoryLayer.model.UserNotification;
import org.seng302.project.serviceLayer.dto.notifications.DeleteUserNotificationDTO;
import org.seng302.project.serviceLayer.service.NotificationService;
import org.seng302.project.webLayer.authentication.AppUserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for handling requests to do with user notifications.
 */
@RestController
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Returns all UserNotifications for the user
     * @param userId id of the user to retrieve notifications for-
     * @param appUser logged in users details
     * @return a list of UserNotifications
     */
    @GetMapping("/users/{userId}/notifications")
    public List<UserNotification> getUserNotifications(@PathVariable int userId, @AuthenticationPrincipal AppUserDetails appUser) {
        return notificationService.getUserNotifications(userId, appUser);
    }

    /**
     * deletes a UserNotification
     * @param userId id of the user to retrieve notifications for
     * @param appUser logged in users details
     */
    @DeleteMapping("/users/{userId}/notifications/{notificationId}")
    public void deleteUserNotification(@PathVariable int userId, @PathVariable int notificationId, @AuthenticationPrincipal AppUserDetails appUser) {
        var dto = new DeleteUserNotificationDTO(userId, notificationId, appUser);
        notificationService.deleteUserNotification(dto);
    }
}
