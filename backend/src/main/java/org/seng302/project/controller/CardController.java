package org.seng302.project.controller;

import org.seng302.project.controller.authentication.AppUserDetails;
import org.seng302.project.model.Card;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.seng302.project.exceptions.*;
import org.seng302.project.exceptions.NoCardExistsException;
import org.seng302.project.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


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
    private static final Logger logger = LoggerFactory.getLogger(CardController.class.getName());
    private final CardRepository cardRepository;


    @Autowired
    public CardController(
            CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }


    /**
     * Gets a single card with id cardID.
     * @param id of the card to retrieve.
     * @return a single card object.
     */
    @GetMapping("/cards/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Card getCard(@PathVariable int id) {

        logger.info(String.format("Request to get card with id %d", id));
        try {
            return cardRepository.findById(id).orElseThrow(() -> new NoCardExistsException(id));
        } catch (NoCardExistsException noCardExistsException) {
            logger.warn(noCardExistsException.getMessage());
            throw noCardExistsException;
        } catch (Exception exception) {
            logger.error(String.format("Unexpected error while getting card: %s", exception.getMessage()));
            throw exception;
        }
    }
}
