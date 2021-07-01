package org.seng302.project.serviceLayer.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.seng302.project.repositoryLayer.model.Keyword;
import org.seng302.project.repositoryLayer.repository.KeywordRepository;
import org.seng302.project.serviceLayer.dto.keyword.AddKeywordDTO;
import org.seng302.project.serviceLayer.dto.keyword.AddKeywordResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for performing unit tests for the KeywordService class and its methods.
 */
@SpringBootTest
class KeywordServiceTest {

    private final List<Keyword> keywords = new ArrayList<>();
    @Autowired
    private KeywordService keywordService;
    @MockBean
    private KeywordRepository keywordRepository;

    /**
     * Tests that adding a keyword successfully adds it to the repository and returns a response DTO.
     */
    @Test
    void testAddKeyword() {
        Mockito.when(keywordRepository.save(Mockito.any(Keyword.class)))
                .thenAnswer(invocation -> {
                    Keyword keyword = invocation.getArgument(0);
                    keyword.setId(keywords.size() + 1);
                    keywords.add(keyword);
                    return null;
                });

        AddKeywordDTO dto = new AddKeywordDTO("TestKeyword");
        AddKeywordResponseDTO responseDTO = keywordService.addKeyword(dto.getName());
        Assertions.assertEquals(1, responseDTO.getKeywordId());
        Assertions.assertEquals(1, keywords.size());
    }

}
