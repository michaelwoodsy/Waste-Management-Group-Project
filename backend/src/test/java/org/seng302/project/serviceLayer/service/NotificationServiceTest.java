package org.seng302.project.serviceLayer.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repositoryLayer.model.*;
import org.seng302.project.repositoryLayer.repository.CardRepository;
import org.seng302.project.repositoryLayer.repository.MessageRepository;
import org.seng302.project.repositoryLayer.repository.UserNotificationRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.dto.message.CreateMessageDTO;
import org.seng302.project.serviceLayer.exceptions.BadRequestException;
import org.seng302.project.serviceLayer.exceptions.NoUserExistsException;
import org.seng302.project.serviceLayer.exceptions.NotAcceptableException;
import org.seng302.project.serviceLayer.exceptions.notification.ForbiddenNotificationActionException;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

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
    private UserRepository userRepository;

    private User testUser;
    private  User testSystemAdmin;
    private User otherUser;
    private Integer noUserId;
    private AppUserDetails appUser;
    private UserNotification testNotification;

    /**
     * Initialises entities from AbstractInitializer
     */
    @BeforeEach
    void setup() {
        this.initialise();
        testUser = this.getTestUser();
        testSystemAdmin = this.getTestSystemAdmin();
        Address address = new Address(null, null, null, null, "New Zealand", null);
        otherUser = new User(
                "John",
                "Jones",
                null,
                null,
                null,
                "john.jones@gmail.com",
                "1995/07/25",
                null,
                address,
                "password");
        otherUser.setId(2);
        otherUser.setPassword(passwordEncoder.encode(testUser.getPassword()));
        noUserId = 1000;

        Mockito.when(userRepository.findByEmail(testUser.getEmail()))
            .thenReturn(List.of(testUser));

        testNotification = this.getTestUserNotification();
        testNotification.setId(1);
        Mockito.when(userNotificationRepository.findById(testNotification.getId()))
                .thenReturn(Optional.of(testNotification));
        this.mocks();
    }

    /**
     * Sets up mocks used by multiple tests.
     */
    void mocks() {
        given(userRepository.findById(testUser.getId())).willReturn(Optional.of(testUser));
        given(userRepository.findByEmail(otherUser.getEmail())).willReturn(List.of(otherUser));
        given(userRepository.findByEmail(testSystemAdmin.getEmail())).willReturn(List.of(testSystemAdmin));
        given(userRepository.findById(noUserId)).willReturn(Optional.empty());
    }


    /**
     * Tests the successful creation of a notification
     */
    @Test
    void createMessage_success() {
        given(userNotificationRepository.findAllByUser(testUser))
                .willReturn(List.of(testNotification));

        List<UserNotification> returnedNotifications = notificationService.getUserNotifications(testUser.getId(), new AppUserDetails(testUser));

        Assertions.assertEquals(1, returnedNotifications.size());
        Assertions.assertEquals("This is a notification message", returnedNotifications.get(0).getMessage());
    }

    /**
     * Tests the unsuccessful creation of a notification with forbidden exception
     */
    @Test
    void createMessage_forbidden() {
        Assertions.assertThrows(ForbiddenNotificationActionException.class,
                () -> notificationService.getUserNotifications(testUser.getId(), new AppUserDetails(otherUser)));
    }

    /**
     * Tests the unsuccessful creation of a notification with user not found exception
     */
    @Test
    void createMessage_not_found() {
        Assertions.assertThrows(NoUserExistsException.class,
                () -> notificationService.getUserNotifications(noUserId, new AppUserDetails(testSystemAdmin)));
    }
}
