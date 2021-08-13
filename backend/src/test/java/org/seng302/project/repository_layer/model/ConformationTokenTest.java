package org.seng302.project.repository_layer.model;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.project.AbstractInitializer;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Collections;


/**
 * Unit tests for ConformationToken class
 */
@SpringBootTest
class ConformationTokenTest extends AbstractInitializer {

    /**
     * Creates the test ConformationToken
     * Then checks all of the attributes of the new ConformationToken.
     */
    @Test
    void createTestCard() {
        this.initialiseTestUsers();
        User user = this.getTestUser();
        user.setId(null);
        String tokenString = "CoolToken";
        ConformationToken token = new ConformationToken(tokenString, user);

        Assertions.assertNull(token.getId());
        Assertions.assertNull(token.getUser().getId());
        Assertions.assertEquals(user.getEmail(), token.getUser().getEmail());
        Assertions.assertEquals(tokenString, token.getToken());
        Assertions.assertTrue(token.getCreated().isBefore(LocalDateTime.now()) || token.getCreated().isEqual(LocalDateTime.now()));
    }
}
