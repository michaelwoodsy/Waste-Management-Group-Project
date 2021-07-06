package org.seng302.project.webLayer.controller;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.serviceLayer.dto.message.CreateMessageDTO;
import org.seng302.project.serviceLayer.exceptions.BadRequestException;
import org.seng302.project.serviceLayer.exceptions.NotAcceptableException;
import org.seng302.project.serviceLayer.service.MessageService;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for MessageController class.
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
class MessageControllerTest extends AbstractInitializer {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    private User testUser;

    /**
     * Initialises entities from AbstractInitializer
     */
    @BeforeEach
    void setup() {
        this.initialise();
        testUser = this.getTestUser();
    }

    /**
     * Tests that a 201 response is given on successful creation of a message
     */
    @Test
    void createMessage_success201() throws Exception {
        JSONObject requestBody = new JSONObject();
        requestBody.put("text", "Hi, how much do you want for the apples?");

        RequestBuilder createMessageRequest = MockMvcRequestBuilders
                .post("/users/{userId}/cards/{cardId}/messages",
                        testUser.getId(), 1)
                .content(requestBody.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(testUser.getEmail()));

        mockMvc.perform(createMessageRequest).andExpect(status().isCreated());

    }

    /**
     * Tests that a 401 response is given when a non-logged in user tries
     * to create a message
     */
    @Test
    void createMessage_notLoggedIn401() throws Exception {
        JSONObject requestBody = new JSONObject();
        requestBody.put("text", "Hi, how much do you want for the apples?");

        RequestBuilder createMessageRequest = MockMvcRequestBuilders
                .post("/users/{userId}/cards/{cardId}/messages",
                        testUser.getId(), 1)
                .content(requestBody.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(createMessageRequest).andExpect(status().isUnauthorized());

    }


    /**
     * Tests that a 400 response is given when the text field is missing
     */
    //TODO: this actually gives a 201
    @Test
    void createMessage_missingField_400() throws Exception {
        doThrow(new BadRequestException("Message is missing 'text' field")).when(messageService)
                .createMessage(any(CreateMessageDTO.class), any(AppUserDetails.class));

        JSONObject requestBody = new JSONObject();

        RequestBuilder createMessageRequest = MockMvcRequestBuilders
                .post("/users/{userId}/cards/{cardId}/messages",
                        testUser.getId(), 1)
                .content(requestBody.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(testUser.getEmail()));

        mockMvc.perform(createMessageRequest).andExpect(status().isBadRequest());
    }


    /**
     * Tests that a 406 response is given when the user or card doesn't exist
     */
    //TODO: this actually gives a 201
    @Test
    void createMessage_nonexistentUserOrCard_406() throws Exception {
        doThrow(new NotAcceptableException("There is no card with the id '250'")).when(messageService)
                .createMessage(any(CreateMessageDTO.class), any(AppUserDetails.class));

        JSONObject requestBody = new JSONObject();

        RequestBuilder createMessageRequest = MockMvcRequestBuilders
                .post("/users/{userId}/cards/{cardId}/messages",
                        testUser.getId(), 250)
                .content(requestBody.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(testUser.getEmail()));

        mockMvc.perform(createMessageRequest).andExpect(status().isNotAcceptable());
    }
}
