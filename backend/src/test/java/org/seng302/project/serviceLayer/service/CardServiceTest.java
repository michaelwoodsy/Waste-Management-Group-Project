package org.seng302.project.serviceLayer.service;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repositoryLayer.model.Card;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.CardRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.dto.card.CreateCardResponseDTO;
import org.seng302.project.serviceLayer.dto.card.GetCardResponseDTO;
import org.seng302.project.serviceLayer.exceptions.card.ForbiddenCardActionException;
import org.seng302.project.serviceLayer.exceptions.card.NoCardExistsException;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for the CardServiceClass
 */
class CardServiceTest extends AbstractInitializer {

    private User testUser;
    private User otherUser;
    private Card testUsersCard1;
    private Card testUsersCard2;
    private Card otherUsersCard;

    @Autowired
    private CardService cardService;

    @MockBean
    private CardRepository cardRepository;

    @MockBean
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        this.initialise();
        testUser = this.getTestUser();
        given(userRepository.findByEmail(testUser.getEmail())).willReturn(List.of(testUser));
        given(userRepository.findById(testUser.getId())).willReturn(Optional.of(testUser));

        otherUser = this.getTestUserBusinessAdmin();
        given(userRepository.findByEmail(otherUser.getEmail())).willReturn(List.of(otherUser));
        given(userRepository.findById(otherUser.getId())).willReturn(Optional.of(otherUser));

        // Create mock cards
        // Card 1
        testUsersCard1 = new Card(testUser, "ForSale", "Test Card", "Test card description", Collections.emptyList());
        testUsersCard1.setId(1);
        given(cardRepository.findById(testUsersCard1.getId())).willReturn(Optional.of(testUsersCard1));

        // Card 2
        testUsersCard2 = new Card(testUser, "Wanted", "Test Card 2", "Test card 2 description", Collections.emptyList());
        testUsersCard2.setId(2);
        given(cardRepository.findById(testUsersCard2.getId())).willReturn(Optional.of(testUsersCard2));

        // Other users card
        otherUsersCard = new Card(otherUser, "ForSale", "Other users card", "Other card description", Collections.emptyList());
        otherUsersCard.setId(3);
        given(cardRepository.findById(otherUsersCard.getId())).willReturn(Optional.of(otherUsersCard));
    }


    /**
     * Only cards from the requested userId are returned.
     */
    @Test
    void getAllCardsByUser_onlyCardsCreatedByCreatorWithId() {
        List<Card> expectedCards = List.of(testUsersCard1, testUsersCard2);
        given(cardRepository.findAllByCreator(testUser)).willReturn(expectedCards);

        List<GetCardResponseDTO> cards = cardService.getAllCardsByUser(testUser.getId());

        verify(cardRepository, times(1)).findAllByCreator(testUser);

        assertEquals(testUsersCard1.getTitle(), cards.get(0).getTitle());
        assertEquals(testUsersCard2.getTitle(), cards.get(1).getTitle());
    }


//
//    /**
//     * Checks expired cards are also returned.
//     */
//    @Test
//    void getAllCardsByUser_allCardsReturned() {
//        // Create, and mock, an expired card
//        Card expiredCard = new Card(testUser, "Wanted", "Test Card 2", "Test card 2 description");
//        expiredCard.setId(4);
//        LocalDateTime timeInPast = LocalDateTime.now().minus(2, ChronoUnit.HOURS);
//        expiredCard.setDisplayPeriodEnd(timeInPast);
//        given(cardRepository.findById(expiredCard.getId())).willReturn(Optional.of(expiredCard));
//
//        // Mock the card repository call
//        List<Card> expectedCards = List.of(testUsersCard1, expiredCard);
//        given(cardRepository.findAllByCreator(testUser)).willReturn(expectedCards);
//
//        // Run the method
//        List<Card> cards = cardController.getAllCardsByUser(testUser.getId());
//
//        // Check the repository was called
//        verify(cardRepository, times(1)).findAllByCreator(testUser);
//
//        // Check the expected cards were returned
//        assertEquals(List.of(testUsersCard1, expiredCard), cards);
//    }
//
//
//    /**
//     * Test GETting card from endpoint
//     */
//    @Test
//    public void getCard_testGetSingleCard() throws Exception {
//        MvcResult returnedCardResult = mockMvc.perform(get("/cards/{id}", testUsersCard1.getId())
//                .with(user(testUser.getEmail())))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        // Convert response to json
//        String returnedCardString = returnedCardResult.getResponse().getContentAsString();
//        JSONObject returnedCard = new JSONObject(returnedCardString);
//
//        // Check the returned card is the correct card
//        assertEquals(testUsersCard1.getSection(), returnedCard.get("section"));
//        assertEquals(testUsersCard1.getTitle(), returnedCard.get("title"));
//        assertEquals(testUsersCard1.getDescription(), returnedCard.get("description"));
//        assertNotNull(returnedCard.get("created"));
//        assertNotNull(returnedCard.get("displayPeriodEnd"));
//    }
//
//
//
//
//    /**
//     * Test getting card that does not exist
//     */
//    @Test
//    public void testGetCardDoesNotExist() throws Exception {
//        // Mock card with id 1 being empty
//        given(cardRepository.findById(1)).willReturn(Optional.empty());
//
//        RequestBuilder getCardRequest = MockMvcRequestBuilders
//                .get("/cards/{id}", 1)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)
//                .with(user(testUser.getEmail()));
//
//        MvcResult getCardResponse = this.mockMvc.perform(getCardRequest)
//                .andExpect(MockMvcResultMatchers.status().isNotAcceptable()) // We expect a 406 response
//                .andReturn();
//
//        String returnedExceptionString = getCardResponse.getResponse().getContentAsString();
//        Assertions.assertEquals(new NoCardExistsException(1).getMessage(), returnedExceptionString);
//    }
//
//
//
//    /**
//     * Test extend display period of card that does not exist
//     */
//    @Test
//    public void testExtendCardDoesNotExist() throws Exception {
//        // Mock card with id 1 being empty
//        given(cardRepository.findById(1)).willReturn(Optional.empty());
//
//        RequestBuilder extendCardRequest = MockMvcRequestBuilders
//                .put("/cards/{id}/extenddisplayperiod", 1)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)
//                .with(user(new AppUserDetails(testUser)));
//
//        MvcResult extendCardResponse = this.mockMvc.perform(extendCardRequest)
//                .andExpect(MockMvcResultMatchers.status().isNotAcceptable()) // We expect a 406 response
//                .andReturn();
//
//        String returnedExceptionString = extendCardResponse.getResponse().getContentAsString();
//        Assertions.assertEquals(new NoCardExistsException(1).getMessage(), returnedExceptionString);
//    }
//
//
//    /**
//     * Test extend display period of card that is not yours
//     */
//    @Test
//    public void testExtendCardForbidden() throws Exception {
//        RequestBuilder extendCardRequest = MockMvcRequestBuilders
//                .put("/cards/{id}/extenddisplayperiod", otherUsersCard.getId())
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)
//                .with(user(new AppUserDetails(testUser)));
//
//        MvcResult extendCardResponse = this.mockMvc.perform(extendCardRequest)
//                .andExpect(MockMvcResultMatchers.status().isForbidden()) // We expect a 403 response
//                .andReturn();
//
//        String returnedExceptionString = extendCardResponse.getResponse().getContentAsString();
//        Assertions.assertEquals(new ForbiddenCardActionException().getMessage(), returnedExceptionString);
//    }
//
//    /**
//     * Test successfully extend display period of card
//     */
//    @Test
//    public void testExtendCardSuccess() throws Exception {
//        LocalDateTime expectedNewEndDate = testUsersCard1.getDisplayPeriodEnd().plusWeeks(2);
//
//        mockMvc.perform(put("/cards/{id}/extenddisplayperiod", testUsersCard1.getId())
//                .with(user(new AppUserDetails(testUser))))
//                .andExpect(status().isOk());
//
//        Optional<Card> extendedCardOptional = cardRepository.findById(testUsersCard1.getId());
//        Assertions.assertTrue(extendedCardOptional.isPresent());
//        Card extendedCard = extendedCardOptional.get();
//
//        assertEquals(expectedNewEndDate.getMonthValue(), extendedCard.getDisplayPeriodEnd().getMonthValue());
//        assertEquals(expectedNewEndDate.getDayOfMonth(), extendedCard.getDisplayPeriodEnd().getDayOfMonth());
//    }
//
//    /**
//     * Test deleting a card that does not exist.
//     * Expect a 406 response with a NoCardExistsException
//     */
//    @Test
//    public void testDeleteCardDoesNotExist() throws Exception {
//        // Mock card with id 1 being empty
//        given(cardRepository.findById(1)).willReturn(Optional.empty());
//
//        RequestBuilder deleteCardRequest = MockMvcRequestBuilders
//                .delete("/cards/{id}/", 1)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)
//                .with(user(new AppUserDetails(testUser)));
//
//        MvcResult deleteCardResponse = this.mockMvc.perform(deleteCardRequest)
//                .andExpect(MockMvcResultMatchers.status().isNotAcceptable()) // We expect a 406 response
//                .andReturn();
//
//        String returnedExceptionString = deleteCardResponse.getResponse().getContentAsString();
//        Assertions.assertEquals(new NoCardExistsException(1).getMessage(), returnedExceptionString);
//    }
//
//
//
//    @Test
//    public void createAndRetrieveTestCard() throws Exception {
//        JSONObject testCardJson = new JSONObject();
//        testCardJson.put("creatorId", testUser.getId());
//        testCardJson.put("section", "ForSale");
//        testCardJson.put("title", "1982 Lada Samara");
//        testCardJson.put("keywords", "word");
//        testCardJson.put("description",
//                "Beige, suitable for a hen house. Fair condition. Some rust. As is, where is. Will swap for budgerigar.");
//
//        // Mock the save method on the cardRepository
//        given(cardRepository.save(any(Card.class))).willReturn(testUsersCard1);
//
//        mockMvc.perform(MockMvcRequestBuilders
//                .post("/cards")
//                .content(testCardJson.toString())
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)
//                .with(user(new AppUserDetails(testUser))))
//                .andExpect(status().isCreated());
//
//        // This captures the arguments given to the mocked repository
//        ArgumentCaptor<Card> argument = ArgumentCaptor.forClass(Card.class);
//        verify(cardRepository).save(argument.capture());
//
//        // This gets the actual card argument passed to the card repository
//        Card retrievedCard = argument.getValue();
//
//        assertNotNull(retrievedCard.getCreator().getId());
//        assertEquals("John", retrievedCard.getCreator().getFirstName());
//        assertEquals("Smith", retrievedCard.getCreator().getLastName());
//
//        assertEquals("ForSale", retrievedCard.getSection());
//        assertEquals("1982 Lada Samara", retrievedCard.getTitle());
//        assertEquals("Beige, suitable for a hen house. Fair condition. Some rust. As is, where is. Will swap for budgerigar.",
//                retrievedCard.getDescription());
//        assertTrue(retrievedCard.getCreated().isBefore(LocalDateTime.now()) || retrievedCard.getCreated().isEqual(LocalDateTime.now()));
//        assertTrue(retrievedCard.getCreated().isAfter(LocalDateTime.now().minusSeconds(5)));
//        assertTrue(retrievedCard.getDisplayPeriodEnd().isEqual(retrievedCard.getCreated().plusDays(14)));
//    }
//
//    /**
//     * Creating a card with the bare minimum required fields.
//     */
//    @Test
//    public void createCard_bareMinimum201() throws Exception {
//        JSONObject testCardJson = new JSONObject();
//        testCardJson.put("creatorId", testUser.getId());
//        testCardJson.put("section", "ForSale");
//        testCardJson.put("title", "1982 Lada Samara");
//
//        // Mock the save method on the cardRepository
//        given(cardRepository.save(any(Card.class))).willReturn(testUsersCard1);
//
//        mockMvc.perform(MockMvcRequestBuilders
//                .post("/cards")
//                .content(testCardJson.toString())
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)
//                .with(user(new AppUserDetails(testUser))))
//                .andExpect(status().isCreated());
//    }
//
}