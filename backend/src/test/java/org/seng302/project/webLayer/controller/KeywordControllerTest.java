package org.seng302.project.webLayer.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repositoryLayer.model.Keyword;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.exceptions.NotAcceptableException;
import org.seng302.project.serviceLayer.exceptions.card.NoCardExistsException;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
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

    @MockBean
    private UserRepository userRepository;

    /**
     * Initialises entities from AbstractInitializer
     */
    @BeforeEach
    void setup() {
        initialise();
        // Mock user repository to return the logged in user
        when(userRepository.findByEmail(getTestUser().getEmail())).thenReturn(List.of(getTestUser()));
        when(userRepository.findByEmail(getTestSystemAdmin().getEmail())).thenReturn(List.of(getTestSystemAdmin()));
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

    /**
     * Tests we get a 401 response when deleting and not logged in
     */
    @Test
    void deleteKeyword_notLoggedIn401() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/keywords/1"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    /**
     * Tests we get a 403 response when a non admin makes the request
     */
    @Test
    void deleteKeyword_nonAdmin403() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/keywords/1")
                .with(user(new AppUserDetails(getTestUser()))))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    /**
     * Tests we get a 400 response when given a non numeric id
     */
    @Test
    void deleteKeyword_nonNumericId400() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/keywords/hi")
                .with(user(new AppUserDetails(getTestSystemAdmin()))))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    /**
     * Tests we get a 406 response when the keyword doesn't exist
     */
    @Test
    void deleteKeyword_nonExistentId406() throws Exception {
        // Mock keyword service to throw a NotAcceptableException
        doThrow(NotAcceptableException.class)
                .when(keywordService)
                .deleteKeyword(any(Integer.class));

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/keywords/1")
                .with(user(new AppUserDetails(getTestSystemAdmin()))))
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable());
    }

    /**
     * Tests we get a 200 response when an existent keyword is provided
     */
    @Test
    void deleteKeyword_existentId200() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/keywords/1")
                .with(user(new AppUserDetails(getTestSystemAdmin()))))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     * Tests the deleteKeyword method on the keywordService is called when an existent keyword is provided
     */
    @Test
    void deleteKeyword_existentId_callsDeleteMethod() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/keywords/1")
                .with(user(new AppUserDetails(getTestSystemAdmin()))));

        // Capture the integer passed to the keywordService deleteKeyword method
        ArgumentCaptor<Integer> cardArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(keywordService).deleteKeyword(cardArgumentCaptor.capture());
        Integer keywordId = cardArgumentCaptor.getValue();

        // Check the id passed to the service was correct
        Assertions.assertEquals(1, keywordId);
    }
}
