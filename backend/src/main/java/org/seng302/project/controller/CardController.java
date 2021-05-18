//package org.seng302.project.controller;
//
//import net.minidev.json.JSONObject;
//import org.seng302.project.exceptions.NoUserExistsException;
//import org.seng302.project.exceptions.RequiredFieldsMissingException;
//import org.seng302.project.model.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Optional;
//
///**
// * REST controller for handling requests to do with marketplace cards.
// */
//@RestController
//public class CardController {
//
//    private static final Logger logger = LoggerFactory.getLogger(CardController.class.getName());
//    private final CardRepository cardRepository;
//    private final UserRepository userRepository;
//
//    @Autowired
//
//    public CardController(CardRepository cardRepository, UserRepository userRepository) {
//        this.cardRepository = cardRepository;
//        this.userRepository = userRepository;
//    }
//
//    @PostMapping("/cards")
//    @ResponseStatus(HttpStatus.CREATED)
//    public void createCard(@RequestBody JSONObject json) {
//        logger.info("Request to create card");
////
//        try {
//            Get creator id
//            Integer creatorId = (Integer) json.getAsNumber("creatorId");
//
//
//
//
//            //If user id listed as card creator is not an id of a user
//            if (userRepository.findById(newCard.getCreatorId()).isEmpty()) {
//                NoUserExistsException exception = new NoUserExistsException(newCard.getCreatorId());
//                logger.error(exception.getMessage());
//                throw exception;
//            }
//
//            //If any of the required fields are empty; title, section, description
//            if (newCard.getTitle().equals("") || newCard.getSection().equals("") ||
//                    newCard.getDescription().equals("")) {
//                RequiredFieldsMissingException exception = new RequiredFieldsMissingException();
//                logger.error(exception.getMessage());
//                throw exception;
//            }
//
//            //TODO: 403	error if user not DGAA attempts card creation for another user.
//
//            cardRepository.save(newCard);
//            logger.info(String.format("Successful creation of card %d", newCard.getId()));
//
//        } catch (RequiredFieldsMissingException handledException) {
//            throw handledException;
//        } catch (Exception unexpectedException) {
//            logger.error(String.format("Unexpected error while creating user: %s", unexpectedException.getMessage()));
//            throw unexpectedException;
//        }
//    }
//}
