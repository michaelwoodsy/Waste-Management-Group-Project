package org.seng302.project.webLayer.controller;

import org.seng302.project.repositoryLayer.model.Notification;
import org.seng302.project.repositoryLayer.repository.NotificationRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.dto.GetNotificationsDTO;
import org.seng302.project.serviceLayer.exceptions.notification.ForbiddenNotificationActionException;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(ProductImagesController.class.getName());

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository

    @Autowired
    public NotificationController(UserRepository userRepository, NotificationRepository notificationRepository) {
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    @GetMapping("/users/{userId}/notifications")
    public List<Notification> getNotifications(@PathVariable int userId, @AuthenticationPrincipal AppUserDetails appUser) {
        try {
            GetNotificationsDTO requestDTO = new GetNotificationsDTO(userId, appUser, userRepository, notificationRepository);
            return requestDTO.executeRequest();
        } catch (ForbiddenNotificationActionException handledException) {
            logger.error(handledException.getMessage());
            throw handledException;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while retrieving user's notifications: %s", unhandledException.getMessage()));
        }
    }
}
