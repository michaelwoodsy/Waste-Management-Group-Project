package org.seng302.project.web_layer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.*;
import org.seng302.project.service_layer.dto.user.ChangePasswordDTO;
import org.seng302.project.service_layer.exceptions.BadRequestException;
import org.seng302.project.service_layer.service.LostPasswordService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class LostPasswordControllerTest extends AbstractInitializer {

    private User testUser;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LostPasswordService lostPasswordService;

    @BeforeEach
    public void setup() {
        this.initialise();
        testUser = this.getTestUser();
    }

    /**
     * Test that when validating a conformation token that does not exist thrown a BadRequestException
     */
    @Test
    void validateToken_doesNotExist_returns400() throws Exception {
        Mockito.doThrow(BadRequestException.class)
                .when(lostPasswordService).validateToken(any(String.class));

        RequestBuilder request = MockMvcRequestBuilders
                .get("/lostpassword/validate")
                .param("token", "SomeToken");

        mockMvc.perform(request).andExpect(status().isBadRequest());
    }

    /**
     * Test that when validating a conformation token sends a 200 response
     */
    @Test
    void validateToken_success_returns200() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/lostpassword/validate")
                .param("token", "SomeToken");

        mockMvc.perform(request).andExpect(status().isOk());
    }

    /**
     * Test that when editing a users password with a token that does not exist thrown a BadRequestException
     */
    @Test
    void editPassword_tokenDoesNotExist_returns400() throws Exception {
        Mockito.doThrow(BadRequestException.class)
                .when(lostPasswordService).changePassword(any(ChangePasswordDTO.class));

        ChangePasswordDTO requestDTO = new ChangePasswordDTO("NotAToken", "NewPassword123");

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/lostpassword/edit")
                .content(objectMapper.writeValueAsString(requestDTO))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request).andExpect(status().isBadRequest());
    }

    /**
     * Test that when editing a users password with a password that is not valid throws a BadRequest 400 code
     */
    @Test
    void editPassword_passwordNotValid_returns400() throws Exception {
        Mockito.doNothing().when(lostPasswordService).changePassword(any(ChangePasswordDTO.class));

        //This password is not valid because it doesnt contain any numbers
        ChangePasswordDTO requestDTO = new ChangePasswordDTO("SomeToken", "NotValidPassword");

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/lostpassword/edit")
                .content(objectMapper.writeValueAsString(requestDTO))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult response = mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = response.getResponse().getContentAsString();
        Assertions.assertEquals("MethodArgumentNotValidException: This Password is not valid.", returnedExceptionString);
    }

    /**
     * Test that when editing a users password sends a 200 response
     */
    @Test
    void editPassword_success_returns200() throws Exception {
        ChangePasswordDTO requestDTO = new ChangePasswordDTO("SomeToken", "ValidPassword123");

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/lostpassword/edit")
                .content(objectMapper.writeValueAsString(requestDTO))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request).andExpect(status().isOk());
    }

    /**
     * Tests that successfully sending a password reset email gives 201 response
     */
    @Test
    void sendPasswordResetEmail_success_returns201() throws Exception {
        JSONObject body = new JSONObject();
        body.put("email", "john.smith@gmail.com");

        RequestBuilder request = MockMvcRequestBuilders
                .post("/lostpassword/send")
                .content(body.toString())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request).andExpect(status().isCreated());
    }

    /**
     * Tests that sending a password reset email to an invalid email gives 400 response
     */
    @Test
    void sendPasswordResetEmail_invalidEmail_returns400() throws Exception {
        Mockito.doThrow(BadRequestException.class)
                .when(lostPasswordService).sendPasswordResetEmail(any(String.class));

        JSONObject body = new JSONObject();
        body.put("email", "notanemail");

        RequestBuilder request = MockMvcRequestBuilders
                .post("/lostpassword/send")
                .content(body.toString())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request).andExpect(status().isBadRequest());
    }

}
