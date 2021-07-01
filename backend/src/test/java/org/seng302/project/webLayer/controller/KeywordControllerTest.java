package org.seng302.project.webLayer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.serviceLayer.dto.keyword.AddKeywordDTO;
import org.seng302.project.serviceLayer.dto.keyword.AddKeywordResponseDTO;
import org.seng302.project.serviceLayer.service.KeywordService;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for performing unit tests for the KeywordController class and its methods.
 */
@SpringBootTest
@AutoConfigureMockMvc
class KeywordControllerTest extends AbstractInitializer {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private KeywordService keywordService;
    private User testUser;

    @BeforeEach
    void setup() {
        this.initialise();
        testUser = this.getTestUser();
    }

    /**
     * Tests that attempting to add a new keyword when not logged in results in a 401 response.
     */
    @Test
    void addKeyword_notLoggedIn_returnStatus401() throws Exception {
        AddKeywordDTO dto = new AddKeywordDTO("TestKeyword");

        RequestBuilder request = MockMvcRequestBuilders
                .post("/keywords")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests that attempting to add a keyword with no name results in a 400 response.
     */
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"kw", "KeywordKeywordKeywordKeywordKeyword"})
    void addKeyword_invalidName_returnStatus400(String string) throws Exception {
        AddKeywordDTO dto = new AddKeywordDTO(string);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/keywords")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    /**
     * Tests that attempting to add a valid keyword results in success.
     */
    @Test
    void addKeyword_validKeyword_success() throws Exception {
        Mockito.when(keywordService.addKeyword(Mockito.any(String.class)))
                .thenReturn(new AddKeywordResponseDTO(1));

        AddKeywordDTO dto = new AddKeywordDTO("NewKeyword");

        RequestBuilder request = MockMvcRequestBuilders
                .post("/keywords")
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(request)
                .andExpect(status().isCreated());
    }

}
