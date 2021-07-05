package org.seng302.project.webLayer.controller;

import org.seng302.project.repositoryLayer.model.Keyword;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.ForbiddenAdministratorActionException;
import org.seng302.project.serviceLayer.exceptions.dgaa.ForbiddenDGAAActionException;
import org.seng302.project.serviceLayer.service.KeywordService;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for handling requests related to Keywords
 */
@RestController
public class KeywordController {

    private final KeywordService keywordService;
    private final UserRepository userRepository; // wont be needed once user controller is refactored
    private static final Logger logger = LoggerFactory.getLogger(KeywordController.class.getName());

    @Autowired
    public KeywordController(KeywordService keywordService, UserRepository userRepository) {
        this.keywordService = keywordService;
        this. userRepository = userRepository;
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

    @DeleteMapping("/keywords/{keywordId}")
    public void deleteKeyword(
            @PathVariable("keywordId") Integer keywordId,
            @AuthenticationPrincipal AppUserDetails appUser) {
        // Get the logged in user
        User user = userRepository.findByEmail(appUser.getUsername()).get(0);

        // Check the user is a gaa
        if (!user.isGAA()) {
            logger.warn(String.format("User %s attempted to delete a keyword but is not a GAA", user.getEmail()));
            throw new ForbiddenDGAAActionException();
        }

        // Delete the keyword
        keywordService.deleteKeyword(keywordId);
    }

}
