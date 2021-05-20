package org.seng302.project.controller;

import net.minidev.json.JSONObject;
import org.seng302.project.controller.authentication.AppUserDetails;
import org.seng302.project.exceptions.InvalidLoginException;
import org.seng302.project.exceptions.NoBusinessExistsException;
import org.seng302.project.exceptions.NoUserExistsException;
import org.seng302.project.exceptions.RequiredFieldsMissingException;
import org.seng302.project.model.Card;
import org.seng302.project.model.CardRepository;
import org.seng302.project.model.User;
import org.seng302.project.model.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Optional;


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
    public void createCard(@RequestBody JSONObject json, @AuthenticationPrincipal AppUserDetails appUser) {
        try {
            logger.info("Request to create card");

            // Get the logged in user
            Integer creatorId = (Integer) json.getAsNumber("creatorId").get();
            String section = json.getAsString("section");
            String title = json.getAsString("title");
            String description = json.getAsString("description");

            User loggedInUser = userRepository.findByEmail(userEmail).get(0);
            Optional<User> creator = userRepository.findById(creatorId);

            //TODO: if loggedInUser != creatorId, check loggedInUser is GAA

            //check that listed card creator exists.
            f (creator.isEmpty()) {
                NoUserExistsException noUserExistsException = new NoUserExistsException(creatorId)
                logger.warn(noUserExistsException.getMessage());
                throw noUserExistsException;
            }

            // check required fields
            if (section.equals("") || title.equals("") || description.equals("")) {
                RequiredFieldsMissingException requiredFieldsMissingException = new RequiredFieldsMissingException();
                logger.warn(requiredFieldsMissingException.getMessage());
                throw requiredFieldsMissingException;
            }

            Card newCard = new Card(creator.get(), section, title, description);
            cardRepository.save(newCard);
        } catch (NoUserExistsException | RequiredFieldsMissingException expectedException) {
            throw expectedException;
        } catch (Exception exception) {
            logger.error(String.format("Unexpected error while creating card: %s", exception.getMessage()));
            throw exception;
        }
    }
}
