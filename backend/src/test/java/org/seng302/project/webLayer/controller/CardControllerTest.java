package org.seng302.project.webLayer.controller;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repositoryLayer.model.Card;
import org.seng302.project.serviceLayer.exceptions.BadRequestException;
import org.seng302.project.serviceLayer.exceptions.NoUserExistsException;
import org.seng302.project.serviceLayer.exceptions.business.BusinessNotFoundException;
import org.seng302.project.serviceLayer.exceptions.card.ForbiddenCardActionException;
import org.seng302.project.serviceLayer.exceptions.card.NoCardExistsException;
import org.seng302.project.serviceLayer.service.CardService;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.seng302.project.repositoryLayer.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Class for testing CardController
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
class CardControllerTest extends AbstractInitializer {

    private User testUser;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @BeforeEach
    void setup() {
        this.initialise();
        testUser = this.getTestUser();
    }

    /**
     * Checks that a 401 response is sent when the request is made by and unauthenticated user.
     */
    @Test
    void getAllCardsByUser_checkUnauthenticated401() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/users/{id}/cards", testUser.getId());

        mockMvc.perform(request).andExpect(status().isUnauthorized());
    }

    /**
     * Checks a 406 response is sent when the userId doesn't exist.
     */
    @Test
    void getAllCardsByUser_checkNonExistentUser406() throws Exception {
        when(cardService.getAllCardsByUser(any(Integer.class)))
                .thenThrow(new NoUserExistsException(3));

        RequestBuilder request = MockMvcRequestBuilders
                .get("/users/{id}/cards", 3)
                .with(user(testUser.getEmail()));

        mockMvc.perform(request).andExpect(status().isNotAcceptable());
    }

    /**
     * Checks a 400 response is sent when the userId is a string.
     */
    @Test
    void getAllCardsByUser_checkInvalidUserId400() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/users/{id}/cards", "beans")
                .with(user(testUser.getEmail()));

        mockMvc.perform(request).andExpect(status().isBadRequest());
    }

    /**
     * Checks a 200 response is sent when a valid request is made.
     */
    @Test
    void getAllCardsByUser_checkValidRequest200() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/users/{id}/cards", testUser.getId())
                .with(user(testUser.getEmail()));

        mockMvc.perform(request).andExpect(status().isOk());
    }

    /**
     * 406 response for getting a single card
     * (card doesn't exist)
     */
    @Test
    void getCard_DoesntExist_406() throws Exception {
        when(cardService.getCard(any(Integer.class)))
                .thenThrow(new NoCardExistsException(13));

        RequestBuilder request = MockMvcRequestBuilders
                .get("/cards/{id}", 12)
                .with(user(testUser.getEmail()));

        mockMvc.perform(request).andExpect(status().isNotAcceptable());
    }

    /**
     * 401 response case for getting a single card
     */
    @Test
    void getCard_checkUnauthenticatedRequest() throws Exception {
        mockMvc.perform(get("/cards/{id}", 1))
                .andExpect(status().isUnauthorized());
    }

    /**
     * 401 response case for extending a card's display period
     */
    @Test
    void extendCardDisplayPeriod_checkUnauthenticatedRequest() throws Exception {
        mockMvc.perform(put("/cards/{id}/extenddisplayperiod", 1))
                .andExpect(status().isUnauthorized());
    }

    /**
     * 403 response for extending a card's display period
     */
    @Test
    void testExtendCard_forbidden403() throws Exception {
        doThrow(new ForbiddenCardActionException()).when(cardService)
                .extendCardDisplayPeriod(any(Integer.class), any(AppUserDetails.class));

        RequestBuilder extendCardRequest = MockMvcRequestBuilders
                .put("/cards/{id}/extenddisplayperiod", 18)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(extendCardRequest)
                .andExpect(MockMvcResultMatchers.status().isForbidden()) // We expect a 403 response
                .andReturn();
    }

    /**
     * 406 response for deleting a card
     * When a card doesn't exist
     */
    @Test
    void deleteCard_cardDoesntExist_406() throws Exception {
        doThrow(new NoCardExistsException(1)).when(cardService)
                .deleteCard(any(Integer.class), any(AppUserDetails.class));

        RequestBuilder deleteCardRequest = MockMvcRequestBuilders
                .delete("/cards/{id}/", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

       mockMvc.perform(deleteCardRequest)
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable()); // We expect a 406 response
    }

    /**
     * Creating a card with the bare minimum required fields.
     */
    @Test
    void createCard_bareMinimum201() throws Exception {
        JSONObject testCardJson = new JSONObject();
        testCardJson.put("creatorId", testUser.getId());
        testCardJson.put("section", "ForSale");
        testCardJson.put("title", "1982 Lada Samara");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/cards")
                .content(testCardJson.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser))))
                .andExpect(status().isCreated());
    }

    /**
     * Check a 400 is returned when the title section is missing.
     */
    @Test
    void createCard_missingTitle400() throws Exception {
        JSONObject testCardJson = new JSONObject();
        testCardJson.put("creatorId", testUser.getId());
        testCardJson.put("section", "ForSale");
        testCardJson.put("keywords", "word");

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
    void createCard_missingSection400() throws Exception {
        JSONObject testCardJson = new JSONObject();
        testCardJson.put("creatorId", testUser.getId());
        testCardJson.put("title", "1982 Lada Samara");
        testCardJson.put("keywords", "word");

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
    void createCard_missingCreatorId400() throws Exception {
        JSONObject testCardJson = new JSONObject();
        testCardJson.put("section", "ForSale");
        testCardJson.put("title", "1982 Lada Samara");
        testCardJson.put("keywords", "word");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/cards")
                .content(testCardJson.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchCard_successful_200() throws Exception {
        Mockito.when(cardService.searchCards(
                Mockito.any(String.class), Mockito.any(List.class), Mockito.any(Boolean.class))
        ).thenReturn(Collections.emptyList());

        RequestBuilder request = MockMvcRequestBuilders
                .get("/cards/search?section=ForSale&keywordIds=1&union=true")
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(request).andExpect(status().isOk());
    }

    @Test
    void searchCard_badRequest_400() throws Exception {
        Mockito.when(cardService.searchCards(
                Mockito.any(String.class), Mockito.any(List.class), Mockito.any(Boolean.class))
        ).thenThrow(new BadRequestException("Test exception"));

        RequestBuilder request = MockMvcRequestBuilders
                .get("/cards/search?section=ForSale&keywordIds=1&union=true")
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(request).andExpect(status().isBadRequest());
    }

    @Test
    void searchCard_notLoggedIn_401() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/cards/search?section=ForSale&keywordIds=1&union=true");

        mockMvc.perform(request).andExpect(status().isUnauthorized());
    }

}
