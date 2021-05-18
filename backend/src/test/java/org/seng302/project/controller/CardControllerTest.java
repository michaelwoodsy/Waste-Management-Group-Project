package org.seng302.project.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.project.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CardController.class)
public class CardControllerTest {
    private User testUser;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardRepository cardRepository;

    @MockBean
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setEmail("test@gmail.com");
        testUser.setPassword("test@gmail.com");
        given(userRepository.findByEmail("test@gmail.com")).willReturn(List.of(testUser));
    }

    /**
     * If the request is made by an unauthorized user, a 401 should be returned.
     */
    @Test
    void unauthenticatedUserReturns401() throws Exception {
        mockMvc.perform(get("/cards"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Checks a 400 response is returned if the "section" parameter isn't supplied.
     */
    @Test
    void missingSectionParamReturns400() throws Exception {
        mockMvc.perform(get("/cards")
                .with(user(testUser.getEmail())))
                .andExpect(status().isBadRequest());
    }

    /**
     * Checks a 400 response is returned if the "section" parameter isn't valid.
     */
    @Test
    void invalidSectionParamReturns400() throws Exception {
        mockMvc.perform(get("/cards")
                .with(user(testUser.getEmail()))
                .param("section", "beans"))
                .andExpect(status().isBadRequest());
    }

    /**
     * Given an authenticated user makes the request with a valid section parameter
     * a 200 response should be received.
     */
    @Test
    void validSectionParameterReturns200() throws Exception {
        mockMvc.perform(get("/cards")
                .with(user(testUser.getEmail()))
                .param("section", "ForSale"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/cards")
                .with(user(testUser.getEmail()))
                .param("section", "Wanted"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/cards")
                .with(user(testUser.getEmail()))
                .param("section", "Exchange"))
                .andExpect(status().isOk());
    }


}
