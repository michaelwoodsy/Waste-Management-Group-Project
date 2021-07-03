package org.seng302.project.webLayer.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.seng302.project.serviceLayer.service.KeywordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test class for performing unit tests for the KeywordController class and its methods.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class KeywordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KeywordService keywordService;

    // TODO: Test to be removed once another test has been implemented.
    @Test
    void basicClassTest() {
        KeywordController keywordController = new KeywordController(keywordService);
        Assertions.assertEquals(keywordController.getClass(), KeywordController.class);
    }

}
