package org.seng302.project.serviceLayer.dto;

import org.seng302.project.repositoryLayer.model.Notification;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.NotificationRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.exceptions.notification.ForbiddenNotificationActionException;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GetNotificationsDTO {

    private final Integer userId;
    private final AppUserDetails appUser;

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    private static final Logger logger = LoggerFactory.getLogger(SetPrimaryProductImageDTO.class.getName());

    public GetNotificationsDTO(Integer userId,
                               AppUserDetails appUser,
                               UserRepository userRepository,
                               NotificationRepository notificationRepository) {
        this.userId = userId;
        this.appUser = appUser;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    /**
     * Called by the getNotifications() method in NotificationController.
     * Handles the business logic for retrieving a user's notifications,
     * throws exceptions up to the controller to handle
     */
    public List<Notification> executeRequest() {
        logger.info("Request to get all notifications for user {}", userId);

        User loggedInUser = userRepository.findByEmail(appUser.getUsername()).get(0);

        // check if loggedInUser has the same ID as the user retieving notifications
        if (!loggedInUser.getId().equals(userId)) {
            throw new ForbiddenNotificationActionException();
        }

        return notificationRepository.findAllByUser(loggedInUser);
    }
}
