package org.seng302.project.serviceLayer.service;

import net.minidev.json.JSONArray;
import org.seng302.project.repositoryLayer.model.UserNotification;
import org.seng302.project.repositoryLayer.repository.UserNotificationRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.exceptions.notification.ForbiddenNotificationActionException;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class.getName());
    private final UserRepository userRepository;
    private final UserNotificationRepository userNotificationRepository;


    @Autowired
    public NotificationService(UserRepository userRepository,
                               UserNotificationRepository userNotificationRepository) {
        this.userRepository = userRepository;
        this.userNotificationRepository = userNotificationRepository;
    }

    /**
     * Gets a list of notifications assigned to the user.
     *
     * @param userId     ID of the user to get the notifications of.
     * @param appUser    AppUserDetails from spring security
     * @return List of notifications assigned to the user.
     */
    public JSONArray getUserNotifications(Integer userId, AppUserDetails appUser) {
        try {
            logger.info("Request to get all notifications for user {}", userId);

            // check if loggedInUser has the same ID as the user retrieving notifications
            var loggedInUser = userRepository.findByEmail(appUser.getUsername()).get(0);
            if (!loggedInUser.getId().equals(userId)) {
                throw new ForbiddenNotificationActionException();
            }

            List<UserNotification> userNotifications = userNotificationRepository.findAllByUser(loggedInUser);
            var returnJson = new JSONArray();
            returnJson.addAll(userNotifications);

            return returnJson;

        } catch (ForbiddenNotificationActionException handledException) {
            logger.error(handledException.getMessage());
            throw handledException;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while retrieving user's notifications: %s", unhandledException.getMessage()));
            throw unhandledException;
        }
    }
}
