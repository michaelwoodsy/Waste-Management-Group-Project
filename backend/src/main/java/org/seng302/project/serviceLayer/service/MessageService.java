package org.seng302.project.serviceLayer.service;

import org.seng302.project.repositoryLayer.model.Card;
import org.seng302.project.repositoryLayer.model.Message;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.CardRepository;
import org.seng302.project.repositoryLayer.repository.MessageRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.dto.message.CreateMessageDTO;
import org.seng302.project.serviceLayer.dto.message.CreateMessageResponseDTO;
import org.seng302.project.serviceLayer.exceptions.BadRequestException;
import org.seng302.project.serviceLayer.exceptions.NotAcceptableException;
import org.seng302.project.serviceLayer.exceptions.user.ForbiddenUserException;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class with methods for handling Messages
 */
@Service
public class MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageService.class.getName());
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final CardRepository cardRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository,
                          UserRepository userRepository,
                          CardRepository cardRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
    }

    /**
     * Creates a new message
     *
     * @param requestDTO containing the message text,
     *                   the id of who the message is for
     *                   and the id of the card the message is about
     * @return CreateMessageResponseDTO containing the new message's id
     */
    public CreateMessageResponseDTO createMessage(CreateMessageDTO requestDTO, AppUserDetails appUser) {
        logger.info("Request to create message about card with id {}", requestDTO.getCardId());

        // Get the logged in user from the users email
        String userEmail = appUser.getUsername();
        var loggedInUser = userRepository.findByEmail(userEmail).get(0);

        Optional<User> receivingUserOptional = userRepository.findById(requestDTO.getUserId());
        if (receivingUserOptional.isEmpty()) {
            throw new NotAcceptableException(String.format("There is no user that exists with the id %d",
                    requestDTO.getUserId()));
        }
        var receivingUser = receivingUserOptional.get();

        Optional<Card> cardOptional = cardRepository.findById(requestDTO.getCardId());
        if (cardOptional.isEmpty()) {
            throw new NotAcceptableException(String.format("There is no card that exists with the id %d",
                    requestDTO.getCardId()));
        }
        var card = cardOptional.get();

        if (requestDTO.getText() == null || requestDTO.getText().equals("")) {
            throw new BadRequestException("Message is missing 'text' field");
        }

        var newMessage = new Message(requestDTO.getText(), receivingUser, card, loggedInUser);
        Integer messageId = messageRepository.save(newMessage).getId();

        return new CreateMessageResponseDTO(messageId);
    }

    /**
     * Called by the deleteMessage() method in MessageController.
     * Handles the business logic for deleting a user's message,
     * throws exceptions up to the controller to handle
     *
     * @param userId    ID of the receiver of the message
     * @param messageId ID of the message to delete
     * @param appUser   the currently logged in user
     */
    public void deleteMessage(Integer userId, Integer messageId, AppUserDetails appUser) {
        try {
            logger.info("Request to delete message with id {}", messageId);

            if (userRepository.findById(userId).isEmpty()) {
                var message = String.format("There is no user that exists with the id %d", userId);
                throw new NotAcceptableException(message);
            }

            var loggedInUser = userRepository.findByEmail(appUser.getUsername()).get(0);

            // Check if the logged in user is the same user whose messages we are retrieving

            if (!loggedInUser.getId().equals(userId)) {
                throw new ForbiddenUserException(userId);
            }

            // Get message from the repository
            Optional<Message> foundMessage = messageRepository.findById(messageId);

            // Check if the message exists
            if (foundMessage.isEmpty()) {
                var exception = new NotAcceptableException(String.format("No message exists with ID %s", messageId));
                logger.warn(exception.getMessage());
                throw exception;
            }

            var message = foundMessage.get();

            // Delete the message
            messageRepository.delete(message);
        } catch (ForbiddenUserException | NotAcceptableException handledException) {
            logger.error(handledException.getMessage());
            throw handledException;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while deleting a user's messages: %s", unhandledException.getMessage()));
            throw unhandledException;
        }
    }

    /**
     * Method that gets all the messages for a given user. Only the given logged in
     * user can view their own messages.
     *
     * @param userId  The ID of the user whose messages we are retrieving
     * @param appUser The user currently logged in
     * @return List of the given user's messages (if they have any)
     */
    public List<Message> getMessages(Integer userId, AppUserDetails appUser) {
        try {
            logger.info("Request to get messages for user with id {}", userId);

            // Get the logged in user from the users email
            String userEmail = appUser.getUsername();
            var loggedInUser = userRepository.findByEmail(userEmail).get(0);

            // Get the user whose messages we want
            Optional<User> userResult = userRepository.findById(userId);

            // Check if the user exists
            if (userResult.isEmpty()) {
                throw new NotAcceptableException(String.format("No User exists with ID %d", userId));
            }
            // We know user exists so retrieve user properly
            var user = userResult.get();

            // Check if the logged in user is the same user whose messages we are retrieving
            if (!loggedInUser.getId().equals(user.getId())) {
                throw new ForbiddenUserException(userId);
            }

            // Get the user's messages
            return messageRepository.findAllByReceiver(user);
        } catch (ForbiddenUserException | NotAcceptableException handledException) {
            logger.error(handledException.getMessage());
            throw handledException;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while retrieving user's messages: %s", unhandledException.getMessage()));
            throw unhandledException;
        }
    }
}

