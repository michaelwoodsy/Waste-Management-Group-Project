package org.seng302.project.controller;

import org.seng302.project.exceptions.*;
import org.seng302.project.exceptions.NoCardExistsException;
import org.seng302.project.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


/**
 * REST controller for handling requests to do with marketplace cards.
 */
@RestController
public class CardController {

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
    public Card getBusiness(@PathVariable int id) {

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
