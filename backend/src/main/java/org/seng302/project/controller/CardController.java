package org.seng302.project.controller;

import net.minidev.json.JSONObject;
import org.seng302.project.controller.authentication.AppUserDetails;
import org.seng302.project.exceptions.*;
import org.seng302.project.exceptions.card.NoCardExistsException;
import org.seng302.project.exceptions.card.ForbiddenCardActionException;
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


    @PostMapping("/cards")
    @ResponseStatus(HttpStatus.CREATED)
    public JSONObject createCard(@RequestBody JSONObject json, @AuthenticationPrincipal AppUserDetails appUser) {
        try {
            logger.info("Request to create card");

            // Get the logged in user
            Integer creatorId = (Integer) json.getAsNumber("creatorId");
            String section = json.getAsString("section");
            String title = json.getAsString("title");
            String description = json.getAsString("description");
            String keywords = json.getAsString("keyworeds");

            User loggedInUser = userRepository.findByEmail(appUser.getUsername()).get(0);
            Optional<User> creator = userRepository.findById(creatorId);

            //TODO: if loggedInUser != creatorId, check loggedInUser is GAA
            if (!loggedInUser.getId().equals(creatorId) {
                if (!loggedInUser.getRole().equals("globalApplicationAdmin") || !loggedInUser.getRole().equals("defaultGlobalApplicationAdmin")) {
                    throw new ForbiddenCardActionException()
                }
            }

            //check that listed card creator exists.
            if (creator.isEmpty()) {
                NoUserExistsException noUserExistsException = new NoUserExistsException(creatorId);
                logger.warn(noUserExistsException.getMessage());
                throw noUserExistsException;
            }

            // check required fields
            if (section.equals("") || title.equals("") || description.equals("")) {
                RequiredFieldsMissingException requiredFieldsMissingException = new RequiredFieldsMissingException();
                logger.warn(requiredFieldsMissingException.getMessage());
                throw requiredFieldsMissingException;
            }

            Card newCard = new Card(creator.get(), section, title, description, keywords);
            Integer cardId = cardRepository.save(newCard).getId();
            JSONObject response = new JSONObject();
            response.put("cardId", cardId);
            return response;
        } catch (NoUserExistsException | RequiredFieldsMissingException expectedException) {
            throw expectedException;
        } catch (Exception exception) {
            logger.error(String.format("Unexpected error while creating card: %s", exception.getMessage()));
            throw exception;
        }
    }


    /**
     * Returns the current logged in user that made the request.
     * @param appUser The AppUserDetails object passed in from the authentication principle.
     * @return User: the user that made the request.
     */
    private User getLoggedInUser(AppUserDetails appUser) {
        String userEmail = appUser.getUsername();
        return userRepository.findByEmail(userEmail).get(0);
    }

    /**
     * Checks if the user is the owner or administrator of the business. Throws an exception if they aren't
     * @param user The user to check.
     * @param card The card to check.
     * @throws ForbiddenAdministratorActionException Thrown if the user isn't and owner or admin of the business.
     */
    private void checkUserCanEditCard(User user, Card card) throws ForbiddenCardActionException {
        // Check if the logged in user is the card creator or a GAA
        if (!card.getCreator().getId().equals(user.getId()) && !user.isGAA()) {
            ForbiddenCardActionException exception = new ForbiddenCardActionException();
            logger.warn(exception.getMessage());
            throw exception;
        }
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

    /**
     * Extends the card with id cardID's displayPeriodEnd date by 12 days.
     * @param id of the card to extend.
     */
    @PutMapping("/cards/{id}/extenddisplayperiod")
    @ResponseStatus(HttpStatus.OK)
    public void extendCardDisplayPeriod(@PathVariable int id, @AuthenticationPrincipal AppUserDetails appUser) {

        logger.info(String.format("Request to extend display period of card with id %d", id));
        try {
            User loggedInUser = getLoggedInUser(appUser);

            // Get card from repository
            Optional<Card> foundCard = cardRepository.findById(id);
            // Check if the card exists
            if (foundCard.isEmpty()) {
                NoCardExistsException exception = new NoCardExistsException(id);
                logger.warn(exception.getMessage());
                throw exception;
            }
            Card cardToExtend = foundCard.get();

            //Checks if the logged in user is allowed to edit this card
            checkUserCanEditCard(loggedInUser, cardToExtend);

            //Change the cards displayPeriodEnd date to 12 days in the future
            cardToExtend.setDisplayPeriodEnd(cardToExtend.getDisplayPeriodEnd().plusWeeks(2));

            cardRepository.save(cardToExtend);

        } catch (NoCardExistsException | ForbiddenCardActionException foundException) {
            logger.warn(foundException.getMessage());
            throw foundException;
        } catch (Exception exception) {
            logger.error(String.format("Unexpected error while extending display period of card: %s", exception.getMessage()));
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
                if (requestMaker.getRole().equals("user")) {
                    throw new ForbiddenCardActionException();
                }
            }

            //200 if card successfully deleted
            cardRepository.deleteById(id);

        } catch (NoCardExistsException | ForbiddenCardActionException expectedException) {
            logger.warn(expectedException.getMessage());
            throw expectedException;
        } catch (Exception unexpectedException) {
            logger.error(String.format("Unexpected error while deleting card: %s", unexpectedException.getMessage()));
            throw unexpectedException;
        }

    }
}
