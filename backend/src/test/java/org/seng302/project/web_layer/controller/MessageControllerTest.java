package org.seng302.project.web_layer.controller;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.Message;
import org.seng302.project.repository_layer.model.User;
import org.seng302.project.service_layer.dto.message.PostMessageDTO;
import org.seng302.project.service_layer.exceptions.BadRequestException;
import org.seng302.project.service_layer.exceptions.NotAcceptableException;
import org.seng302.project.service_layer.exceptions.user.ForbiddenUserException;
import org.seng302.project.service_layer.service.MessageService;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for MessageController class.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class MessageControllerTest extends AbstractInitializer {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    private User testUser;
    private User otherTestUser;
    private List<Message> testMessages;

    private User testAdmin;

    /**
     * Initialises entities from AbstractInitializer
     */
    @BeforeEach
    void setup() {
        testUser = this.getTestUser();
        otherTestUser = this.getTestOtherUser();
        testAdmin = this.getTestSystemAdmin();
        testMessages = this.getTestMessages();
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
                .with(user(new AppUserDetails(testUser)));

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
    @Test
    void createMessage_missingField_400() throws Exception {
        when(messageService.createMessage(any(PostMessageDTO.class), any(AppUserDetails.class)))
                .thenThrow(new BadRequestException("Message is missing 'text' field"));

        JSONObject requestBody = new JSONObject();

        RequestBuilder createMessageRequest = MockMvcRequestBuilders
                .post("/users/{userId}/cards/{cardId}/messages",
                        testUser.getId(), 1)
                .content(requestBody.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(createMessageRequest).andExpect(status().isBadRequest());
    }


    /**
     * Tests that a 406 response is given when the user or card doesn't exist
     */
    @Test
    void createMessage_nonexistentUserOrCard_406() throws Exception {
        when(messageService.createMessage(any(PostMessageDTO.class), any(AppUserDetails.class)))
                .thenThrow(new NotAcceptableException("There is no card that exists with the id '250'"));

        JSONObject requestBody = new JSONObject();

        RequestBuilder createMessageRequest = MockMvcRequestBuilders
                .post("/users/{userId}/cards/{cardId}/messages",
                        testUser.getId(), 250)
                .content(requestBody.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(createMessageRequest).andExpect(status().isNotAcceptable());
    }

    /**
     * Tests that a 200 response on a successful retrieval of a user's messages
     */
    @Test
    void getMessages_success200() throws Exception {
        RequestBuilder getMessagesRequest = MockMvcRequestBuilders
                .get("/users/{userId}/messages",
                        testUser.getId())
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(getMessagesRequest).andExpect(status().isOk());
    }

    /**
     * Tests that a 401 response is thrown when a user tries to retrieve
     * a user's messages and they're not logged in.
     */
    @Test
    void getMessages_notLoggedIn_throws401() throws Exception {
        RequestBuilder getMessagesRequest = MockMvcRequestBuilders
                .get("/users/{userId}/messages",
                        testUser.getId());

        mockMvc.perform(getMessagesRequest).andExpect(status().isUnauthorized());
    }

    /**
     * Tests that a 403 response is thrown when a user tries to retrieve
     * a user's messages and it's not their own messages.
     */
    @Test
    void getMessages_wrongUser_throws403() throws Exception {
        doThrow(new ForbiddenUserException(100))
                .when(messageService)
                .getMessages(Mockito.any(Integer.class), Mockito.any(AppUserDetails.class));

        RequestBuilder getMessagesRequest = MockMvcRequestBuilders
                .get("/users/{userId}/messages",
                        100)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(getMessagesRequest).andExpect(status().isForbidden());
    }

    /**
     * Tests that a 403 response is thrown when a GAA tries to retrieve
     * a user's messages and it's not their own messages.
     */
    @Test
    void getMessages_GAAWrongUser_throws403() throws Exception {
        doThrow(new ForbiddenUserException(100))
                .when(messageService)
                .getMessages(Mockito.any(Integer.class), Mockito.any(AppUserDetails.class));

        RequestBuilder getMessagesRequest = MockMvcRequestBuilders
                .get("/users/{userId}/messages",
                        100)
                .with(user(new AppUserDetails(testAdmin)));

        mockMvc.perform(getMessagesRequest).andExpect(status().isForbidden());
    }

    /**
     * Tests that a 406 response is thrown when a user tries to retrieve
     * a user's messages for a user that does not exist.
     */
    @Test
    void getMessages_nonExistentUser_throws406() throws Exception {
        doThrow(NotAcceptableException.class)
                .when(messageService)
                .getMessages(Mockito.any(Integer.class), Mockito.any(AppUserDetails.class));

        RequestBuilder getMessagesRequest = MockMvcRequestBuilders
                .get("/users/{userId}/messages",
                        100)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(getMessagesRequest).andExpect(status().isNotAcceptable());
    }

    /**
     * Tests that a 200 response on a successful deletion of a user's messages
     */
    @Test
    void deleteMessage_success200() throws Exception {
        RequestBuilder deleteMessageRequest = MockMvcRequestBuilders
                .delete("/users/{userId}/messages/{messageId}",
                        testUser.getId(), testMessages.get(0).getId())
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(deleteMessageRequest).andExpect(status().isOk());
    }

    /**
     * Tests that a 401 response is thrown when a user tries to delete
     * a user's messages and they're not logged in.
     */
    @Test
    void deleteMessages_notLoggedIn_throws401() throws Exception {
        RequestBuilder deleteMessageRequest = MockMvcRequestBuilders
                .delete("/users/{userId}/messages/{messageId}",
                        testUser.getId(), testMessages.get(0).getId());

        mockMvc.perform(deleteMessageRequest).andExpect(status().isUnauthorized());
    }

    /**
     * Tests that a 403 response is thrown when a user tries to delete
     * a user's messages and it's not their own messages.
     */
    @Test
    void deleteMessages_wrongUser_throws403() throws Exception {
        doThrow(new ForbiddenUserException(100))
                .when(messageService)
                .deleteMessage(
                        Mockito.any(Integer.class),
                        Mockito.any(Integer.class),
                        Mockito.any(AppUserDetails.class)
                );

        RequestBuilder deleteMessageRequest = MockMvcRequestBuilders
                .delete("/users/{userId}/messages/{messageId}",
                        otherTestUser.getId(), testMessages.get(0).getId())
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(deleteMessageRequest).andExpect(status().isForbidden());
    }

    /**
     * Tests that a 403 response is thrown when a user tries to delete
     * a user's messages and it's not their own messages.
     */
    @Test
    void deleteMessages_GAAWrongUser_throws403() throws Exception {
        doThrow(new ForbiddenUserException(100))
                .when(messageService)
                .deleteMessage(
                        Mockito.any(Integer.class),
                        Mockito.any(Integer.class),
                        Mockito.any(AppUserDetails.class)
                );

        RequestBuilder deleteMessageRequest = MockMvcRequestBuilders
                .delete("/users/{userId}/messages/{messageId}",
                        testUser.getId(), testMessages.get(0).getId())
                .with(user(new AppUserDetails(testAdmin)));

        mockMvc.perform(deleteMessageRequest).andExpect(status().isForbidden());
    }

    /**
     * Tests that a 406 response is thrown when a user tries to delete
     * a user's messages for a user that does not exist.
     */
    @Test
    void deleteMessages_nonExistentUser_throws406() throws Exception {
        doThrow(NotAcceptableException.class)
                .when(messageService)
                .deleteMessage(
                        Mockito.any(Integer.class),
                        Mockito.any(Integer.class),
                        Mockito.any(AppUserDetails.class)
                );

        RequestBuilder deleteMessageRequest = MockMvcRequestBuilders
                .delete("/users/{userId}/messages/{messageId}",
                        100, testMessages.get(0).getId())
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(deleteMessageRequest).andExpect(status().isNotAcceptable());
    }

    /**
     * Tests that a 406 response is thrown when a user tries to delete
     * a user's messages for a user that does not exist.
     */
    @Test
    void deleteMessages_nonExistentMessage_throws406() throws Exception {
        doThrow(NotAcceptableException.class)
                .when(messageService)
                .deleteMessage(
                        Mockito.any(Integer.class),
                        Mockito.any(Integer.class),
                        Mockito.any(AppUserDetails.class)
                );

        RequestBuilder deleteMessageRequest = MockMvcRequestBuilders
                .delete("/users/{userId}/messages/{messageId}",
                        testUser.getId(), 100)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(deleteMessageRequest).andExpect(status().isNotAcceptable());
    }

    /**
     * Test that a valid request to read a message results in status code 200
     */
    @Test
    void readMessage_validRequest_status200() throws Exception {
        JSONObject requestBody = new JSONObject();
        requestBody.put("read", true);

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/users/{userId}/messages/{messageId}/read", testUser.getId(), testMessages.get(0).getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody.toString())
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(request).andExpect(status().isOk());
    }

    /**
     * Test that a request with non-existent user or message results in status 406
     */
    @Test
    void readMessage_nonExistentUserOrMessage_status406() throws Exception {
        Mockito.doThrow(NotAcceptableException.class)
                .when(messageService)
                .readMessage(
                        any(Integer.class),
                        any(Integer.class),
                        any(boolean.class),
                        any(AppUserDetails.class)
                );

        JSONObject requestBody = new JSONObject();
        requestBody.put("read", true);

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/users/{userId}/messages/{messageId}/read", 1000, 1000)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody.toString())
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(request).andExpect(status().isNotAcceptable());
    }

    /**
     * Test that a request from user who is not the receiver results in status 403
     */
    @Test
    void readMessage_userNotReceiver_status403() throws Exception {
        Mockito.doThrow(ForbiddenUserException.class)
                .when(messageService)
                .readMessage(
                        any(Integer.class),
                        any(Integer.class),
                        any(boolean.class),
                        any(AppUserDetails.class)
                );

        JSONObject requestBody = new JSONObject();
        requestBody.put("read", true);

        RequestBuilder request = MockMvcRequestBuilders
                .patch(
                        "/users/{userId}/messages/{messageId}/read",
                        testUser.getId(),
                        testMessages.get(0).getId()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody.toString())
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(otherTestUser)));

        mockMvc.perform(request).andExpect(status().isForbidden());
    }

    @Test
    void readMessage_bodyNull_status400() throws Exception {
        JSONObject requestBody = new JSONObject();
        requestBody.put("read", null);

        RequestBuilder request = MockMvcRequestBuilders
                .patch(
                        "/users/{userId}/messages/{messageId}/read",
                        testUser.getId(),
                        testMessages.get(0).getId()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody.toString())
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(otherTestUser)));

        mockMvc.perform(request).andExpect(status().isBadRequest());
    }

    @Test
    void readMessage_bodyNotBoolean_status400() throws Exception {
        JSONObject requestBody = new JSONObject();
        requestBody.put("read", "not a boolean");

        RequestBuilder request = MockMvcRequestBuilders
                .patch(
                        "/users/{userId}/messages/{messageId}/read",
                        testUser.getId(),
                        testMessages.get(0).getId()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody.toString())
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(otherTestUser)));

        mockMvc.perform(request).andExpect(status().isBadRequest());
    }
}
