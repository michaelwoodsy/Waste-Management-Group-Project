package org.seng302.project.web_layer.controller;

import net.minidev.json.JSONObject;
import org.seng302.project.repository_layer.model.AdminNotification;
import org.seng302.project.repository_layer.model.UserNotification;
import org.seng302.project.service_layer.dto.notifications.DeleteUserNotificationDTO;
import org.seng302.project.service_layer.exceptions.BadRequestException;
import org.seng302.project.service_layer.service.NotificationService;
import org.seng302.project.web_layer.authentication.AppUserDetails;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for handling requests to do with user notifications.
 */
@RestController
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
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

    /**
     * Gets all admin notifications
     *
     * @param appUser The user making the request
     * @return a list of all admin notifications
     */
    @GetMapping("/notifications")
    public List<AdminNotification> getAdminNotifications(@AuthenticationPrincipal AppUserDetails appUser) {
        return notificationService.getAdminNotifications(appUser);
    }

    /**
     * Deletes an admin notification
     *
     * @param notificationId ID of the notification to delete
     * @param appUser The user trying to delete the notification
     */
    @DeleteMapping("/notifications/{notificationId}")
    public void deleteAdminNotification(@PathVariable Integer notificationId, @AuthenticationPrincipal AppUserDetails appUser) {
        notificationService.deleteAdminNotification(notificationId, appUser);
    }

    /**
     * Marks a user notification as read
     *
     * @param userId ID of the user to read the user notification for
     * @param notificationId ID of the user notification to read
     * @param appUser The user trying to read the user notification
     */
    @PatchMapping("/users/{userId}/notifications/{notificationId}/read")
    public void readUserNotification(@RequestBody JSONObject request, @PathVariable int userId,
                                 @PathVariable int notificationId, @AuthenticationPrincipal AppUserDetails appUser) {
        boolean read;
        try {
            read = (boolean) request.get("read");
        } catch (ClassCastException | NullPointerException exception) {
            String message = "Value of \"read\" must be a boolean";
            logger.warn(message);
            throw new BadRequestException(message);
        }
        notificationService.readUserNotification(read, userId, notificationId, appUser);
    }

    /**
     * Marks an admin notification as read
     *
     * @param notificationId ID of the admin notification to read
     * @param appUser The user trying to read the admin notification
     */
    @PatchMapping("/notifications/{notificationId}/read")
    public void readAdminNotification(@RequestBody JSONObject request, @PathVariable int notificationId,
                                      @AuthenticationPrincipal AppUserDetails appUser) {
        boolean read;
        try {
            read = (boolean) request.get("read");
        } catch (ClassCastException | NullPointerException exception) {
            String message = "Value of \"read\" must be a boolean";
            logger.warn(message);
            throw new BadRequestException(message);
        }
        notificationService.readAdminNotification(read, notificationId, appUser);
    }
}
