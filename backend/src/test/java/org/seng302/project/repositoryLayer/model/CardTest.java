package org.seng302.project.repositoryLayer.model;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


import java.time.LocalDateTime;
import java.util.Collections;


/**
 * Unit tests for Card class
 */
@SpringBootTest
class CardTest {


    /**
     * Creates the test card from the API.
     * Then checks all of the attributes of the new card.
     */
    @Test
    void createTestCard() {
        User testCardCreator = new User("John", "Smith", "Arthur", "Jonny",
                "Likes long walks on the beach", "johnsmith9999@gmail.com",
                "1999-04-27", "+64 3 555 0129", null,
                "1337-H%nt3r2");

        Card testCard = new Card(testCardCreator, "ForSale", "1982 Lada Samara",
                "Beige, suitable for a hen house. Fair condition. Some rust. As is, where is. Will swap for budgerigar.",
                Collections.emptySet());

        Assertions.assertNull(testCard.getId());
        Assertions.assertNull(testCard.getCreator().getId());
        Assertions.assertEquals("John", testCard.getCreator().getFirstName());
        Assertions.assertEquals("Smith", testCard.getCreator().getLastName());
        Assertions.assertEquals("ForSale", testCard.getSection());
        Assertions.assertEquals("1982 Lada Samara", testCard.getTitle());
        Assertions.assertEquals("Beige, suitable for a hen house. Fair condition. Some rust. As is, where is. Will swap for budgerigar.",
                testCard.getDescription());
        Assertions.assertTrue(testCard.getCreated().isBefore(LocalDateTime.now()) || testCard.getCreated().isEqual(LocalDateTime.now()));
        Assertions.assertTrue(testCard.getCreated().isAfter(LocalDateTime.now().minusSeconds(5)));
        Assertions.assertTrue(testCard.getDisplayPeriodEnd().isEqual(testCard.getCreated().plusWeeks(2)));
    }
}
