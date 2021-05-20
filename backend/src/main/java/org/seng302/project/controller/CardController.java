package org.seng302.project.controller;

import org.seng302.project.controller.authentication.AppUserDetails;
import org.seng302.project.exceptions.card.ForbiddenCardActionException;
import org.seng302.project.exceptions.card.NoCardExistsException;
import org.seng302.project.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
    public CardController(
            CardRepository cardRepository,
            UserRepository userRepository) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
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



    @DeleteMapping("/cards/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteCard(@PathVariable int id, @AuthenticationPrincipal AppUserDetails appUser) {
        logger.info(String.format("Request to delete card with id %d", id));
        try {
            //406 if card cannot be found (NoCardExistsException)
            Optional<Card> cardOptional = cardRepository.findById(id);
            if (cardOptional.isEmpty()) {
                throw new NoCardExistsException(id);
            }
            Card retrievedCard = cardOptional.get();

            //403 if card is not yours or you aren't DGAA/GAA (ForbiddenCardActionException)
            User requestMaker = userRepository.findByEmail(appUser.getUsername()).get(0);
            if (!requestMaker.getId().equals(retrievedCard.getCreator().getId())) {
                //TODO: check if requestMaker is GAA/DGAA before throwing this exception
                throw new ForbiddenCardActionException();
            }

            //TODO: 200 if card successfully deleted


        } catch (NoCardExistsException | ForbiddenCardActionException expectedException) {
            logger.warn(expectedException.getMessage());
            throw expectedException;
        } catch (Exception unexpectedException) {
            logger.error(String.format("Unexpected error while deleting card: %s", unexpectedException.getMessage()));
            throw unexpectedException;
        }

    }
}
