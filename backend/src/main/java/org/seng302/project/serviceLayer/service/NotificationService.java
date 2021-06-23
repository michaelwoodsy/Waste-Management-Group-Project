package org.seng302.project.serviceLayer.service;

import org.seng302.project.repositoryLayer.model.Notification;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.NotificationRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.dto.SetPrimaryProductImageDTO;
import org.seng302.project.serviceLayer.exceptions.notification.ForbiddenNotificationActionException;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class NotificationService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    private static final Logger logger = LoggerFactory.getLogger(SetPrimaryProductImageDTO.class.getName());

    @Autowired
    public NotificationService(UserRepository userRepository,
                               NotificationRepository notificationRepository) {
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    /**
     * Gets a list of notifications assgined to the user.
     *
     * @param userId     ID of the user to get the notifications of.
     * @param appUser    AppUserDetails from spring security
     * @return List of notifications assigned to the user.
     */
    public List<Notification> getNotifications(Integer userId, AppUserDetails appUser) {
        try {
            logger.info("Request to get all notifications for user {}", userId);

            // check if loggedInUser has the same ID as the user retieving notifications
            User loggedInUser = userRepository.findByEmail(appUser.getUsername()).get(0);
            if (!loggedInUser.getId().equals(userId)) {
                throw new ForbiddenNotificationActionException();
            }

            // get the notifications for the user
            return notificationRepository.findAllByUser(loggedInUser);
        } catch (ForbiddenNotificationActionException handledException) {
            logger.error(handledException.getMessage());
            throw handledException;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while retrieving user's notifications: %s", unhandledException.getMessage()));
            throw unhandledException;
        }
    }
}
