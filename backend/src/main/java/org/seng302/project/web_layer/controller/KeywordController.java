package org.seng302.project.web_layer.controller;

import org.seng302.project.repository_layer.model.Keyword;
import org.seng302.project.repository_layer.model.User;
import org.seng302.project.repository_layer.repository.UserRepository;
import org.seng302.project.service_layer.dto.keyword.AddKeywordDTO;
import org.seng302.project.service_layer.dto.keyword.AddKeywordResponseDTO;
import org.seng302.project.service_layer.exceptions.dgaa.ForbiddenDGAAActionException;
import org.seng302.project.service_layer.service.KeywordService;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * REST controller for handling requests related to Keywords
 */
@RestController
public class KeywordController {

    private static final Logger logger = LoggerFactory.getLogger(KeywordController.class.getName());
    private final KeywordService keywordService;
    private final UserRepository userRepository; // wont be needed once user controller is refactored

    @Autowired
    public KeywordController(KeywordService keywordService, UserRepository userRepository) {
        this.keywordService = keywordService;
        this.userRepository = userRepository;
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

    /**
     * Deletes a keyword, and removes it from all cards.
     *
     * @param keywordId Id of the keyword to delete.
     * @param appUser The current logged in user.
     */
    @DeleteMapping("/keywords/{keywordId}")
    public void deleteKeyword(
            @PathVariable("keywordId") Integer keywordId,
            @AuthenticationPrincipal AppUserDetails appUser) {
        // Get the logged in user
        User user = userRepository.findByEmail(appUser.getUsername()).get(0);

        // Check the user is a gaa
        if (!user.isGAA()) {
            logger.warn("User {} attempted to delete a keyword but is not a GAA", user.getEmail());
            throw new ForbiddenDGAAActionException();
        }

        // Delete the keyword
        keywordService.deleteKeyword(keywordId);
    }

}
