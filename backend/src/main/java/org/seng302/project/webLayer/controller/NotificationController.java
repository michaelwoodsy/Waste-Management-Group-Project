package org.seng302.project.webLayer.controller;

import org.seng302.project.repositoryLayer.model.Notification;
import org.seng302.project.serviceLayer.service.NotificationService;
import org.seng302.project.webLayer.authentication.AppUserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @GetMapping("/users/{userId}/notifications")
    public List<Notification> getNotifications(@PathVariable int userId,@AuthenticationPrincipal AppUserDetails appUser) {
        return notificationService.getUserNotifications(userId, appUser);
    }
}
