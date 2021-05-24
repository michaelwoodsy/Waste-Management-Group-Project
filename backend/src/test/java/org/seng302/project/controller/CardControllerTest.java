package org.seng302.project.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.seng302.project.controller.authentication.AppUserDetails;
import org.seng302.project.exceptions.card.ForbiddenCardActionException;
import org.seng302.project.exceptions.card.NoCardExistsException;
import org.seng302.project.model.Card;
import org.seng302.project.model.CardRepository;
import org.seng302.project.model.User;
import org.seng302.project.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Class for testing GET /users/{id}/cards.
 * Separated from the CardControllerTest class so the tests can be setup with mocking.
 */
@WebMvcTest(CardController.class)
public class CardControllerTest {

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
        given(userRepository.findById(1)).willReturn(Optional.of(testUser));

        // Create mock user 2
        otherUser = new User("John", "Smith", "Bob", "Jonny",
                "Likes long walks on the beach", "other@gmail.com", "1999-04-27",
                "+64 3 555 0129", null, "");
        otherUser.setId(2);
        given(userRepository.findByEmail("other@gmail.com")).willReturn(List.of(otherUser));
        given(userRepository.findById(2)).willReturn(Optional.of(otherUser));

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
     * Checks expired cards are also returned.
     */
    @Test
    void getAllCardsByUser_allCardsReturned() {
        // Create, and mock, an expired card
        Card expiredCard = new Card(testUser, "Wanted", "Test Card 2", "Test card 2 description");
        expiredCard.setId(4);
        LocalDateTime timeInPast = LocalDateTime.now().minus(2, ChronoUnit.HOURS);
        expiredCard.setDisplayPeriodEnd(timeInPast);
        given(cardRepository.findById(expiredCard.getId())).willReturn(Optional.of(expiredCard));

        // Mock the card repository call
        List<Card> expectedCards = List.of(testUsersCard1, expiredCard);
        given(cardRepository.findAllByCreator(testUser)).willReturn(expectedCards);

        // Run the method
        List<Card> cards = cardController.getAllCardsByUser(testUser.getId());

        // Check the repository was called
        verify(cardRepository, times(1)).findAllByCreator(testUser);

        // Check the expected cards were returned
        assertEquals(List.of(testUsersCard1, expiredCard), cards);
    }

    @Test
    public void getCard_checkUnauthenticatedRequest() throws Exception {
        mockMvc.perform(get("/cards/{id}", 1))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void extendCardDisplayPeriod_checkUnauthenticatedRequest() throws Exception {
        mockMvc.perform(put("/cards/{id}/extenddisplayperiod", 1))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Test GETting card from endpoint
     */
    @Test
    public void getCard_testGetSingleCard() throws Exception {
        MvcResult returnedCardResult = mockMvc.perform(get("/cards/{id}", testUsersCard1.getId())
                .with(user(testUser.getEmail())))
                .andExpect(status().isOk())
                .andReturn();

        // Convert response to json
        String returnedCardString = returnedCardResult.getResponse().getContentAsString();
        JSONObject returnedCard = new JSONObject(returnedCardString);

        // Check the returned card is the correct card
        assertEquals(testUsersCard1.getSection(), returnedCard.get("section"));
        assertEquals(testUsersCard1.getTitle(), returnedCard.get("title"));
        assertEquals(testUsersCard1.getDescription(), returnedCard.get("description"));
        assertNotNull(returnedCard.get("created"));
        assertNotNull(returnedCard.get("displayPeriodEnd"));
    }

    /**
     * Test getting card that does not exist
     */
    @Test
    public void testGetCardDoesNotExist() throws Exception {
        // Mock card with id 1 being empty
        given(cardRepository.findById(1)).willReturn(Optional.empty());

        RequestBuilder getCardRequest = MockMvcRequestBuilders
                .get("/cards/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(testUser.getEmail()));

        MvcResult getCardResponse = this.mockMvc.perform(getCardRequest)
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable()) // We expect a 406 response
                .andReturn();

        String returnedExceptionString = getCardResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new NoCardExistsException(1).getMessage(), returnedExceptionString);
    }

    /**
     * Test extend display period of card that does not exist
     */
    @Test
    public void testExtendCardDoesNotExist() throws Exception {
        // Mock card with id 1 being empty
        given(cardRepository.findById(1)).willReturn(Optional.empty());

        RequestBuilder extendCardRequest = MockMvcRequestBuilders
                .put("/cards/{id}/extenddisplayperiod", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        MvcResult extendCardResponse = this.mockMvc.perform(extendCardRequest)
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable()) // We expect a 406 response
                .andReturn();

        String returnedExceptionString = extendCardResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new NoCardExistsException(1).getMessage(), returnedExceptionString);
    }

    /**
     * Test extend display period of card that is not yours
     */
    @Test
    public void testExtendCardForbidden() throws Exception {
        RequestBuilder extendCardRequest = MockMvcRequestBuilders
                .put("/cards/{id}/extenddisplayperiod", otherUsersCard.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        MvcResult extendCardResponse = this.mockMvc.perform(extendCardRequest)
                .andExpect(MockMvcResultMatchers.status().isForbidden()) // We expect a 403 response
                .andReturn();

        String returnedExceptionString = extendCardResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new ForbiddenCardActionException().getMessage(), returnedExceptionString);
    }

    /**
     * Test successfully extend display period of card
     */
    @Test
    public void testExtendCardSuccess() throws Exception {
        LocalDateTime expectedNewEndDate = testUsersCard1.getDisplayPeriodEnd().plusWeeks(2);

        mockMvc.perform(put("/cards/{id}/extenddisplayperiod", testUsersCard1.getId())
                .with(user(new AppUserDetails(testUser))))
                .andExpect(status().isOk());

        Optional<Card> extendedCardOptional = cardRepository.findById(testUsersCard1.getId());
        Assertions.assertTrue(extendedCardOptional.isPresent());
        Card extendedCard = extendedCardOptional.get();

        assertEquals(expectedNewEndDate.getMonthValue(), extendedCard.getDisplayPeriodEnd().getMonthValue());
        assertEquals(expectedNewEndDate.getDayOfMonth(), extendedCard.getDisplayPeriodEnd().getDayOfMonth());
    }

    /**
     * Test deleting a card that does not exist.
     * Expect a 406 response with a NoCardExistsException
     */
    @Test
    public void testDeleteCardDoesNotExist() throws Exception {
        // Mock card with id 1 being empty
        given(cardRepository.findById(1)).willReturn(Optional.empty());

        RequestBuilder deleteCardRequest = MockMvcRequestBuilders
                .delete("/cards/{id}/", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        MvcResult deleteCardResponse = this.mockMvc.perform(deleteCardRequest)
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable()) // We expect a 406 response
                .andReturn();

        String returnedExceptionString = deleteCardResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new NoCardExistsException(1).getMessage(), returnedExceptionString);
    }

    @Test
    public void createAndRetrieveTestCard() throws Exception {
        JSONObject testCardJson = new JSONObject();
        testCardJson.put("creatorId", testUser.getId());
        testCardJson.put("section", "ForSale");
        testCardJson.put("title", "1982 Lada Samara");
        testCardJson.put("description",
                "Beige, suitable for a hen house. Fair condition. Some rust. As is, where is. Will swap for budgerigar.");

        // Mock the save method on the cardRepository
        given(cardRepository.save(any(Card.class))).willReturn(testUsersCard1);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/cards")
                .content(testCardJson.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser))))
                .andExpect(status().isCreated());

        // This captures the arguments given to the mocked repository
        ArgumentCaptor<Card> argument = ArgumentCaptor.forClass(Card.class);
        verify(cardRepository).save(argument.capture());

        // This gets the actual card argument passed to the card repository
        Card retrievedCard = argument.getValue();

        assertNotNull(retrievedCard.getCreator().getId());
        assertEquals("John", retrievedCard.getCreator().getFirstName());
        assertEquals("Smith", retrievedCard.getCreator().getLastName());

        assertEquals("ForSale", retrievedCard.getSection());
        assertEquals("1982 Lada Samara", retrievedCard.getTitle());
        assertEquals("Beige, suitable for a hen house. Fair condition. Some rust. As is, where is. Will swap for budgerigar.",
                retrievedCard.getDescription());
        assertTrue(retrievedCard.getCreated().isBefore(LocalDateTime.now()) || retrievedCard.getCreated().isEqual(LocalDateTime.now()));
        assertTrue(retrievedCard.getCreated().isAfter(LocalDateTime.now().minusSeconds(5)));
        assertTrue(retrievedCard.getDisplayPeriodEnd().isEqual(retrievedCard.getCreated().plusDays(14)));
    }

    @Test
    public void createTestCardWithoutRequiredFields() {
        Assertions.assertThrows(ForbiddenCardActionException.class, () ->
                cardController.createCard(new net.minidev.json.JSONObject(), new AppUserDetails(testUser))
        );
    }

    /**
     * Creating a card with the bare minimum required fields.
     */
    @Test
    public void createCard_bareMinimum201() throws Exception {
        JSONObject testCardJson = new JSONObject();
        testCardJson.put("creatorId", testUser.getId());
        testCardJson.put("section", "ForSale");
        testCardJson.put("title", "1982 Lada Samara");
        testCardJson.put("keywords", "word");

        // Mock the save method on the cardRepository
        given(cardRepository.save(any(Card.class))).willReturn(testUsersCard1);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/cards")
                .content(testCardJson.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser))))
                .andExpect(status().isCreated());
    }

    /**
     * Check a 400 is returned when the keywords section is missing
     */
    @Test
    public void createCard_missingKeywordsField400() throws Exception {
        JSONObject testCardJson = new JSONObject();
        testCardJson.put("creatorId", testUser.getId());
        testCardJson.put("section", "ForSale");
        testCardJson.put("title", "1982 Lada Samara");

        // Mock the save method on the cardRepository
        given(cardRepository.save(any(Card.class))).willReturn(testUsersCard1);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/cards")
                .content(testCardJson.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser))))
                .andExpect(status().isBadRequest());
    }

    /**
     * Check a 400 is returned when the title section is missing.
     */
    @Test
    public void createCard_missingTitle400() throws Exception {
        JSONObject testCardJson = new JSONObject();
        testCardJson.put("creatorId", testUser.getId());
        testCardJson.put("section", "ForSale");
        testCardJson.put("keywords", "word");

        // Mock the save method on the cardRepository
        given(cardRepository.save(any(Card.class))).willReturn(testUsersCard1);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/cards")
                .content(testCardJson.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser))))
                .andExpect(status().isBadRequest());
    }

    /**
     * Check a 400 is returned when the section field is missing.
     */
    @Test
    public void createCard_missingSection400() throws Exception {
        JSONObject testCardJson = new JSONObject();
        testCardJson.put("creatorId", testUser.getId());
        testCardJson.put("title", "1982 Lada Samara");
        testCardJson.put("keywords", "word");

        // Mock the save method on the cardRepository
        given(cardRepository.save(any(Card.class))).willReturn(testUsersCard1);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/cards")
                .content(testCardJson.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser))))
                .andExpect(status().isBadRequest());
    }

    /**
     * Check a 400 is returned when the creatorId field is missing.
     */
    @Test
    public void createCard_missingCreatorId400() throws Exception {
        JSONObject testCardJson = new JSONObject();
        testCardJson.put("section", "ForSale");
        testCardJson.put("title", "1982 Lada Samara");
        testCardJson.put("keywords", "word");

        // Mock the save method on the cardRepository
        given(cardRepository.save(any(Card.class))).willReturn(testUsersCard1);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/cards")
                .content(testCardJson.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser))))
                .andExpect(status().isBadRequest());
    }
}
