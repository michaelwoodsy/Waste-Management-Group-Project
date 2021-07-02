package org.seng302.project.serviceLayer.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.seng302.project.repositoryLayer.model.Keyword;
import org.seng302.project.repositoryLayer.repository.KeywordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * Test class for performing unit tests for the KeywordService class and its methods.
 */
@SpringBootTest
class KeywordServiceTest {

    @Autowired
    private KeywordService keywordService;

    @MockBean
    private KeywordRepository keywordRepository;


    /**
     * Tests that we can return a single matching keyword
     */
    @Test
    void keywordSearch_oneMatch() {
        given(keywordRepository.findAll(any(Specification.class)))
                .willReturn(List.of(new Keyword("Vegetables")));

        List<Keyword> returnedKeywords = keywordService.searchKeywords("Vege");

        Assertions.assertEquals(1, returnedKeywords.size());
        Assertions.assertEquals("Vegetables", returnedKeywords.get(0).getName());

    }

    /**
     * Tests that we can return multiple matching keywords
     */
    @Test
    void keywordSearch_matches() {
        given(keywordRepository.findAll(any(Specification.class)))
                .willReturn(List.of(new Keyword("Apples"),
                        new Keyword("Pineapples")));

        List<Keyword> returnedKeywords = keywordService.searchKeywords("appl");

        Assertions.assertEquals(2, returnedKeywords.size());
        Assertions.assertEquals("Apples", returnedKeywords.get(0).getName());
        Assertions.assertEquals("Pineapples", returnedKeywords.get(1).getName());

    }

    /**
     * Tests that we can return an empty list of matching keywords
     */
    @Test
    void keywordSearch_noMatches() {

        given(keywordRepository.findAll(any(Specification.class)))
                .willReturn(List.of());

        List<Keyword> returnedKeywords = keywordService.searchKeywords("Vege");
        Assertions.assertEquals(0, returnedKeywords.size());

    }

}
