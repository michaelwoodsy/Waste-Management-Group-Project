package org.seng302.project.serviceLayer.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test class for performing unit tests for the KeywordService class and its methods.
 */
@SpringBootTest
class KeywordServiceTest {

    @Autowired
    private KeywordService keywordService;

    // TODO: Test to be removed once another test has been implemented.
    @Test
    void basicClassTest() {
        Assertions.assertEquals(keywordService.getClass(), KeywordService.class);
    }

}
