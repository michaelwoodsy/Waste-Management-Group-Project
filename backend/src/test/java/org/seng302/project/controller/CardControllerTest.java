package org.seng302.project.controller;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.project.exceptions.NoCardExistsException;
import org.seng302.project.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CardController.class)
public class CardControllerTest {
    private User testUser;
    private Card testCard;

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
        // Create mock user
        testUser = new User("John", "Smith", "Bob", "Jonny",
                "Likes long walks on the beach", "test@gmail.com", "1999-04-27",
                "+64 3 555 0129", null, "");
        testUser.setId(1);
        given(userRepository.findByEmail("test@gmail.com")).willReturn(List.of(testUser));

        // Create mock card
        testCard = new Card(testUser, "ForSale", "Test Card", "Test card description");
        testCard.setId(1);
        given(cardRepository.findById(testCard.getId())).willReturn(Optional.of(testCard));
    }

    @Test
    public void checkUnauthenticatedRequest() throws Exception {
        mockMvc.perform(get("/cards/{id}", testCard.getId()))
                .andExpect(status().isUnauthorized());
    }

    /**
    * Test GETting card from endpoint
    */
    @Test
    public void testGetSingleCard() throws Exception {
        // Make the request
        MvcResult returnedCardResult = mockMvc.perform(get("/cards/{id}", testCard.getId())
                .with(user(testUser.getEmail())))
                .andExpect(status().isOk())
                .andReturn();

        // Check the repository was called
        verify(cardRepository, times(1)).findById(testCard.getId());

        String returnedCardString = returnedCardResult.getResponse().getContentAsString();
        JSONObject returnedCard = new JSONObject(returnedCardString);

        assertEquals(testCard.getSection(), returnedCard.get("section"));
        assertEquals(testCard.getTitle(), returnedCard.get("title"));
        assertEquals(testCard.getDescription(), returnedCard.get("description"));
        assertNotNull(returnedCard.get("created"));
        assertNotNull(returnedCard.get("displayPeriodEnd"));
    }

    /**
     * Test getting card that does not exist
     */
    @Test
    public void testGetCardDoesNotExist() throws Exception {
        //Make sure the card repository is empty
        cardRepository.deleteAll();

        RequestBuilder getCardRequest = MockMvcRequestBuilders
                .get("/cards/{id}/", 999)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(testUser.getEmail()));

        MvcResult getCardResponse = this.mockMvc.perform(getCardRequest)
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable()) // We expect a 406 response
                .andReturn();

        String returnedExceptionString = getCardResponse.getResponse().getContentAsString();
        assertEquals(new NoCardExistsException(999).getMessage(), returnedExceptionString);
    }

    /**
     * If the request is made by an unauthorized user, a 401 should be returned.
     */
    @Test
    void getAllCardsUnauthenticatedUserReturns401() throws Exception {
        mockMvc.perform(get("/cards"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Checks a 400 response is returned if the "section" parameter isn't supplied.
     */
    @Test
    void getAllCardsMissingSectionParamReturns400() throws Exception {
        mockMvc.perform(get("/cards")
                .with(user(testUser.getEmail())))
                .andExpect(status().isBadRequest());
    }

    /**
     * Checks a 400 response is returned if the "section" parameter isn't valid.
     */
    @Test
    void getAllCardsInvalidSectionParamReturns400() throws Exception {
        mockMvc.perform(get("/cards")
                .with(user(testUser.getEmail()))
                .param("section", "beans"))
                .andExpect(status().isBadRequest());
    }

    /**
     * Given an authenticated user makes the request with a 'ForSale' section parameter
     * a 200 response should be received.
     */
    @Test
    void getAllCardsForSaleSectionParameterReturns200() throws Exception {
        mockMvc.perform(get("/cards")
                .with(user(testUser.getEmail()))
                .param("section", "ForSale"))
                .andExpect(status().isOk());
    }

    /**
     * Given an authenticated user makes the request with a 'Wanted' section parameter
     * a 200 response should be received.
     */
    @Test
    void getAllCardsWantedSectionParameterReturns200() throws Exception {
        mockMvc.perform(get("/cards")
                .with(user(testUser.getEmail()))
                .param("section", "Wanted"))
                .andExpect(status().isOk());
    }

    /**
     * Given an authenticated user makes the request with a 'Exchange' section parameter
     * a 200 response should be received.
     */
    @Test
    void getAllCardsExchangeSectionParameterReturns200() throws Exception {
        mockMvc.perform(get("/cards")
                .with(user(testUser.getEmail()))
                .param("section", "Exchange"))
                .andExpect(status().isOk());
    }

    /**
     * Only cards in the "Exchange" section are returned when the section parameter is "Exchange".
     */
    @Test
    void getAllCardsExchangeSectionOnlyReturnsExchangeCards() {
        // Section to test
        String section = "Exchange";

        // Mock the card repository call
        List<Card> expectedCards = List.of(testCard);
        given(cardRepository.findAllBySection(section)).willReturn(expectedCards);

        // Run the getAllCards
        List<Card> cards = cardController.getAllCards(section);

        // Check the repository was called
        verify(cardRepository, times(1)).findAllBySection(section);
        // Check the cards were returned
        assertEquals(expectedCards, cards);
    }

    /**
     * Only cards in the "ForSale" section are returned when the section parameter is "ForSale".
     */
    @Test
    void getAllCardsForSaleSectionOnlyReturnsExchangeCards() {
        // Section to test
        String section = "ForSale";

        // Mock the card repository call
        List<Card> expectedCards = List.of(testCard);
        given(cardRepository.findAllBySection(section)).willReturn(expectedCards);

        // Run the getAllCards
        List<Card> cards = cardController.getAllCards(section);

        // Check the repository was called
        verify(cardRepository, times(1)).findAllBySection(section);
        // Check the cards were returned
        assertEquals(expectedCards, cards);
    }

    /**
     * Only cards in the "Wanted" section are returned when the section parameter is "Wanted".
     */
    @Test
    void getAllCardsWantedSectionOnlyReturnsExchangeCards() {
        // Section to test
        String section = "Wanted";

        // Mock the card repository call
        List<Card> expectedCards = List.of(testCard);
        given(cardRepository.findAllBySection(section)).willReturn(expectedCards);

        // Run the getAllCards
        List<Card> cards = cardController.getAllCards(section);

        // Check the repository was called
        verify(cardRepository, times(1)).findAllBySection(section);
        // Check the cards were returned
        assertEquals(expectedCards, cards);
    }


}
