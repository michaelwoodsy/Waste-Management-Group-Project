package org.seng302.project.serviceLayer.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.seng302.project.repositoryLayer.model.Keyword;
import org.seng302.project.repositoryLayer.repository.KeywordRepository;
import org.seng302.project.serviceLayer.dto.keyword.AddKeywordDTO;
import org.seng302.project.serviceLayer.dto.keyword.AddKeywordResponseDTO;
import org.seng302.project.serviceLayer.exceptions.keyword.KeywordExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Test class for performing unit tests for the KeywordService class and its methods.
 */
@SpringBootTest
class KeywordServiceTest {

    @Autowired
    private KeywordService keywordService;
    @MockBean
    private KeywordRepository keywordRepository;
    private List<Keyword> keywords;

    @BeforeEach
    void setup() {
        keywords = new ArrayList<>();
    }

    /**
     * Tests that adding a new keyword successfully adds it to the repository and returns a response DTO.
     */
    @Test
    void addKeyword_newKeyword_success() {
        Mockito.when(keywordRepository.findByName(Mockito.any(String.class)))
                .thenReturn(Collections.emptyList());

        Mockito.when(keywordRepository.save(Mockito.any(Keyword.class)))
                .thenAnswer(invocation -> {
                    Keyword keyword = invocation.getArgument(0);
                    keyword.setId(keywords.size() + 1);
                    keywords.add(keyword);
                    return keyword;
                });

        AddKeywordDTO dto = new AddKeywordDTO("TestKeyword");
        AddKeywordResponseDTO responseDTO = keywordService.addKeyword(dto.getName());
        Assertions.assertEquals(1, responseDTO.getKeywordId());
        Assertions.assertEquals(1, keywords.size());
    }

    /**
     * Tests that attempting to add a keyword that already exists results in an error.
     */
    @Test
    void addKeyword_alreadyExists_throwsException() {
        Mockito.when(keywordRepository.findByName(Mockito.any(String.class)))
                .thenAnswer(invocation -> {
                    String name = invocation.getArgument(0);
                    Keyword keyword = new Keyword(name);
                    keyword.setId(1);
                    return List.of(keyword);
                });

        AddKeywordDTO dto = new AddKeywordDTO("TestKeyword");
        Assertions.assertThrows(KeywordExistsException.class,
                () -> keywordService.addKeyword(dto.getName()));
    }

}
