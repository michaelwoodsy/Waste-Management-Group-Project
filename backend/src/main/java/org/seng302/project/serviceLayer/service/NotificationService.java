package org.seng302.project.serviceLayer.service;

import org.seng302.project.repositoryLayer.model.AdminNotification;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.model.UserNotification;
import org.seng302.project.repositoryLayer.repository.AdminNotificationRepository;
import org.seng302.project.repositoryLayer.repository.UserNotificationRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.dto.notifications.DeleteUserNotificationDTO;
import org.seng302.project.serviceLayer.exceptions.NoNotificationExistsException;
import org.seng302.project.serviceLayer.exceptions.NotAcceptableException;
import org.seng302.project.serviceLayer.exceptions.dgaa.ForbiddenSystemAdminActionException;
import org.seng302.project.serviceLayer.exceptions.notification.ForbiddenNotificationActionException;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

            return userNotificationRepository.findAllByUser(user.get());
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
}
