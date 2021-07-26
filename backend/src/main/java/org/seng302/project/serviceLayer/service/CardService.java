package org.seng302.project.serviceLayer.service;

import net.minidev.json.JSONObject;
import org.seng302.project.repositoryLayer.model.*;
import org.seng302.project.repositoryLayer.repository.*;
import org.seng302.project.repositoryLayer.specification.CardSpecifications;
import org.seng302.project.serviceLayer.dto.card.CreateCardDTO;
import org.seng302.project.serviceLayer.dto.card.CreateCardResponseDTO;
import org.seng302.project.serviceLayer.dto.card.EditCardDTO;
import org.seng302.project.serviceLayer.dto.card.GetCardResponseDTO;
import org.seng302.project.serviceLayer.exceptions.BadRequestException;
import org.seng302.project.serviceLayer.exceptions.NoUserExistsException;
import org.seng302.project.serviceLayer.exceptions.RequiredFieldsMissingException;
import org.seng302.project.serviceLayer.exceptions.card.ForbiddenCardActionException;
import org.seng302.project.serviceLayer.exceptions.card.InvalidMarketplaceSectionException;
import org.seng302.project.serviceLayer.exceptions.card.NoCardExistsException;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class with methods for handling community marketplace cards.
 */
@Service
public class CardService {

    private static final Logger logger = LoggerFactory.getLogger(CardService.class.getName());
    private final CardRepository cardRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final KeywordRepository keywordRepository;
    private final UserNotificationRepository userNotificationRepository;

    @Autowired
    public CardService(CardRepository cardRepository,
                       MessageRepository messageRepository,
                       UserRepository userRepository,
                       KeywordRepository keywordRepository,
                       UserNotificationRepository userNotificationRepository) {
        this.cardRepository = cardRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.keywordRepository = keywordRepository;
        this.userNotificationRepository = userNotificationRepository;
    }

    /**
     * Service method for creating a new card
     * Check for forbidden action is done in the controller layer
     *
     * @param dto DTO containing card creation parameters
     * @return a response DTO containing the new card's ID
     */
    public CreateCardResponseDTO createCard(CreateCardDTO dto) {
        try {
            logger.info("Request to create card");

            Optional<User> creator = userRepository.findById(dto.getCreatorId());

            //check that listed card creator exists.
            if (creator.isEmpty()) {
                var badRequestException = new BadRequestException("No user exists with the id " + dto.getCreatorId());
                logger.warn(badRequestException.getMessage());
                throw badRequestException;
            }


            Set<Keyword> keywords = Collections.emptySet();
            if (dto.getKeywordIds() != null) {
                keywords = new HashSet<>(keywordRepository.findAllById(dto.getKeywordIds()));
            }

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
    public JSONObject getAllCardsForSection(String section, Integer page, String sortBy) {
        // TODO: Implement sorting on backend
        try {
            logger.info("Request to get all cards by section: {}, page {}", section, page);

            // Check if the section is invalid
            if (!("Exchange".equals(section) || "ForSale".equals(section) || "Wanted".equals(section))) {
                throw new InvalidMarketplaceSectionException();
            }

            // Return all cards in the section
            Specification<Card> spec = CardSpecifications.inSection(section);
            Pageable pageable = PageRequest.of(page, 10);
            Page<Card> cardsPage = cardRepository.findAll(spec, pageable);
            List<Card> cards = cardsPage.getContent();
            Long totalCards = cardsPage.getTotalElements();
            List<GetCardResponseDTO> responseCards = new ArrayList<>();
            for (Card card : cards) {
                responseCards.add(new GetCardResponseDTO(card));
            }

            JSONObject response = new JSONObject();
            response.put("cards", responseCards);
            response.put("totalCards", totalCards);
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

            //Delete messages about the card
            var cardMessages = messageRepository.findAllByCard(retrievedCard);
            for (Message message : cardMessages) {
                messageRepository.delete(message);
            }
            logger.info("Deleted {} messages about the card wanting to be deleted", cardMessages.size());

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
     * Service method for editing a card
     *
     * @param cardId  id of the card to edit
     * @param dto     dto of the edited card
     * @param appUser user who is making the request
     */
    public void editCard(Integer cardId, EditCardDTO dto, AppUserDetails appUser) {
        logger.info("Request to edit card with id {}", cardId);
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

            //Can now edit the card

            //Section has been checked to be non empty and a valid section by DTO
            retrievedCard.setSection(dto.getSection());

            //Title has been checked to be non empty by DTO
            retrievedCard.setTitle(dto.getTitle());

            retrievedCard.setDescription(dto.getDescription());

            Set<Keyword> keywords = Collections.emptySet();
            if (dto.getKeywordIds() != null) {
                keywords = new HashSet<>(keywordRepository.findAllById(dto.getKeywordIds()));
            }
            retrievedCard.setKeywords(keywords);

            cardRepository.save(retrievedCard);
        } catch (NoCardExistsException | ForbiddenCardActionException expectedException) {
            logger.warn(expectedException.getMessage());
            throw expectedException;
        } catch (Exception unexpectedException) {
            logger.error(String.format("Unexpected error while editing card: %s", unexpectedException.getMessage()));
            throw unexpectedException;
        }
    }

    /**
     * Searches for cards based on a section and keyword IDs
     *
     * @param section    the section to search cards in
     * @param keywordIds the keyword IDs to search for cards with
     * @param union      whether cards should match with any or all keywords
     * @return a list of cards that matches the query
     */
    public List<GetCardResponseDTO> searchCards(String section, List<Integer> keywordIds, Boolean union) {
        // TODO: change method to use pagination
        if (section == null || !List.of("ForSale", "Wanted", "Exchange").contains(section)) {
            var exception = new BadRequestException("Bad Request: invalid section");
            logger.warn(exception.getMessage());
            throw exception;
        }
        if (keywordIds == null || keywordIds.isEmpty()) {
            var exception = new BadRequestException("Bad Request: at least one keyword ID is required");
            logger.warn(exception.getMessage());
            throw exception;
        }
        if (union == null) {
            var exception = new BadRequestException("Bad Request: union is a required parameter");
            logger.warn(exception.getMessage());
            throw exception;
        }

        Specification<Card> spec = Specification.where(null);
        for (Integer keywordId : keywordIds) {
            Optional<Keyword> keyword = keywordRepository.findById(keywordId);
            if (keyword.isEmpty()) {
                var exception = new BadRequestException("Bad Request: invalid keyword ID");
                logger.warn(exception.getMessage());
                throw exception;
            }
            if (union) {
                spec = spec.or(CardSpecifications.hasKeyword(keyword.get()));
            } else {
                spec = spec.and(CardSpecifications.hasKeyword(keyword.get()));
            }
        }

        spec = CardSpecifications.inSection(section).and(spec);

        List<Card> cards = cardRepository.findAll(spec);
        List<GetCardResponseDTO> response = new ArrayList<>();
        for (Card card : cards) {
            response.add(new GetCardResponseDTO(card));
        }
        return response;
    }


    /**
     * Method that gets called every 60 seconds that removes all cards that have a display period end of more than a day ago
     */
    @Scheduled(fixedRate = 60000)
    public void removeCardsAfter24Hrs() {
        logger.info("Removing cards that have been expired for 24 hours or more");
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        List<Card> cardsDeleted = cardRepository.deleteByDisplayPeriodEndBefore(oneDayAgo);

        //Create a CardExpiryNotification for each expired card
        for (Card card : cardsDeleted) {
            var newNotification = new CardExpiryNotification(
                    card.getCreator(),
                    "This card has expired and was deleted.",
                    card.getTitle());
            userNotificationRepository.save(newNotification);
        }
    }
}
