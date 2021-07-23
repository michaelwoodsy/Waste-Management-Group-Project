package org.seng302.project.serviceLayer.service;

import org.seng302.project.repositoryLayer.model.Card;
import org.seng302.project.repositoryLayer.model.Keyword;
import org.seng302.project.repositoryLayer.model.NewKeywordNotification;
import org.seng302.project.repositoryLayer.repository.AdminNotificationRepository;
import org.seng302.project.repositoryLayer.repository.CardRepository;
import org.seng302.project.repositoryLayer.repository.KeywordRepository;
import org.seng302.project.repositoryLayer.specification.KeywordSpecifications;
import org.seng302.project.serviceLayer.dto.keyword.AddKeywordResponseDTO;
import org.seng302.project.serviceLayer.exceptions.NotAcceptableException;
import org.seng302.project.serviceLayer.exceptions.keyword.KeywordExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

/**
 * Service class with methods for handling Keywords
 */
@Service
public class KeywordService {

    private static final Logger logger = LoggerFactory.getLogger(KeywordService.class.getName());
    private final KeywordRepository keywordRepository;
    private final CardRepository cardRepository;
    private final AdminNotificationRepository adminNotificationRepository;

    @Autowired
    public KeywordService(KeywordRepository keywordRepository, CardRepository cardRepository,
                          AdminNotificationRepository adminNotificationRepository) {
        this.keywordRepository = keywordRepository;
        this.cardRepository = cardRepository;
        this.adminNotificationRepository = adminNotificationRepository;
    }

    /**
     * Method which adds a new keyword to the keyword repository.
     *
     * @param name String name of the keyword to add.
     * @return an object containing the ID of the keyword that has been added.
     */
    public AddKeywordResponseDTO addKeyword(String name) {
        try {
            //Convert to lower case
            name = name.toLowerCase();
            List<Keyword> keywords = keywordRepository.findByName(name);
            if (!keywords.isEmpty()) {
                var exception = new KeywordExistsException(name);
                logger.error(exception.getMessage());
                throw exception;
            }

            var keyword = new Keyword(name);
            keyword = keywordRepository.save(keyword);

            var adminNotification = new NewKeywordNotification(keyword);
            //You can use the adminNotificationRepository for keyword notifications
            adminNotificationRepository.save(adminNotification);

            return new AddKeywordResponseDTO(keyword.getId());
        } catch (Exception exception) {
            logger.error(String.format("Unexpected error while searching keywords: %s", exception.getMessage()));
            throw exception;
        }
    }

    /**
     * Searches for keywords that include the string partialKeyword
     *
     * @param partialKeyword string to search by
     * @return a list of keywords that match the partialKeyword
     */
    public List<Keyword> searchKeywords(String partialKeyword) {
        try {
            partialKeyword = partialKeyword.toLowerCase();

            Set<Keyword> matchingKeywords = new LinkedHashSet<>(keywordRepository.findAll(
                    KeywordSpecifications.containsPartialKeyword(partialKeyword)));

            return new ArrayList<>(matchingKeywords);
        } catch (Exception exception) {
            logger.error(String.format("Unexpected error while searching keywords: %s", exception.getMessage()));
            throw exception;
        }
    }

    /**
     * Deletes a keyword with the corresponding ID.
     *
     * @param keywordId ID of the keyword to delete.
     */
    @Transactional
    public void deleteKeyword(Integer keywordId) {
        try {
            // Get keyword from the repository
            Optional<Keyword> foundKeyword = keywordRepository.findById(keywordId);

            // Check if the keyword exists
            if (foundKeyword.isEmpty()) {
                var exception = new NotAcceptableException(String.format("No keyword exists with ID %s", keywordId));
                logger.warn(exception.getMessage());
                throw exception;
            }

            var keyword = foundKeyword.get();

            // Delete any associations in cards
            for (Card card : cardRepository.findAllByKeywordsContaining(keyword)) {
                card.removeKeyword(keyword);
                cardRepository.save(card);
            }

            // Delete the keyword
            keywordRepository.delete(keyword);

        } catch (Exception exception) {
            logger.error(String.format("Unexpected error while deleting keyword: %s", exception.getMessage()));
            throw exception;
        }
    }

}
