package org.seng302.project.service_layer.service;

import org.seng302.project.repository_layer.model.AdminNotification;
import org.seng302.project.repository_layer.model.User;
import org.seng302.project.repository_layer.model.UserNotification;
import org.seng302.project.repository_layer.repository.AdminNotificationRepository;
import org.seng302.project.repository_layer.repository.UserNotificationRepository;
import org.seng302.project.repository_layer.repository.UserRepository;
import org.seng302.project.service_layer.dto.notifications.DeleteUserNotificationDTO;
import org.seng302.project.service_layer.exceptions.NoNotificationExistsException;
import org.seng302.project.service_layer.exceptions.NotAcceptableException;
import org.seng302.project.service_layer.exceptions.dgaa.ForbiddenSystemAdminActionException;
import org.seng302.project.service_layer.exceptions.notification.ForbiddenNotificationActionException;
import org.seng302.project.service_layer.exceptions.user.ForbiddenUserException;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class.getName());
    private final UserRepository userRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final AdminNotificationRepository adminNotificationRepository;

    private final UserService userService;


    @Autowired
    public NotificationService(UserRepository userRepository, UserNotificationRepository userNotificationRepository,
                               UserService userService, AdminNotificationRepository adminNotificationRepository) {
        this.userRepository = userRepository;
        this.userNotificationRepository = userNotificationRepository;
        this.adminNotificationRepository = adminNotificationRepository;
        this.userService = userService;
    }

    /**
     * Gets a list of notifications assigned to the user.
     *
     * @param userId  ID of the user to get the notifications of.
     * @param appUser AppUserDetails from spring security
     * @return List of notifications assigned to the user.
     */
    public List<UserNotification> getUserNotifications(Integer userId, AppUserDetails appUser) {
        try {
            logger.info("Request to get all notifications for user with ID: {}", userId);

            Optional<User> user = userRepository.findById(userId);

            userService.checkForbidden(userId, appUser);

            //check that the user exists.
            if (user.isEmpty()) {
                throw new NotAcceptableException(String.format("No User exists with ID %d", userId));
            }

            var notifications = userNotificationRepository.findAllByUser(user.get());
            for (var notification: notifications) {
                notification.getUser().setLikedSaleListings(Collections.emptyList());
            }
            return notifications;
        } catch (ForbiddenNotificationActionException | NotAcceptableException handledException) {
            logger.error(handledException.getMessage());
            throw handledException;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while retrieving user's notifications: %s", unhandledException.getMessage()));
            throw unhandledException;
        }
    }

    /**
     * Deletes a notification.
     *
     * @param dto DTO containing the userId, notificationId and the appUser details
     */
    public void deleteUserNotification(DeleteUserNotificationDTO dto) {
        try {
            logger.info("Request to delete notification with ID: {} for user with ID: {}", dto.getNotificationId(), dto.getUserId());

            Optional<User> user = userRepository.findById(dto.getUserId());

            userService.checkForbidden(dto.getUserId(), dto.getAppUser());

            //check that the user exists.
            if (user.isEmpty()) {
                throw new NotAcceptableException(String.format("No User exists with ID %d", dto.getUserId()));
            }

            Optional<UserNotification> notification = userNotificationRepository.findById(dto.getNotificationId());
            //check that the notification exists.
            if (notification.isEmpty()) {
                throw new NotAcceptableException(String.format("No User Notification exists with ID %d", dto.getNotificationId()));
            }

            //Delete notification and return a 200 response
            userNotificationRepository.delete(notification.get());
        } catch (ForbiddenNotificationActionException | NotAcceptableException handledException) {
            logger.error(handledException.getMessage());
            throw handledException;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while deleting user's notification: %s", unhandledException.getMessage()));
            throw unhandledException;
        }

    }

    /**
     * Gets all admin notifications from the repository and returns them
     *
     * @param appUser User attempting to get notifications
     * @return a list of all admin notifications
     */
    public List<AdminNotification> getAdminNotifications(AppUserDetails appUser) {
        try {
            var requestUser = userRepository.findByEmail(appUser.getUsername()).get(0);
            if (!requestUser.isGAA()) {
                throw new ForbiddenSystemAdminActionException();
            }
            return adminNotificationRepository.findAll();
        } catch (ForbiddenSystemAdminActionException handledException) {
            logger.warn(handledException.getMessage());
            throw handledException;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while deleting user's notification: %s", unhandledException.getMessage()));
            throw unhandledException;
        }
    }

    /**
     * Deletes an admin notification
     *
     * @param notificationId ID of the admin notification to delete
     * @param appUser        the User trying to delete the notification
     */
    public void deleteAdminNotification(Integer notificationId, AppUserDetails appUser) {
        try {
            var requestUser = userRepository.findByEmail(appUser.getUsername()).get(0);
            if (!requestUser.isGAA()) {
                throw new ForbiddenSystemAdminActionException();
            }
            if (adminNotificationRepository.findById(notificationId).isEmpty()) {
                throw new NoNotificationExistsException(notificationId);
            }
            adminNotificationRepository.deleteById(notificationId);
        } catch (ForbiddenSystemAdminActionException | NoNotificationExistsException handledException) {
            logger.warn(handledException.getMessage());
            throw handledException;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while deleting user's notification: %s", unhandledException.getMessage()));
            throw unhandledException;
        }
    }

    /**
     * Marks a user notification as read
     *
     * @param read boolean stating whether the notification is to be changed to read or unread (should be true)
     * @param userId ID of the user to unread the user notification for
     * @param notificationId ID of the user notification to unread
     * @param appUser The user trying to unread the user notification
     */
    public void readUserNotification(boolean read, Integer userId, Integer notificationId, AppUserDetails appUser) {
        logger.info("Request to read user notification with ID: {} for user with ID: {}", notificationId, userId);

        Optional<User> userOptional = userRepository.findById(userId);
        Optional<UserNotification> userNotificationOptional = userNotificationRepository.findById(notificationId);
        UserNotification userNotification;

        if (userOptional.isEmpty()) {
            String error = String.format("No user exists with ID: %d", userId);
            logger.warn(error);
            throw new NotAcceptableException(error);
        } else if (userNotificationOptional.isEmpty()) {
            String error = String.format("No user notification exists with ID: %d", notificationId);
            logger.warn(error);
            throw new NotAcceptableException(error);
        } else {
            userNotification = userNotificationOptional.get();
        }

        userService.checkForbidden(userId, appUser);

        userNotification.setRead(read);
        userNotificationRepository.save(userNotification);
    }

    /**
     * Marks an admin notification as read
     *
     * @param read boolean stating whether the admin notification is to be changed to read or unread (should be true)
     * @param notificationId ID of the admin notification to read
     * @param appUser The user trying to read the admin notification
     */
    public void readAdminNotification(boolean read, Integer notificationId, AppUserDetails appUser) {
        logger.info("Request to read admin notification with ID: {}", notificationId);

        Optional<AdminNotification> adminNotificationOptional = adminNotificationRepository.findById(notificationId);
        AdminNotification adminNotification;

        if (adminNotificationOptional.isEmpty()) {
            String error = String.format("No admin notification exists with ID: %d", notificationId);
            logger.warn(error);
            throw new NotAcceptableException(error);
        } else {
            adminNotification = adminNotificationOptional.get();
        }

        String userEmail = appUser.getUsername();
        var loggedInUser = userRepository.findByEmail(userEmail).get(0);

        if (!loggedInUser.isGAA()) {
            throw new ForbiddenSystemAdminActionException();
        }

        adminNotification.setRead(read);
        adminNotificationRepository.save(adminNotification);
    }
}
