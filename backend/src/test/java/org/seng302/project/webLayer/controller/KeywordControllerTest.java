package org.seng302.project.webLayer.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.serviceLayer.service.KeywordService;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

/**
 * Test class for performing unit tests for the KeywordController class and its methods.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class KeywordControllerTest extends AbstractInitializer {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KeywordService keywordService;

    /**
     * Initialises entities from AbstractInitializer
     */
    @BeforeEach
    void setup() {
        initialise();
    }


    /**
     * Tests that we get a 200 response when searching for keywords
     */
    @Test
    void keywordSearch_success200() throws Exception {
        RequestBuilder searchKeywordRequest = MockMvcRequestBuilders
                .get("/keywords/search?searchQuery=fruit")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(getTestUser())));

        mockMvc.perform(searchKeywordRequest)
                .andExpect(MockMvcResultMatchers.status().isOk());


    }

    /**
     * Tests that we get a 401 response when searching for keywords,
     * and not logged in
     */
    @Test
    void keywordSearch_notLoggedIn401() throws Exception {

        RequestBuilder searchKeywordRequest = MockMvcRequestBuilders
                .get("/keywords/search?searchQuery=fruit")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(searchKeywordRequest)
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

    }

}
