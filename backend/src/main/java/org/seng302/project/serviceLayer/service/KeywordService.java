package org.seng302.project.serviceLayer.service;

import org.seng302.project.repositoryLayer.repository.KeywordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}
