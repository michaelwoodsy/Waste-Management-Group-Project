package org.seng302.project.serviceLayer.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repositoryLayer.model.Card;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.CardRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.dto.card.CreateCardDTO;
import org.seng302.project.serviceLayer.dto.card.GetCardResponseDTO;
import org.seng302.project.serviceLayer.exceptions.card.NoCardExistsException;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Tests for the CardServiceClass
 */
@SpringBootTest
class CardServiceTest extends AbstractInitializer {

    private User testUser;
    private Card testUsersCard1;
    private Card testUsersCard2;

    @Autowired
    private CardService cardService;

    @MockBean
    private CardRepository cardRepository;

    @MockBean
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        this.initialise();
        testUser = this.getTestUser();
        given(userRepository.findByEmail(testUser.getEmail())).willReturn(List.of(testUser));
        given(userRepository.findById(testUser.getId())).willReturn(Optional.of(testUser));

        // Create mock cards
        // Card 1
        testUsersCard1 = new Card(testUser, "ForSale", "Test Card", "Test card description", Collections.emptyList());
        testUsersCard1.setId(1);
        given(cardRepository.findById(testUsersCard1.getId())).willReturn(Optional.of(testUsersCard1));

        // Card 2
        testUsersCard2 = new Card(testUser, "Wanted", "Test Card 2", "Test card 2 description", Collections.emptyList());
        testUsersCard2.setId(2);
        given(cardRepository.findById(testUsersCard2.getId())).willReturn(Optional.of(testUsersCard2));

    }


    /**
     * Only cards from the requested userId are returned.
     */
    @Test
    void getAllCardsByUser_onlyCardsCreatedByCreatorWithId() {
        List<Card> expectedCards = List.of(testUsersCard1, testUsersCard2);
        given(cardRepository.findAllByCreator(testUser)).willReturn(expectedCards);

        List<GetCardResponseDTO> cards = cardService.getAllCardsByUser(testUser.getId());

        verify(cardRepository, times(1)).findAllByCreator(testUser);

        assertEquals(testUsersCard1.getId(), cards.get(0).getId());
        assertEquals(testUsersCard2.getId(), cards.get(1).getId());
    }


    /**
     * Checks expired cards are also returned.
     */
    @Test
    void getAllCardsByUser_allCardsReturned() {
        // Create, and mock, an expired card
        Card expiredCard = new Card(testUser, "Wanted", "Test Card 2", "Test card 2 description",
                Collections.emptyList());
        expiredCard.setId(4);
        LocalDateTime timeInPast = LocalDateTime.now().minus(2, ChronoUnit.HOURS);
        expiredCard.setDisplayPeriodEnd(timeInPast);
        given(cardRepository.findById(expiredCard.getId())).willReturn(Optional.of(expiredCard));

        // Mock the card repository call
        List<Card> expectedCards = List.of(testUsersCard1, expiredCard);
        given(cardRepository.findAllByCreator(testUser)).willReturn(expectedCards);

        // Run the method
        List<GetCardResponseDTO> cards = cardService.getAllCardsByUser(testUser.getId());

        // Check the repository was called
        verify(cardRepository, times(1)).findAllByCreator(testUser);

        // Check the expected cards were returned
        assertEquals(testUsersCard1.getId(), cards.get(0).getId());
        assertEquals(expiredCard.getId(), cards.get(1).getId());
    }


    /**
     * Test getting a single card
     */
    @Test
    void getCard_testGetSingleCard() {

        GetCardResponseDTO returnedCard = cardService.getCard(testUsersCard1.getId());

        // Check the returned card is the correct card
        assertEquals(testUsersCard1.getSection(), returnedCard.getSection());
        assertEquals(testUsersCard1.getTitle(), returnedCard.getTitle());
        assertEquals(testUsersCard1.getDescription(), returnedCard.getDescription());
        assertNotNull(returnedCard.getCreated());
        assertNotNull(returnedCard.getDisplayPeriodEnd());
    }


    /**
     * Test getting card that does not exist
     */
    @Test
    void testGetCardDoesNotExist() {
        // Mock card with id 1 being empty
        given(cardRepository.findById(1)).willReturn(Optional.empty());

        Assertions.assertThrows(NoCardExistsException.class,
                () -> cardService.getCard(1));
    }


    /**
     * Test extend display period of card that does not exist
     */
    @Test
    void testExtendCardDoesNotExist() {
        // Mock card with id 1 being empty
        given(cardRepository.findById(1)).willReturn(Optional.empty());

        Assertions.assertThrows(NoCardExistsException.class,
                () -> cardService.getCard(1));
    }


    /**
     * Test successfully extend display period of card
     */
    @Test
    void testExtendCardSuccess() {
        LocalDateTime expectedNewEndDate = testUsersCard1.getDisplayPeriodEnd().plusWeeks(2);

        cardService.extendCardDisplayPeriod(testUsersCard1.getId(), new AppUserDetails(testUser));

        Optional<Card> extendedCardOptional = cardRepository.findById(testUsersCard1.getId());
        Assertions.assertTrue(extendedCardOptional.isPresent());
        Card extendedCard = extendedCardOptional.get();

        assertEquals(expectedNewEndDate.getMonthValue(), extendedCard.getDisplayPeriodEnd().getMonthValue());
        assertEquals(expectedNewEndDate.getDayOfMonth(), extendedCard.getDisplayPeriodEnd().getDayOfMonth());
    }

    /**
     * Test deleting a card that does not exist.
     * Expect a 406 response with a NoCardExistsException
     */
    @Test
    void testDeleteCardDoesNotExist() {
        // Mock card with id 1 being empty
        given(cardRepository.findById(1)).willReturn(Optional.empty());

        AppUserDetails appUser = new AppUserDetails(testUser);
        Assertions.assertThrows(NoCardExistsException.class,
                () -> cardService.deleteCard(1, appUser));

    }


    /**
     * Creating a card with the bare minimum required fields.
     */
    @Test
    void createCard() {
        CreateCardDTO requestDTO = new CreateCardDTO(
                testUser.getId(),
                testUsersCard1.getSection(),
                testUsersCard1.getTitle(),
                testUsersCard1.getDescription(),
                Collections.emptyList()
                );

        // Mock the save method on the cardRepository
        given(cardRepository.save(any(Card.class))).willReturn(testUsersCard1);

        cardService.createCard(requestDTO, new AppUserDetails(testUser));

        ArgumentCaptor<Card> cardArgumentCaptor = ArgumentCaptor.forClass(Card.class);
        verify(cardRepository).save(cardArgumentCaptor.capture());
        Card createdCard = cardArgumentCaptor.getValue();

        Assertions.assertEquals(testUsersCard1.getTitle(), createdCard.getTitle());
        Assertions.assertEquals(testUsersCard1.getDescription(), createdCard.getDescription());
        Assertions.assertEquals(testUsersCard1.getSection(), createdCard.getSection());
        Assertions.assertEquals(testUsersCard1.getCreator().getEmail(), createdCard.getCreator().getEmail());
    }
}