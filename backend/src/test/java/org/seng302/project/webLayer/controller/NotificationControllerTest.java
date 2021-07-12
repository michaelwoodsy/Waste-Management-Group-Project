package org.seng302.project.webLayer.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repositoryLayer.model.*;
import org.seng302.project.repositoryLayer.repository.UserNotificationRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.dto.notifications.DeleteUserNotificationDTO;
import org.seng302.project.serviceLayer.exceptions.NotAcceptableException;
import org.seng302.project.serviceLayer.exceptions.notification.ForbiddenNotificationActionException;
import org.seng302.project.serviceLayer.service.NotificationService;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Unit tests for NotificationController class.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class NotificationControllerTest extends AbstractInitializer {

    private User testUser;

    private UserNotification testUserNotification;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private UserNotificationRepository userNotificationRepository;

    @MockBean
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        this.initialise();

        testUser = this.getTestUser();
        testUserNotification = this.getTestUserNotification();
    }

    /**
     * Checks that a 401 response is sent when the request is made by an unauthenticated user.
     */
    @Test
    void getUserNotifications_Unauthenticated401() throws Exception {
        mockMvc.perform(get("/users/{userId}/notifications", testUser.getId()))
                .andExpect(status().isUnauthorized());
    }


    /**
     * Tries to get the user notifications when logged in user does not match user notifications to be retrieved for..
     * Expects a 403 forbidden response.
     */
    @Test
    void getUserNotifications_Forbidden403() throws Exception {
        // check loggedInUser not match request UserId
        Mockito.when(notificationService.getUserNotifications(Mockito.any(Integer.class),
                Mockito.any(AppUserDetails.class)))
                .thenThrow(new ForbiddenNotificationActionException());

        mockMvc.perform(get("/users/{userId}/notifications", testUser.getId())
                .with(user(new AppUserDetails(testUser))))
                .andExpect(status().isForbidden());
    }

    /**
     * Tries to get the user notifications when logged in as system admin and when the userId does not exist
     */
    @Test
    void getUserNotifications_NotFound406() throws Exception {
        // check loggedInUser not match request UserId
        Mockito.when(notificationService.getUserNotifications(Mockito.any(Integer.class),
                Mockito.any(AppUserDetails.class)))
                .thenThrow(new ForbiddenNotificationActionException());

        mockMvc.perform(get("/users/{userId}/notifications", testUser.getId())
                .with(user(new AppUserDetails(testUser))))
                .andExpect(status().isForbidden());
    }

    /**
     * Checks a 200 response is sent when a valid request is made.
     */
    @Test
    void getAllNotificationsByUser_ValidRequest200() throws Exception {
        mockMvc.perform(get("/users/{userId}/notifications", testUser.getId())
                .with(user(new AppUserDetails(testUser))))
                .andExpect(status().isOk());
    }

    /**
     * Tests deleting a notification when a user is not authenticated
     * Checks that a 401 response is sent when the request is made by an unauthenticated user.
     */
    @Test
    void deleteUserNotification_Unauthenticated401() throws Exception {
        mockMvc.perform(delete("/users/{userId}/notifications/{notificationId}", testUser.getId(), testUserNotification.getId()))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests deleting a notification when a user is not authorized
     * Checks that a 401 response is sent when the request is made by an unauthenticated user.
     */
    @Test
    void deleteUserNotification_NotAuthorized403() throws Exception {
        doThrow(ForbiddenNotificationActionException.class)
                .when(notificationService)
                .deleteUserNotification(Mockito.any(DeleteUserNotificationDTO.class));

        mockMvc.perform(delete("/users/{userId}/notifications/{notificationId}", testUser.getId(), testUserNotification.getId())
                .with(user(new AppUserDetails(testUser))))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests deleting a notification when a user or notification does not exist
     * Checks that a 406 response is sent when the request is made by an unauthenticated user.
     */
    @Test
    void deleteUserNotification_NotAcceptable406() throws Exception {
        doThrow(NotAcceptableException.class)
                .when(notificationService)
                .deleteUserNotification(Mockito.any(DeleteUserNotificationDTO.class));

        mockMvc.perform(delete("/users/{userId}/notifications/{notificationId}", testUser.getId(), testUserNotification.getId())
                .with(user(new AppUserDetails(testUser))))
                .andExpect(status().isNotAcceptable());
    }

    /**
     * Checks a 200 response is sent when a valid request is made.
     */
    @Test
    void deleteUserNotification_ValidRequest200() throws Exception {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(userNotificationRepository.findById(testUserNotification.getId())).thenReturn(Optional.of(testUserNotification));

        mockMvc.perform(delete("/users/{userId}/notifications/{notificationId}", testUser.getId(), testUserNotification.getId())
                .with(user(new AppUserDetails(testUser))))
                .andExpect(status().isOk());
    }
}
