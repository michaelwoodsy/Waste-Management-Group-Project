package org.seng302.project.repositoryLayer.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

/**
 * Unit tests for Keyword class
 */
@SpringBootTest
class KeywordTest {

    /**
     * Creates an example test Keyword
     * Then checks all of the attributes of the new keyword.
     */
    @Test
    void createTestKeyword() {
        Keyword testKeyword = new Keyword("test-keyword");

        Assertions.assertNull(testKeyword.getId());
        Assertions.assertEquals("test-keyword", testKeyword.getName());
        Assertions.assertTrue(testKeyword.getCreated().isBefore(LocalDateTime.now()) ||
                testKeyword.getCreated().isEqual(LocalDateTime.now()));
        Assertions.assertTrue(testKeyword.getCreated().isAfter(LocalDateTime.now().minusSeconds(5)));
    }
}
