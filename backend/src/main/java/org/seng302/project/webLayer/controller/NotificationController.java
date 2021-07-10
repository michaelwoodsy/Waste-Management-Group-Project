package org.seng302.project.webLayer.controller;

import net.minidev.json.JSONArray;
import org.seng302.project.repositoryLayer.model.Notification;
import org.seng302.project.repositoryLayer.model.UserNotification;
import org.seng302.project.serviceLayer.service.NotificationService;
import org.seng302.project.webLayer.authentication.AppUserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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
     * @param userId id of the user to retrieve notifications for
     * @param appUser logged in users details
     * @return a list of UserNotifications
     */
    @GetMapping("/users/{userId}/notifications")
    public JSONArray getNotifications(@PathVariable int userId, @AuthenticationPrincipal AppUserDetails appUser) {
        return notificationService.getUserNotifications(userId, appUser);
    }
}
