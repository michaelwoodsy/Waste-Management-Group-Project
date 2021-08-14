package org.seng302.project.web_layer.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.*;
import org.seng302.project.service_layer.exceptions.NotAcceptableException;
import org.seng302.project.service_layer.service.LostPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class LostPasswordControllerTest extends AbstractInitializer {

    private User testUser;

    @Autowired
    private MockMvc mockMvc;

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
}
