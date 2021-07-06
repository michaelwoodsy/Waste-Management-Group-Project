package org.seng302.project.serviceLayer.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.seng302.project.repositoryLayer.model.Keyword;
import org.seng302.project.repositoryLayer.model.NewKeywordNotification;
import org.seng302.project.repositoryLayer.repository.CardRepository;
import org.seng302.project.repositoryLayer.repository.KeywordRepository;
import org.seng302.project.repositoryLayer.repository.NewKeywordNotificationRepository;
import org.seng302.project.serviceLayer.dto.keyword.AddKeywordDTO;
import org.seng302.project.serviceLayer.dto.keyword.AddKeywordResponseDTO;
import org.seng302.project.serviceLayer.exceptions.NotAcceptableException;
import org.seng302.project.serviceLayer.exceptions.keyword.KeywordExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

    @MockBean
    private CardRepository cardRepository;
    @MockBean
    private NewKeywordNotificationRepository newKeywordNotificationRepository;

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

        ArgumentCaptor<NewKeywordNotification> adminNotificationArgumentCaptor = ArgumentCaptor
                .forClass(NewKeywordNotification.class);
        Mockito.verify(newKeywordNotificationRepository).save(adminNotificationArgumentCaptor.capture());
        Keyword notificationKeyword = adminNotificationArgumentCaptor.getValue().getKeyword();
        Assertions.assertEquals("TestKeyword", notificationKeyword.getName());
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
        String dtoName = dto.getName();
        Assertions.assertThrows(KeywordExistsException.class,
                () -> keywordService.addKeyword(dtoName));
    }

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

    /**
     * Tests deleting a keyword that exists in the repository calls delete method on the repository.
     */
    @Test
    void deleteKeyword_keywordExists() {
        Keyword keywordToDelete = new Keyword("bean");

        // Mock a keyword with id 1
        given(keywordRepository.findById(1)).willReturn(Optional.of(keywordToDelete));

        // Run the method
        keywordService.deleteKeyword(1);

        // Capture the keyword passed to the repository delete method
        ArgumentCaptor<Keyword> cardArgumentCaptor = ArgumentCaptor.forClass(Keyword.class);
        verify(keywordRepository).delete(cardArgumentCaptor.capture());
        Keyword deletedKeyword = cardArgumentCaptor.getValue();

        // Assert the correct card was deleted
        Assertions.assertEquals(keywordToDelete.getName(), deletedKeyword.getName());
    }

    /**
     * Tests deleting a keyword that doesn't exist raises an exception.
     */
    @Test
    void deleteKeyword_keywordNotExist() {
        // Mock a keyword with id 1 returning empty
        given(keywordRepository.findById(1)).willReturn(Optional.empty());

        // Run the method
        Assertions.assertThrows(
                NotAcceptableException.class,
                () -> keywordService.deleteKeyword(1),
                "Expected deleteKeyword() to throw, but it didn't"
        );

        // verify the delete method was not called
        verify(keywordRepository, times(0)).delete(any(Keyword.class));
    }

}
