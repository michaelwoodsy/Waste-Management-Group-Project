package org.seng302.project.serviceLayer.service;

import org.seng302.project.repositoryLayer.model.Card;
import org.seng302.project.repositoryLayer.model.Keyword;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.CardRepository;
import org.seng302.project.repositoryLayer.repository.KeywordRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.dto.card.CreateCardDTO;
import org.seng302.project.serviceLayer.dto.card.CreateCardResponseDTO;
import org.seng302.project.serviceLayer.dto.card.GetCardResponseDTO;
import org.seng302.project.serviceLayer.exceptions.NoUserExistsException;
import org.seng302.project.serviceLayer.exceptions.RequiredFieldsMissingException;
import org.seng302.project.serviceLayer.exceptions.card.ForbiddenCardActionException;
import org.seng302.project.serviceLayer.exceptions.card.InvalidMarketplaceSectionException;
import org.seng302.project.serviceLayer.exceptions.card.NoCardExistsException;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service class with methods for handling community marketplace cards.
 */
@Service
public class CardService {

    private static final Logger logger = LoggerFactory.getLogger(CardService.class.getName());
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final KeywordRepository keywordRepository;

    @Autowired
    public CardService(CardRepository cardRepository,
                       UserRepository userRepository,
                       KeywordRepository keywordRepository) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.keywordRepository = keywordRepository;
    }

    /**
     * Service method for creating a new card
     *
     * @param dto DTO containing card creation parameters
     * @return a response DTO containing the new card's ID
     */
    public CreateCardResponseDTO createCard(CreateCardDTO dto, AppUserDetails appUser) {
        try {
            logger.info("Request to create card");

            var loggedInUser = userRepository.findByEmail(appUser.getUsername()).get(0);
            Optional<User> creator = userRepository.findById(dto.getCreatorId());

            // check if loggedInUser has the same ID as the creator id provided, otherwise check loggedInUser is GAA
            if (!loggedInUser.getId().equals(dto.getCreatorId()) && (
                    !loggedInUser.getRole().equals("globalApplicationAdmin")
                            || !loggedInUser.getRole().equals("defaultGlobalApplicationAdmin")
            )) {
                throw new ForbiddenCardActionException();
            }

            //check that listed card creator exists.
            if (creator.isEmpty()) {
                var noUserExistsException = new NoUserExistsException(dto.getCreatorId());
                logger.warn(noUserExistsException.getMessage());
                throw noUserExistsException;
            }

            List<Keyword> keywords = keywordRepository.findAllById(dto.getKeywordIds());

            var newCard = new Card(creator.get(), dto.getSection(), dto.getTitle(), dto.getDescription(), keywords);
            Integer cardId = cardRepository.save(newCard).getId();
            return new CreateCardResponseDTO(cardId);
        } catch (NoUserExistsException | RequiredFieldsMissingException expectedException) {
            throw expectedException;
        } catch (Exception exception) {
            logger.error(String.format("Unexpected error while creating card: %s", exception.getMessage()));
            throw exception;
        }
    }

    /**
     * Service method for getting a card
     *
     * @param cardId ID of the card to get
     * @return response DTO containing card information
     */
    public GetCardResponseDTO getCard(Integer cardId) {
        logger.info("Request to get card with id {}", cardId);
        try {
            var card = cardRepository.findById(cardId).orElseThrow(() -> new NoCardExistsException(cardId));
            return new GetCardResponseDTO(card);
        } catch (NoCardExistsException noCardExistsException) {
            logger.warn(noCardExistsException.getMessage());
            throw noCardExistsException;
        } catch (Exception exception) {
            logger.error(String.format("Unexpected error while getting card: %s", exception.getMessage()));
            throw exception;
        }
    }

    /**
     * Service method for getting all cards from a marketplace section
     *
     * @param section the section to get all cards from
     * @return a list of response DTOs containing card data
     */
    public List<GetCardResponseDTO> getAllCardsForSection(String section) {
        try {
            logger.info("Request to get all cards by section: {}", section);

            // Check if the section is invalid
            if (!("Exchange".equals(section) ||
                    "ForSale".equals(section) ||
                    "Wanted".equals(section))) {
                throw new InvalidMarketplaceSectionException();
            }

            // Return all cards in the section
            List<Card> cards = cardRepository.findAllBySection(section);
            List<GetCardResponseDTO> response = new ArrayList<>();
            cards.forEach(card -> response.add(new GetCardResponseDTO(card)));
            return response;
        } catch (Exception exception) {
            logger.error("Unexpected error while getting all cards");
            throw exception;
        }
    }

    /**
     * Service method for extending the display period of a card
     *
     * @param cardId  ID of the card to get
     * @param appUser the currently logged in user
     */
    public void extendCardDisplayPeriod(Integer cardId, AppUserDetails appUser) {
        logger.info("Request to extend display period of card with id {}", cardId);
        try {
            var loggedInUser = userRepository.findByEmail(appUser.getUsername()).get(0);

            // Get card from repository
            Optional<Card> foundCard = cardRepository.findById(cardId);
            // Check if the card exists
            if (foundCard.isEmpty()) {
                var exception = new NoCardExistsException(cardId);
                logger.warn(exception.getMessage());
                throw exception;
            }
            var cardToExtend = foundCard.get();

            //Checks if the logged in user is allowed to edit this card
            if (!cardToExtend.userCanEdit(loggedInUser)) {
                var exception = new ForbiddenCardActionException();
                logger.warn(exception.getMessage());
                throw exception;
            }

            //Change the cards displayPeriodEnd date to 12 days in the future
            cardToExtend.setDisplayPeriodEnd(cardToExtend.getDisplayPeriodEnd().plusWeeks(2));
            cardRepository.save(cardToExtend);
        } catch (NoCardExistsException | ForbiddenCardActionException foundException) {
            logger.warn(foundException.getMessage());
            throw foundException;
        } catch (Exception exception) {
            logger.error(String.format("Unexpected error while extending display period of card: %s", exception.getMessage()));
            throw exception;
        }
    }

    /**
     * Service method for deleting a card
     *
     * @param cardId  ID of the card to delete
     * @param appUser user who is making the request
     */
    public void deleteCard(Integer cardId, AppUserDetails appUser) {
        logger.info("Request to delete card with id {}", cardId);
        try {
            //406 if card cannot be found (NoCardExistsException)
            Optional<Card> cardOptional = cardRepository.findById(cardId);
            if (cardOptional.isEmpty()) {
                throw new NoCardExistsException(cardId);
            }
            var retrievedCard = cardOptional.get();

            //403 if card is not yours or you aren't DGAA/GAA (ForbiddenCardActionException)
            User requestMaker = userRepository.findByEmail(appUser.getUsername()).get(0);
            if (!retrievedCard.userCanEdit(requestMaker)) {
                throw new ForbiddenCardActionException();
            }

            //200 if card successfully deleted
            cardRepository.deleteById(cardId);
        } catch (NoCardExistsException | ForbiddenCardActionException expectedException) {
            logger.warn(expectedException.getMessage());
            throw expectedException;
        } catch (Exception unexpectedException) {
            logger.error(String.format("Unexpected error while deleting card: %s", unexpectedException.getMessage()));
            throw unexpectedException;
        }
    }

    /**
     * Service method for getting all cards made by a specific user
     *
     * @param userId ID of the user to get cards of
     * @return a list of the user's cards
     */
    public List<GetCardResponseDTO> getAllCardsByUser(Integer userId) {
        logger.info("Request to get cards by user with id {}", userId);

        try {
            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalUser.isEmpty()) {
                throw new NoUserExistsException(userId);
            }
            var user = optionalUser.get();

            List<Card> cards = cardRepository.findAllByCreator(user);
            List<GetCardResponseDTO> response = new ArrayList<>();
            cards.forEach(card -> response.add(new GetCardResponseDTO(card)));
            return response;
        } catch (NoUserExistsException expectedException) {
            logger.warn(expectedException.getMessage());
            throw expectedException;
        } catch (Exception unexpectedException) {
            logger.error(String.format("Unexpected error while retrieving users cards: %s", unexpectedException.getMessage()));
            throw unexpectedException;
        }
    }

    /**
     * Method that gets called every 60 seconds that removes all cards that have a display period end of more than a day ago
     */
    @Scheduled(fixedRate = 60000)
    public void removeCardsAfter24Hrs() {
        logger.info("Removing cards that have been expired for 24 hours or more");
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        cardRepository.deleteByDisplayPeriodEndBefore(oneDayAgo);
    }
}
