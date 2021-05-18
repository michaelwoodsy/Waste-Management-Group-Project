package org.seng302.project.controller;

import org.seng302.project.controller.authentication.AppUserDetails;
import org.seng302.project.model.Card;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * REST controller for handling requests to do with marketplace cards.
 */
@RestController
public class CardController {

    /**
     * Endpoint for getting all cards in a section.
     *
     * @param section Section to get all cards from.
     * @param appUser The user that made the request.
     * @return List of Cards in the corresponding section.
     */
    @GetMapping("/cards")
    public List<Card> getAllCards(
            @RequestParam int section,
            @AuthenticationPrincipal AppUserDetails appUser) {
        return List.of();
    }
}
