package org.seng302.project.serviceLayer.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repositoryLayer.model.Card;
import org.seng302.project.repositoryLayer.model.CardExpiryNotification;
import org.seng302.project.repositoryLayer.model.Keyword;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.model.UserNotification;
import org.seng302.project.repositoryLayer.repository.CardRepository;
import org.seng302.project.repositoryLayer.repository.UserNotificationRepository;
import org.seng302.project.repositoryLayer.repository.KeywordRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.dto.card.CreateCardDTO;
import org.seng302.project.serviceLayer.dto.card.EditCardDTO;
import org.seng302.project.serviceLayer.dto.card.GetCardResponseDTO;
import org.seng302.project.serviceLayer.exceptions.BadRequestException;
import org.seng302.project.serviceLayer.exceptions.card.NoCardExistsException;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.domain.Specification;

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
    private List<Card> testCards;

    @Autowired
    private CardService cardService;

    @MockBean
    private CardRepository cardRepository;

    @MockBean
    private KeywordRepository keywordRepository;


    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserNotificationRepository userNotificationRepository;

    @BeforeEach
    void setup() {
        this.initialise();
        testUser = this.getTestUser();
        testCards = this.getTestCards();
        given(userRepository.findByEmail(testUser.getEmail())).willReturn(List.of(testUser));
        given(userRepository.findById(testUser.getId())).willReturn(Optional.of(testUser));

        // Create mock cards
        // Card 1
        testUsersCard1 = testCards.get(0);
        given(cardRepository.findById(testUsersCard1.getId())).willReturn(Optional.of(testUsersCard1));

        // Card 2
        testUsersCard2 = testCards.get(1);
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
                Collections.emptySet());
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

    @Test
    void searchCard_validInput_returnsList() {
        Mockito.when(keywordRepository.findById(Mockito.any(Integer.class)))
                .thenAnswer(invocation -> {
                    Integer keywordId = invocation.getArgument(0);
                    for (Keyword keyword : getTestKeywords()) {
                        if (keyword.getId().equals(keywordId)) {
                            return Optional.of(keyword);
                        }
                    }
                    return Optional.empty();
                });

        Mockito.when(cardRepository.findAll(Mockito.any(Specification.class)))
                .thenReturn(testCards);

        List<GetCardResponseDTO> result = cardService.searchCards("ForSale", List.of(1, 2), true);
        Assertions.assertEquals(2, result.size());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"invalidSection"})
    void searchCard_invalidSection_throwsException(String section) {
        List<Integer> keywordIds = List.of(1, 2);
        Assertions.assertThrows(BadRequestException.class,
                () -> cardService.searchCards(section, keywordIds, true));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void searchCard_invalidKeywordIds_throwsException(List<Integer> keywordIds) {
        Assertions.assertThrows(BadRequestException.class,
                () -> cardService.searchCards("ForSale", keywordIds, true));
    }

    @Test
    void searchCard_keywordIdNonExistent_throwsException() {
        Mockito.when(keywordRepository.findById(Mockito.any(Integer.class)))
                .thenAnswer(invocation -> {
                    Integer keywordId = invocation.getArgument(0);
                    for (Keyword keyword : getTestKeywords()) {
                        if (keyword.getId().equals(keywordId)) {
                            return Optional.of(keyword);
                        }
                    }
                    return Optional.empty();
                });

        List<Integer> keywordIds = List.of(1, 4);
        Assertions.assertThrows(BadRequestException.class,
                () -> cardService.searchCards("ForSale", keywordIds, true));
    }

    @Test
    void searchCard_unionNull_throwsException() {
        List<Integer> keywordIds = List.of(1, 2);
        Assertions.assertThrows(BadRequestException.class,
                () -> cardService.searchCards("ForSale", keywordIds, null));
    }


    /**
     * Test editing a card that does not exist
     */
    @Test
    void testEditCardDoesNotExist() {
        // Mock card with id 1 being empty
        given(cardRepository.findById(1)).willReturn(Optional.empty());

        EditCardDTO requestDTO = new EditCardDTO(
                "ForSale",
                "Title",
                null,
                Collections.emptyList());

        Assertions.assertThrows(NoCardExistsException.class,
                () -> cardService.editCard(1, requestDTO, new AppUserDetails(testUser)));
    }

    /**
     * Test editing a card that is successful
     */
    @Test
    void testEditCardSuccess() {

        EditCardDTO requestDTO = new EditCardDTO(
                "Exchange",
                "Some Title",
                null,
                Collections.emptyList());

        // Mock the save method on the cardRepository
        given(cardRepository.save(any(Card.class))).willReturn(testUsersCard1);

        cardService.editCard(testUsersCard1.getId(), requestDTO, new AppUserDetails(testUser));

        ArgumentCaptor<Card> cardArgumentCaptor = ArgumentCaptor.forClass(Card.class);
        verify(cardRepository).save(cardArgumentCaptor.capture());
        Card editedCard = cardArgumentCaptor.getValue();

        Assertions.assertEquals(requestDTO.getTitle(), editedCard.getTitle());
        Assertions.assertEquals(requestDTO.getDescription(), editedCard.getDescription());
        Assertions.assertEquals(requestDTO.getSection(), editedCard.getSection());
        Assertions.assertEquals(testUser.getEmail(), editedCard.getCreator().getEmail());
    }

    /**
     * Tests a notification is sent to the user if their card expires
     */
    @Test
    void testCardExpirySendsNotification() {
        //Mocking that the card is returned from the delete function, making it look like it was deleted
        given(cardRepository.deleteByDisplayPeriodEndBefore(any(LocalDateTime.class))).willReturn(List.of(testUsersCard1));

        //Simulating an automatic call for removal of expired cards
        cardService.removeCardsAfter24Hrs();

        //Verifies that a notification was created
        verify(userNotificationRepository, times(1)).save(any(CardExpiryNotification.class));

        //Retrieves the created notification
        ArgumentCaptor<CardExpiryNotification> cardExpiryArgumentCaptor = ArgumentCaptor.forClass(CardExpiryNotification.class);
        verify(userNotificationRepository).save(cardExpiryArgumentCaptor.capture());
        CardExpiryNotification notification = cardExpiryArgumentCaptor.getValue();


        Assertions.assertEquals(testUsersCard1.getTitle(), notification.getCardTitle());
    }
}