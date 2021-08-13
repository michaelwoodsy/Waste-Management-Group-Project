package org.seng302.project.service_layer.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.Card;
import org.seng302.project.repository_layer.model.Message;
import org.seng302.project.repository_layer.model.User;
import org.seng302.project.repository_layer.repository.CardRepository;
import org.seng302.project.repository_layer.repository.MessageRepository;
import org.seng302.project.repository_layer.repository.UserRepository;
import org.seng302.project.service_layer.dto.message.GetMessageDTO;
import org.seng302.project.service_layer.dto.message.PostMessageDTO;
import org.seng302.project.service_layer.exceptions.BadRequestException;
import org.seng302.project.service_layer.exceptions.NotAcceptableException;
import org.seng302.project.service_layer.exceptions.user.ForbiddenUserException;
import org.seng302.project.web_layer.authentication.AppUserDetails;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Test class for performing unit tests for the MessageService class and its methods.
 */
class MessageServiceTest extends AbstractInitializer {

    private MessageService messageService;
    private UserRepository userRepository;
    private CardRepository cardRepository;
    private MessageRepository messageRepository;

    private User testSender;
    private User testReceiver;
    private User testOtherUser;
    private Card testCard;
    private Message testMessage;

    /**
     * Initialises entities from AbstractInitializer
     */
    @BeforeEach
    void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        cardRepository = Mockito.mock(CardRepository.class);
        messageRepository = Mockito.mock(MessageRepository.class);
        messageService = new MessageService(messageRepository, userRepository, cardRepository);

        testSender = this.getTestUser();
        Mockito.when(userRepository.findByEmail(testSender.getEmail()))
                .thenReturn(List.of(testSender));
        Mockito.when(userRepository.findById(testSender.getId()))
                .thenReturn(Optional.of(testSender));

        testReceiver = this.getTestUserBusinessAdmin();
        Mockito.when(userRepository.findByEmail(testReceiver.getEmail()))
                .thenReturn(List.of(testReceiver));
        Mockito.when(userRepository.findById(testReceiver.getId()))
                .thenReturn(Optional.of(testReceiver));

        testOtherUser = this.getTestOtherUser();
        Mockito.when(userRepository.findById(testOtherUser.getId()))
                .thenReturn(Optional.of(testOtherUser));

        testCard = this.getTestCard();
        Mockito.when(cardRepository.findById(testCard.getId()))
                .thenReturn(Optional.of(testCard));

        testMessage = new Message("Is this still available?", testReceiver, testCard, testSender);
        testMessage.setId(1);

        Message testReceivedMessage = new Message("Yes it is still available", testSender, testCard, testReceiver);
        testReceivedMessage.setId(2);
        Mockito.when(messageRepository.findAllByReceiver(testSender))
                .thenReturn(List.of(testReceivedMessage));
    }


    /**
     * Tests the successful creation of a message
     */
    @Test
    void createMessage_success() {
        AppUserDetails appUser = new AppUserDetails(testSender);

        PostMessageDTO requestDTO = new PostMessageDTO(
                testReceiver.getId(), testCard.getId(), testMessage.getText());

        Mockito.when(messageRepository.save(Mockito.any(Message.class)))
                .thenReturn(testMessage);

        messageService.createMessage(requestDTO, appUser);

        ArgumentCaptor<Message> messageArgumentCaptor = ArgumentCaptor.forClass(Message.class);
        Mockito.verify(messageRepository).save(messageArgumentCaptor.capture());
        Message savedMessage = messageArgumentCaptor.getValue();

        Assertions.assertEquals(testMessage.getText(), savedMessage.getText());
        Assertions.assertEquals(testMessage.getCard().getId(), savedMessage.getCard().getId());
        Assertions.assertEquals(testMessage.getReceiver().getId(), savedMessage.getReceiver().getId());
        Assertions.assertEquals(testMessage.getSender().getId(), savedMessage.getSender().getId());


    }


    /**
     * Tests that creating a message with
     * an empty text field throws a BadRequestException
     */
    @Test
    void createMessage_emptyTextField_badRequestException() {
        AppUserDetails appUser = new AppUserDetails(testSender);

        PostMessageDTO requestDTO = new PostMessageDTO(
                testReceiver.getId(), testCard.getId(), "");

        Assertions.assertThrows(BadRequestException.class,
                () -> messageService.createMessage(requestDTO, appUser));
    }


    /**
     * Tests that creating a message with
     * a null text field throws a BadRequestException
     */
    @Test
    void createMessage_nullTextField_badRequestException() {
        AppUserDetails appUser = new AppUserDetails(testSender);

        PostMessageDTO requestDTO = new PostMessageDTO();
        requestDTO.setUserId(testReceiver.getId());
        requestDTO.setCardId(testCard.getId());

        Assertions.assertThrows(BadRequestException.class,
                () -> messageService.createMessage(requestDTO, appUser));
    }


    /**
     * Tests that creating a message with
     * a nonexistent user throws a NotAcceptableException
     */
    @Test
    void createMessage_nonExistentUser_notAcceptableException() {
        Mockito.when(userRepository.findById(Mockito.any(Integer.class)))
                .thenReturn(Optional.empty());

        AppUserDetails appUser = new AppUserDetails(testSender);

        PostMessageDTO requestDTO = new PostMessageDTO(
                testReceiver.getId(), testCard.getId(), testMessage.getText());

        Assertions.assertThrows(NotAcceptableException.class,
                () -> messageService.createMessage(requestDTO, appUser));

    }


    /**
     * Tests that creating a message with
     * a nonexistent card throws a NotAcceptableException
     */
    @Test
    void createMessage_nonExistentCard_notAcceptableException() {
        AppUserDetails appUser = new AppUserDetails(testSender);

        PostMessageDTO requestDTO = new PostMessageDTO(
                testReceiver.getId(), testCard.getId(), testMessage.getText());

        Mockito.when(cardRepository.findById(requestDTO.getCardId()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(NotAcceptableException.class,
                () -> messageService.createMessage(requestDTO, appUser));

    }

    /**
     * Tests the successful retrieval of the currently
     * logged in user's messages
     */
    @Test
    void getMessages_success() {
        AppUserDetails appUser = new AppUserDetails(testSender);

        List<GetMessageDTO> receivedMessages = messageService.getMessages(testSender.getId(), appUser);

        Assertions.assertEquals(1, receivedMessages.size());

        Assertions.assertEquals("Yes it is still available", receivedMessages.get(0).getText());
        Assertions.assertEquals(testReceiver.getId(), receivedMessages.get(0).getSender().getId());
        Assertions.assertEquals(testCard.getId(), receivedMessages.get(0).getCard().getId());
        Assertions.assertEquals(testSender.getId(), receivedMessages.get(0).getReceiver().getId());
    }

    /**
     * Tests that retrieving messages for a user that is
     * not the currently logged in user throws an exception
     */
    @Test
    void getMessages_notValidUser_error() {
        AppUserDetails appUser = new AppUserDetails(testSender);
        Integer userId = testOtherUser.getId();

        Assertions.assertThrows(ForbiddenUserException.class,
                () -> messageService.getMessages(userId, appUser));
    }

    /**
     * Tests that retrieving messages for a non-existent user
     * throws an exception
     */
    @Test
    void getMessages_nonExistentUser_error() {
        AppUserDetails appUser = new AppUserDetails(testSender);

        Assertions.assertThrows(NotAcceptableException.class,
                () -> messageService.getMessages(1000, appUser));
    }

    /**
     * Tests that successful deletion of a message
     * of the currently logged in user
     */
    @Test
    void deleteMessage_success() {
        //Test User
        AppUserDetails appUser = new AppUserDetails(testSender);

        //Get messages belonging to the test user
        List<Message> receivedMessagesBefore = messageRepository.findAllByReceiver(testSender);

        //Set the message that is to be deleted
        Message messageToDelete = receivedMessagesBefore.get(0);

        // Mock a message with id of the chosen message
        given(messageRepository.findById(messageToDelete.getId())).willReturn(Optional.of(messageToDelete));

        //Make request to delete message
        messageService.deleteMessage(testSender.getId(), messageToDelete.getId(), appUser);

        // Capture the message passed to the repository delete method
        ArgumentCaptor<Message> cardArgumentCaptor = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository).delete(cardArgumentCaptor.capture());
        Message deletedMessage = cardArgumentCaptor.getValue();

        // Assert the correct card was deleted
        Assertions.assertEquals(messageToDelete.getId(), deletedMessage.getId());
    }

    /**
     * Tests that deleting messages for a non-existent user
     * throws an exception
     */
    @Test
    void deleteMessage_nonExistentUser_error() {
        //Test User
        AppUserDetails appUser = new AppUserDetails(testSender);

        //Get messages belonging to the test user
        List<Message> receivedMessagesBefore = messageRepository.findAllByReceiver(testSender);

        //Set the message that is to be deleted
        Message messageToDelete = receivedMessagesBefore.get(0);

        // Mock a message with id of the chosen message
        given(messageRepository.findById(messageToDelete.getId())).willReturn(Optional.of(messageToDelete));

        //Make request to delete message
        Integer messageId = messageToDelete.getId();
        Assertions.assertThrows(NotAcceptableException.class,
                () -> messageService.deleteMessage(100, messageId, appUser));
    }


    /**
     * Tests that deleting messages for a user that is
     * not the currently logged in user throws an exception
     */
    @Test
    void deleteMessage_notValidUser_error() {
        //Test User
        AppUserDetails appUser = new AppUserDetails(testReceiver);

        //Get messages belonging to the test user
        List<Message> receivedMessagesBefore = messageRepository.findAllByReceiver(testSender);

        //Set the message that is to be deleted
        Message messageToDelete = receivedMessagesBefore.get(0);

        // Mock a message with id of the chosen message
        given(messageRepository.findById(messageToDelete.getId())).willReturn(Optional.of(messageToDelete));

        //Make request to delete message
        Integer userId = testSender.getId();
        Integer messageId = messageToDelete.getId();
        Assertions.assertThrows(ForbiddenUserException.class,
                () -> messageService.deleteMessage(userId, messageId, appUser));
    }

}
