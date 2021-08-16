package org.seng302.project.web_layer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.*;
import org.seng302.project.service_layer.dto.user.ChangePasswordDTO;
import org.seng302.project.service_layer.exceptions.NotAcceptableException;
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
     * Test that when validating a conformation token that does not exist thrown a NotAcceptableException
     */
    @Test
    void validateToken_doesNotExist_returns406() throws Exception {
        Mockito.doThrow(NotAcceptableException.class)
                .when(lostPasswordService).validateToken(any(String.class));

        RequestBuilder request = MockMvcRequestBuilders
                .get("/lostpassword/validate")
                .param("token", "SomeToken");

        mockMvc.perform(request).andExpect(status().isNotAcceptable());
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
     * Test that when editing a users password with a token that does not exist thrown a NotAcceptableException
     */
    @Test
    void editPassword_tokenDoesNotExist_returns406() throws Exception {
        Mockito.doThrow(NotAcceptableException.class)
                .when(lostPasswordService).changePassword(any(ChangePasswordDTO.class));

        ChangePasswordDTO requestDTO = new ChangePasswordDTO("NotAToken", "NewPassword123");

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/lostpassword/edit")
                .content(objectMapper.writeValueAsString(requestDTO))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request).andExpect(status().isNotAcceptable());
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
}
