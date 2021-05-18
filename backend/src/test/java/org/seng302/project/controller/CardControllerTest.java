package org.seng302.project.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.project.controller.authentication.AppUserDetails;
import org.seng302.project.controller.authentication.AppUserDetailsService;
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

    @Test
    void unauthenticatedUserReturns401() {
        //given(userRepository.findByEmail("test@gmail.com")).willReturn(List.of(testUser));
        assertEquals(userRepository.findByEmail("test@gmail.com").get(0), testUser);
    }

    @Test
    void missingSectionParamReturns400() {

    }

    @Test
    void authenticatedUserReturns200() throws Exception {
    }

    @Test
    void mockedUserProofConcept() throws Exception {
        mockMvc.perform(get("/cards")
                .with(user(testUser.getEmail())))
                .andExpect(status().isBadRequest());
    }


}
