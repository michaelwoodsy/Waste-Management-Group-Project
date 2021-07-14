package org.seng302.project.serviceLayer.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repositoryLayer.model.*;
import org.seng302.project.repositoryLayer.repository.AdminNotificationRepository;
import org.seng302.project.repositoryLayer.repository.UserNotificationRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.dto.notifications.DeleteUserNotificationDTO;
import org.seng302.project.serviceLayer.exceptions.ForbiddenActionException;
import org.seng302.project.serviceLayer.exceptions.NoNotificationExistsException;
import org.seng302.project.serviceLayer.exceptions.NotAcceptableException;
import org.seng302.project.serviceLayer.exceptions.dgaa.ForbiddenSystemAdminActionException;
import org.seng302.project.serviceLayer.exceptions.notification.ForbiddenNotificationActionException;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * Test class for performing unit tests for the MessageService class and its methods.
 */
@SpringBootTest
class NotificationServiceTest extends AbstractInitializer {

    @Autowired
    private NotificationService notificationService;

    @MockBean
    private UserNotificationRepository userNotificationRepository;

    @MockBean
    private AdminNotificationRepository adminNotificationRepository;

    @MockBean
    private UserRepository userRepository;

    private User testUser;
    private  User testSystemAdmin;
    private User otherUser;
    private Integer noUserId;
    private Integer noNotificationId;
    private Integer noAdminNotificationId;
    private UserNotification testNotification;
    private AdminNotification testAdminNotification;

    /**
     * Initialises entities from AbstractInitializer
     */
    @BeforeEach
    void setup() {
        this.initialise();
        testUser = this.getTestUser();
        testSystemAdmin = this.getTestSystemAdmin();
        otherUser = this.getTestOtherUser();
        noUserId = 1000;
        noNotificationId = 1000;
        noAdminNotificationId = 1500;
        testNotification = this.getTestUserNotification();
        testAdminNotification = new AdminNotification("Test admin notification");
        testAdminNotification.setId(2);
        this.mocks();
    }

    /**
     * Sets up mocks used by multiple tests.
     */
    void mocks() {
        when(userRepository.findByEmail(testUser.getEmail()))
                .thenReturn(List.of(testUser));
        when(userRepository.findById(testUser.getId()))
                .thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail(otherUser.getEmail()))
                .thenReturn(List.of(otherUser));
        when(userRepository.findByEmail(testSystemAdmin.getEmail()))
                .thenReturn(List.of(testSystemAdmin));
        when(userRepository.findById(noUserId))
                .thenReturn(Optional.empty());
        when(userNotificationRepository.findById(noNotificationId))
                .thenReturn(Optional.empty());
        when(userNotificationRepository.findById(testNotification.getId()))
                .thenReturn(Optional.of(testNotification));
        when(adminNotificationRepository.findAll())
                .thenReturn(List.of(testAdminNotification));
        when(adminNotificationRepository.findById(testAdminNotification.getId()))
                .thenReturn(Optional.of(testAdminNotification));
        when(adminNotificationRepository.findById(noAdminNotificationId))
                .thenReturn(Optional.empty());
    }


    /**
     * Tests the successful retrieval of a notification
     */
    @Test
    void getNotification_success() {
        given(userNotificationRepository.findAllByUser(testUser))
                .willReturn(List.of(testNotification));

        List<UserNotification> returnedNotifications = notificationService.getUserNotifications(testUser.getId(), new AppUserDetails(testUser));

        Assertions.assertEquals(1, returnedNotifications.size());
        Assertions.assertEquals("This is a notification message", returnedNotifications.get(0).getMessage());
    }

    /**
     * Tests the unsuccessful retrieval of a notification with forbidden exception
     */
    @Test
    void getNotification_forbidden() {
        Integer userId = testUser.getId();
        AppUserDetails appUser = new AppUserDetails(otherUser);
        Assertions.assertThrows(ForbiddenNotificationActionException.class,
                () -> notificationService.getUserNotifications(userId, appUser));
        Assertions.assertThrows(ForbiddenActionException.class,
                () -> notificationService.getUserNotifications(testUser.getId(), new AppUserDetails(otherUser)));
    }

    /**
     * Tests the unsuccessful creation of a notification with user not found exception
     */
    @Test
    void getNotification_not_found() {
        AppUserDetails appUser = new  AppUserDetails(testSystemAdmin);
        Assertions.assertThrows(NotAcceptableException.class,
                () -> notificationService.getUserNotifications(noUserId, appUser));
    }


    /**
     * Tests the successful deletion of a notification
     */
    @Test
    void deleteNotification_success() {
        given(userRepository.findById(testUser.getId())).willReturn(Optional.of(testUser));
        given(userNotificationRepository.findById(testNotification.getId())).willReturn(Optional.of(testNotification));

        DeleteUserNotificationDTO dto = new DeleteUserNotificationDTO(
                testUser.getId(),
                testNotification.getId(),
                new AppUserDetails(testUser));

        notificationService.deleteUserNotification(dto);

        //Verifies that the delete method is called for the test notification
        verify(userNotificationRepository, times(1)).delete(testNotification);
    }

    /**
     * Tests the unsuccessful deletion of a notification with forbidden exception
     */
    @Test
    void deleteNotification_forbidden() {
        DeleteUserNotificationDTO dto = new DeleteUserNotificationDTO(
                testUser.getId(),
                testNotification.getId(),
                new AppUserDetails(otherUser));

        Assertions.assertThrows(ForbiddenActionException.class,
                () -> notificationService.deleteUserNotification(dto));
    }

    /**
     * Tests the unsuccessful deletion of a notification with user not found exception (NotAcceptableException)
     */
    @Test
    void deleteNotification_UserNotFound() {
        DeleteUserNotificationDTO dto = new DeleteUserNotificationDTO(
                noUserId,
                testNotification.getId(),
                new AppUserDetails(testSystemAdmin));

        Assertions.assertThrows(NotAcceptableException.class,
                () -> notificationService.deleteUserNotification(dto));
    }

    /**
     * Tests the unsuccessful deletion of a notification with notification not found exception (NotAcceptableException)
     */
    @Test
    void deleteNotification_NotificationNotFound() {
        DeleteUserNotificationDTO dto = new DeleteUserNotificationDTO(
                testUser.getId(),
                noNotificationId,
                new AppUserDetails(testUser));

        Assertions.assertThrows(NotAcceptableException.class,
                () -> notificationService.deleteUserNotification(dto));
    }

    /**
     * Tests that a system admin can successfully get all admin notifications
     */
    @Test
    void getAdminNotifications_success() {
        AppUserDetails appUser = new AppUserDetails(testSystemAdmin);
        List<AdminNotification> result = notificationService.getAdminNotifications(appUser);
        Assertions.assertEquals(1, result.size());
    }

    /**
     * Tests that if a regular user tries to get admin notifications, an exception is thrown
     */
    @Test
    void getAdminNotifications_userNotAdmin_throwsException() {
        AppUserDetails appUser = new AppUserDetails(testUser);
        Assertions.assertThrows(ForbiddenSystemAdminActionException.class,
                () -> notificationService.getAdminNotifications(appUser));
    }

    /**
     * Tests that a system admin can successfully delete an admin notification
     */
    @Test
    void deleteAdminNotification_success() {
        AppUserDetails appUser = new AppUserDetails(testSystemAdmin);
        notificationService.deleteAdminNotification(testAdminNotification.getId(), appUser);
        verify(adminNotificationRepository, times(1))
                .deleteById(testAdminNotification.getId());
    }

    /**
     * Tests that an exception is thrown when a non-admin user attempts to delete an admin notification
     */
    @Test
    void deleteAdminNotification_notAdmin_throwsException() {
        Integer notificationId = testAdminNotification.getId();
        AppUserDetails appUser = new AppUserDetails(testUser);
        Assertions.assertThrows(ForbiddenSystemAdminActionException.class,
                () -> notificationService.deleteAdminNotification(notificationId, appUser));
    }

    /**
     * Tests that an exception is thrown when an admin tries to delete a notification that does not exist
     */
    @Test
    void deleteAdminNotification_noNotificationExists_throwsException() {
        AppUserDetails appUser = new AppUserDetails(testSystemAdmin);
        Assertions.assertThrows(NoNotificationExistsException.class,
                () -> notificationService.deleteAdminNotification(noAdminNotificationId, appUser));
    }
}
