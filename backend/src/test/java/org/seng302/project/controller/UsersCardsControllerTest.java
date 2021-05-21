package org.seng302.project.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.project.model.Card;
import org.seng302.project.model.CardRepository;
import org.seng302.project.model.User;
import org.seng302.project.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Class for testing GET /users/{id}/cards.
 * Separated from the CardControllerTest class so the tests can be setup with mocking.
 */
@WebMvcTest(CardController.class)
public class UsersCardsControllerTest {

    private User testUser;
    private User otherUser;
    private Card testUsersCard1;
    private Card testUsersCard2;
    private Card otherUsersCard;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardRepository cardRepository;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private CardController cardController;

    @BeforeEach
    void setup() {
        // Create mock user 1
        testUser = new User("John", "Smith", "Bob", "Jonny",
                "Likes long walks on the beach", "test@gmail.com", "1999-04-27",
                "+64 3 555 0129", null, "");
        testUser.setId(1);
        given(userRepository.findByEmail("test@gmail.com")).willReturn(List.of(testUser));

        // Create mock user 2
        otherUser = new User("John", "Smith", "Bob", "Jonny",
                "Likes long walks on the beach", "other@gmail.com", "1999-04-27",
                "+64 3 555 0129", null, "");
        otherUser.setId(2);
        given(userRepository.findByEmail("other@gmail.com")).willReturn(List.of(otherUser));

        // Create mock cards
        // Card 1
        testUsersCard1 = new Card(testUser, "ForSale", "Test Card", "Test card description");
        testUsersCard1.setId(1);
        given(cardRepository.findById(testUsersCard1.getId())).willReturn(Optional.of(testUsersCard1));

        // Card 2
        testUsersCard2 = new Card(testUser, "Wanted", "Test Card 2", "Test card 2 description");
        testUsersCard2.setId(2);
        given(cardRepository.findById(testUsersCard2.getId())).willReturn(Optional.of(testUsersCard2));

        // Other users card
        otherUsersCard = new Card(otherUser, "ForSale", "Other users card", "Other card description");
        otherUsersCard.setId(3);
        given(cardRepository.findById(otherUsersCard.getId())).willReturn(Optional.of(otherUsersCard));
    }

    /**
     * Checks that a 401 response is sent when the request is made by and unauthenticated user.
     */
    @Test
    public void getAllCardsByUser_checkUnauthenticated401() throws Exception {
        mockMvc.perform(get("/users/{id}/cards", testUser.getId()))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Checks a 406 response is sent when the userId doesn't exist.
     */
    @Test
    public void getAllCardsByUser_checkNonExistentUser406() throws Exception {
        // Mock a user that doesn't exist
        given(userRepository.findByEmail("notAUser@gmail.com")).willReturn(List.of());
        given(userRepository.findById(3)).willReturn(Optional.empty());

        // Make the request
        mockMvc.perform(get("/users/{id}/cards", 3)
                .with(user(testUser.getEmail())))
                .andExpect(status().isNotAcceptable());
    }

    /**
     * Checks a 400 response is sent when the userId is a string.
     */
    @Test
    public void getAllCardsByUser_checkInvalidUserId400() throws Exception {
        mockMvc.perform(get("/users/{id}/cards", "beans")
                .with(user(testUser.getEmail())))
                .andExpect(status().isBadRequest());
    }

    /**
     * Checks a 200 response is sent when a valid request is made.
     */
    @Test
    public void getAllCardsByUser_checkValidRequest200() throws Exception {
        mockMvc.perform(get("/users/{id}/cards", testUser.getId())
                .with(user(testUser.getEmail())))
                .andExpect(status().isOk());
    }

    /**
     * Only cards from the requested userId are returned.
     */
    @Test
    void getAllCardsByUser_onlyCardsCreatedByCreatorWithId() {
        // Mock the card repository call
        List<Card> expectedCards = List.of(testUsersCard1, testUsersCard2);
        given(cardRepository.findAllByCreator(testUser)).willReturn(expectedCards);

        // Run the method
        List<Card> cards = cardController.getAllCardsByUser(testUser.getId());

        // Check the repository was called
        verify(cardRepository, times(1)).findAllByCreator(testUser);

        // Check the expected cards were returned
        assertEquals(expectedCards, cards);
    }

    /**
     * Checks only cards that are active are returned.
     */
    @Test
    void getAllCardsByUser_onlyActiveCardsReturned() {
        // Create, and mock, an expired card
        Card expiredCard = new Card(testUser, "Wanted", "Test Card 2", "Test card 2 description");
        expiredCard.setId(4);
        LocalDateTime timeInPast = LocalDateTime.now().minus(2, ChronoUnit.HOURS);
        expiredCard.setDisplayPeriodEnd(timeInPast);
        given(cardRepository.findById(expiredCard.getId())).willReturn(Optional.of(expiredCard));

        // Mock the card repository call
        given(cardRepository.findAllByCreator(testUser)).willReturn(List.of(testUsersCard1, expiredCard));

        // Run the method
        List<Card> cards = cardController.getAllCardsByUser(testUser.getId());

        // Check the repository was called
        verify(cardRepository, times(1)).findAllByCreator(testUser);

        // Check the expected cards were returned
        assertEquals(List.of(testUsersCard1), cards);
    }



}
