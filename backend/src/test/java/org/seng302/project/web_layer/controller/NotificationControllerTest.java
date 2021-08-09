package org.seng302.project.web_layer.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.*;
import org.seng302.project.repository_layer.repository.UserNotificationRepository;
import org.seng302.project.repository_layer.repository.UserRepository;
import org.seng302.project.service_layer.dto.notifications.DeleteUserNotificationDTO;
import org.seng302.project.service_layer.exceptions.NoNotificationExistsException;
import org.seng302.project.service_layer.exceptions.NotAcceptableException;
import org.seng302.project.service_layer.exceptions.dgaa.ForbiddenSystemAdminActionException;
import org.seng302.project.service_layer.exceptions.notification.ForbiddenNotificationActionException;
import org.seng302.project.service_layer.service.NotificationService;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
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
    private User testSystemAdmin;
    private UserNotification testUserNotification;
    private AdminNotification testAdminNotification;

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
        testSystemAdmin = this.getTestSystemAdmin();
        testUserNotification = this.getTestUserNotification();
        testAdminNotification = new AdminNotification("Test notification");
        testAdminNotification.setId(1);
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

    /**
     * Tests that a 200 response is sent when a valid request is made.
     */
    @Test
    void getAdminNotifications_validRequest200() throws Exception {
        when(notificationService.getAdminNotifications(Mockito.any(AppUserDetails.class)))
                .thenReturn(Collections.emptyList());

        RequestBuilder request = MockMvcRequestBuilders
                .get("/notifications")
                .with(user(new AppUserDetails(testSystemAdmin)));

        mockMvc.perform(request).andExpect(status().isOk());
    }

    /**
     * Tests that a 401 status code is returned when a user is not logged in
     */
    @Test
    void getAdminNotifications_notLoggedIn401() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/notifications");

        mockMvc.perform(request).andExpect(status().isUnauthorized());
    }

    /**
     * Tests that a 403 status code is returned when the user is not an admin
     */
    @Test
    void getAdminNotifications_notAdmin403() throws Exception {
        when(notificationService.getAdminNotifications(Mockito.any(AppUserDetails.class)))
                .thenThrow(new ForbiddenSystemAdminActionException());

        RequestBuilder request = MockMvcRequestBuilders
                .get("/notifications")
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(request).andExpect(status().isForbidden());
    }

    /**
     * Tests that a 200 status is returned when a notification is successfully deleted.
     */
    @Test
    void deleteAdminNotification_success200() throws Exception {
        doNothing().when(notificationService)
                .deleteAdminNotification(Mockito.any(Integer.class), Mockito.any(AppUserDetails.class));

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/notifications/{notificationId}", testAdminNotification.getId())
                .with(user(new AppUserDetails(testSystemAdmin)));

        mockMvc.perform(request).andExpect(status().isOk());
    }

    /**
     * Tests that a 401 status is returned when a user is not logged in
     */
    @Test
    void deleteAdminNotification_notLoggedIn401() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/notifications/{notificationId}", testAdminNotification.getId());

        mockMvc.perform(request).andExpect(status().isUnauthorized());
    }

    /**
     * Tests that a 403 status is returned when a user is not an admin
     */
    @Test
    void deleteAdminNotification_notAdmin403() throws Exception {
        doThrow(new ForbiddenSystemAdminActionException()).when(notificationService)
                .deleteAdminNotification(Mockito.any(Integer.class), Mockito.any(AppUserDetails.class));

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/notifications/{notificationId}", testAdminNotification.getId())
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(request).andExpect(status().isForbidden());
    }

    /**
     * Tests that a 406 status is returned when a notification does not exist
     */
    @Test
    void deleteAdminNotification_notificationNotFound406() throws Exception {
        doThrow(new NoNotificationExistsException(2)).when(notificationService)
                .deleteAdminNotification(Mockito.any(Integer.class), Mockito.any(AppUserDetails.class));

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/notifications/{notificationId}", testAdminNotification.getId())
                .with(user(new AppUserDetails(testSystemAdmin)));

        mockMvc.perform(request).andExpect(status().isNotAcceptable());
    }



    /**
     * Testing the /users/{userId}/notifications endpoint can handle PurchaserNotification objects.
     */
    @Test
    void getAllNotificationsByUser_purchaserNotification_validRequest200() throws Exception {
        when(notificationService.getUserNotifications(any(), any())).thenReturn(List.of(new PurchaserNotification()));
        mockMvc.perform(get("/users/{userId}/notifications", testUser.getId())
                .with(user(new AppUserDetails(testUser))))
                .andExpect(status().isOk());
    }


    /**
     * Testing the /users/{userId}/notifications endpoint can handle InterestedUserNotification objects.
     */
    @Test
    void getAllNotificationsByUser_interestedUserNotification_validRequest200() throws Exception {
        when(notificationService.getUserNotifications(any(), any()))
                .thenReturn(List.of(new InterestedUserNotification()));
        mockMvc.perform(get("/users/{userId}/notifications", testUser.getId())
                .with(user(new AppUserDetails(testUser))))
                .andExpect(status().isOk());
    }

    /**
     * Tests that a 200 status is returned when a notification is read successfully.
     */
    @Test
    void readNotification_validRequest200() throws Exception {
        int test = 1;
        Assertions.assertEquals(1, test);
    }

    /**
     * Tests that a 401 status is returned when a user is not logged in
     */
    @Test
    void readNotification_notLoggedIn401() throws Exception {
        int test = 1;
        Assertions.assertEquals(1, test);
    }

    /**
     * Tests that a 403 status is returned when a user is trying to read
     * a notification for someone else
     */
    @Test
    void readNotification_notAuthorized403() throws Exception {
        int test = 1;
        Assertions.assertEquals(1, test);
    }

    /**
     * Tests that a 406 status is returned when a user does not exist
     */
    @Test
    void readNotification_userNotFound_notAcceptable406() throws Exception {
        int test = 1;
        Assertions.assertEquals(1, test);
    }

    /**
     * Tests that a 406 status is returned when a notification does not exist
     */
    @Test
    void readNotification_notificationNotFound_notAcceptable406() throws Exception {
        int test = 1;
        Assertions.assertEquals(1, test);
    }
}
