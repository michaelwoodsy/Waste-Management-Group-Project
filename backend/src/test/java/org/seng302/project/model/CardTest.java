package org.seng302.project.model;


import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

/**
 * Unit tests for Card class
 */
@SpringBootTest
public class CardTest {

    /**
     * Creates the test card from the API.
     * Then checks all of the attributes of the new card.
     */
    @Test
    public void createTestCard() {
        Card testCard = new Card(100, "ForSale", "1982 Lada Samara",
                "Beige, suitable for a hen house. Fair condition. Some rust. As is, where is. Will swap for budgerigar.");

        Assertions.assertNull(testCard.getId());
        Assertions.assertEquals(100, testCard.getCreatorId());
        Assertions.assertEquals("ForSale", testCard.getSection());
        Assertions.assertEquals("1982 Lada Samara", testCard.getTitle());
        Assertions.assertEquals("Beige, suitable for a hen house. Fair condition. Some rust. As is, where is. Will swap for budgerigar.",
                testCard.getDescription());
        Assertions.assertTrue(testCard.getCreated().isBefore(LocalDateTime.now()) || testCard.getCreated().isEqual(LocalDateTime.now()));
        Assertions.assertTrue(testCard.getCreated().isAfter(LocalDateTime.now().minusSeconds(5)));
    }
}
