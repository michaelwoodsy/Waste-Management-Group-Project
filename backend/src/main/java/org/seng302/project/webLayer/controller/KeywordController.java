package org.seng302.project.webLayer.controller;

import org.seng302.project.serviceLayer.dto.keyword.AddKeywordDTO;
import org.seng302.project.serviceLayer.dto.keyword.AddKeywordResponseDTO;
import org.seng302.project.repositoryLayer.model.Keyword;
import org.seng302.project.serviceLayer.service.KeywordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.util.List;

/**
 * REST controller for handling requests related to Keywords
 */
@RestController
public class KeywordController {

    private final KeywordService keywordService;

    @Autowired
    public KeywordController(KeywordService keywordService) {
        this.keywordService = keywordService;
    }

    /**
     * Controller method which handles an HTTP request for adding a new keyword.
     *
     * @param dto Object containing the name of the new keyword to add.
     * @return a response object with the ID of the keyword that has been added.
     */
    @PostMapping("/keywords")
    @ResponseStatus(HttpStatus.CREATED)
    public AddKeywordResponseDTO addKeyword(@Valid @RequestBody AddKeywordDTO dto) {
        return keywordService.addKeyword(dto.getName());
    }

    /**
     * Searches for keywords that include the string searchQuery
     *
     * @param searchQuery string to search by
     * @return a list of keywords that match the partialKeyword
     */
    @GetMapping("/keywords/search")
    @ResponseStatus(HttpStatus.OK)
    public List<Keyword> searchKeywords(@RequestParam("searchQuery") String searchQuery) {
        return keywordService.searchKeywords(searchQuery);
    }


}
