package org.seng302.project.webLayer.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repositoryLayer.model.*;
import org.seng302.project.repositoryLayer.repository.CardRepository;
import org.seng302.project.repositoryLayer.repository.UserNotificationRepository;
import org.seng302.project.serviceLayer.exceptions.notification.ForbiddenNotificationActionException;
import org.seng302.project.serviceLayer.service.NotificationService;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Unit tests for NotificationController class.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class NotificationControllerTest extends AbstractInitializer {

    private User testUser;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private UserNotificationRepository userNotificationRepository;

    @BeforeEach
    public void setup() {
        this.initialise();
        // Create the users
        Address testUserAddress = new Address(null, null, null, null, "New Zealand", null);
        testUser = new User("John", "Smith", "Bob", "Jonny",
                "Likes long walks on the beach", "johnxyz@gmail.com", "1999-04-27",
                "+64 3 555 0129", testUserAddress, "1337-H%nt3r2");
        testUser.setId(1);
    }

    /**
     * Checks that a 401 response is sent when the request is made by an unauthenticated user.
     */
    @Test
    public void getNotifications_Unauthenticated401() throws Exception {
        mockMvc.perform(get("/users/{userId}/notifications", testUser.getId()))
                .andExpect(status().isUnauthorized());
    }


    /**
     * Tries to get the user notifications when logged in user does not match user notifications to be retrieved for..
     * Expects a 403 forbidden response.
     */
    @Test
    void getNotifications_Forbidden403() throws Exception {
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
    void getAllCardsByUser_ValidRequest200() throws Exception {
        mockMvc.perform(get("/users/{userId}/notifications", testUser.getId())
                .with(user(new AppUserDetails(testUser))))
                .andExpect(status().isOk());
    }

    //This was just used for my own testing
//    @Test
//    void testRepo() {
//        User user = this.getTestUser();
//        Card testCard = this.getTestCard();
//        cardRepository.save(testCard);
//
//        Card newCard = new Card(user, "Exchange", "Title", "description", Collections.emptySet());
//        cardRepository.save(newCard);
//
//        CardExpiryNotification expiryNotification = new CardExpiryNotification(user, "This card has expired.", testCard);
//        userNotificationRepository.save(expiryNotification);
//
//        CardExpiryNotification expiryNotification2 = new CardExpiryNotification(user, "This card has also expired.", newCard);
//        userNotificationRepository.save(expiryNotification2);
//
//        UserNotification userNotification = new UserNotification(user, "This is a notification");
//        userNotificationRepository.save(userNotification);
//
//
//
//        List<UserNotification> userNotificationList = userNotificationRepository.findAllByUser(user);
//
//        System.out.println(userNotificationList);
//
//    }
}
