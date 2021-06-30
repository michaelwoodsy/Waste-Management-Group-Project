package org.seng302.project.serviceLayer.service;

import org.seng302.project.repositoryLayer.model.Keyword;
import org.seng302.project.repositoryLayer.repository.KeywordRepository;
import org.seng302.project.repositoryLayer.specification.KeywordSpecifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Service class with methods for handling Keywords
 */
@Service
public class KeywordService {

    private static final Logger logger = LoggerFactory.getLogger(KeywordService.class.getName());
    private final KeywordRepository keywordRepository;

    @Autowired
    public KeywordService(KeywordRepository keywordRepository) {
        this.keywordRepository = keywordRepository;
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

}
