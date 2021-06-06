package org.seng302.project.repositoryLayer.model;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.seng302.project.repositoryLayer.repository.CardRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Unit tests for Card class
 */
@SpringBootTest
class CardTest {

    /**
     * Creates the test card from the API.
     * Then checks all of the attributes of the new card.
     */

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    void createTestCard() {
        User testCardCreator = new User("John", "Smith", "Arthur", "Jonny",
                "Likes long walks on the beach", "johnsmith9999@gmail.com",
                "1999-04-27", "+64 3 555 0129", null,
                "1337-H%nt3r2");

        Card testCard = new Card(testCardCreator, "ForSale", "1982 Lada Samara",
                "Beige, suitable for a hen house. Fair condition. Some rust. As is, where is. Will swap for budgerigar.");

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


    @Test
    void testRepository() {

        //TODO: these need to be mocked!
        cardRepository.deleteAll();
        userRepository.deleteAll();

        User testCardCreator = new User("John", "Smith", "Arthur", "Jonny",
                "Likes long walks on the beach", "johnsmith9999@gmail.com",
                "1999-04-27", "+64 3 555 0129", null,
                "1337-H%nt3r2");


        testCardCreator.setPassword(passwordEncoder.encode(testCardCreator.getPassword()));
        userRepository.save(testCardCreator);
        userRepository.save(testCardCreator);

        Card testCard = new Card(testCardCreator, "ForSale", "1982 Lada Samara",
                "Beige, suitable for a hen house. Fair condition. Some rust. As is, where is. Will swap for budgerigar.");

        cardRepository.save(testCard);

        //Test finding single card
        Optional<Card> retrievedCardOptions = cardRepository.findById(testCard.getId());
        Assertions.assertTrue(retrievedCardOptions.isPresent());

        Card retrievedCard = retrievedCardOptions.get();

        Assertions.assertNotNull(retrievedCard.getCreator().getId());
        Assertions.assertEquals("John", retrievedCard.getCreator().getFirstName());
        Assertions.assertEquals("Smith", retrievedCard.getCreator().getLastName());

        Assertions.assertEquals("ForSale", retrievedCard.getSection());
        Assertions.assertEquals("1982 Lada Samara", retrievedCard.getTitle());
        Assertions.assertEquals("Beige, suitable for a hen house. Fair condition. Some rust. As is, where is. Will swap for budgerigar.",
                retrievedCard.getDescription());

        //TODO: the below line sometimes fails
        Assertions.assertTrue(retrievedCard.getCreated().isBefore(LocalDateTime.now()) || retrievedCard.getCreated().isEqual(LocalDateTime.now()));
        Assertions.assertTrue(retrievedCard.getCreated().isAfter(LocalDateTime.now().minusSeconds(5)));
        Assertions.assertTrue(retrievedCard.getDisplayPeriodEnd().isEqual(retrievedCard.getCreated().plusWeeks(2)));


        //TODO: This may fail in future when we have more than one card in the repository.
        //TODO: Sarah had a go at mocking the cardRepository repository for this one but didn't succeed
        //Test finding all cards
        List<Card> retrievedCards = cardRepository.findAllBySection("ForSale");

        Assertions.assertEquals(1, retrievedCards.size());

        retrievedCard = retrievedCards.get(0);

        Assertions.assertNotNull(retrievedCard.getCreator().getId());
        Assertions.assertEquals("John", retrievedCard.getCreator().getFirstName());
        Assertions.assertEquals("Smith", retrievedCard.getCreator().getLastName());

        Assertions.assertEquals("ForSale", retrievedCard.getSection());
        Assertions.assertEquals("1982 Lada Samara", retrievedCard.getTitle());
        Assertions.assertEquals("Beige, suitable for a hen house. Fair condition. Some rust. As is, where is. Will swap for budgerigar.",
                retrievedCard.getDescription());
        Assertions.assertTrue(retrievedCard.getCreated().isBefore(LocalDateTime.now()) || retrievedCard.getCreated().isEqual(LocalDateTime.now()));
        Assertions.assertTrue(retrievedCard.getCreated().isAfter(LocalDateTime.now().minusSeconds(5)));
        Assertions.assertTrue(retrievedCard.getDisplayPeriodEnd().isEqual(retrievedCard.getCreated().plusWeeks(2)));

    }
}
