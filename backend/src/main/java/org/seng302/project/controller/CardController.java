package org.seng302.project.controller;

import net.minidev.json.JSONObject;
import org.seng302.project.model.Card;
import org.seng302.project.model.CardRepository;
import org.seng302.project.model.User;
import org.seng302.project.model.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


/**
 * REST controller for handling requests to do with marketplace cards.
 */
@RestController
public class CardController {
    private static final Logger logger = LoggerFactory.getLogger(CardController.class.getName());
    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    @Autowired

    public CardController(CardRepository cardRepository, UserRepository userRepository) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/cards")
    @ResponseStatus(HttpStatus.CREATED)
    public void createCard(@RequestBody JSONObject json) {
        logger.info("Request to create card");

        Integer creatorId = (Integer) json.getAsNumber("creatorId");

        //TODO: handle case when user is not found.
        User creator = userRepository.findById(creatorId).get();

        String section = json.getAsString("section");
        String title = json.getAsString("title");
        String description = json.getAsString("description");

        Card newCard = new Card(creator, section, title, description);
        cardRepository.save(newCard);

    }
}
