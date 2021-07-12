package org.seng302.project.serviceLayer.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repositoryLayer.model.Card;
import org.seng302.project.repositoryLayer.model.Message;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.CardRepository;
import org.seng302.project.repositoryLayer.repository.MessageRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.dto.message.CreateMessageDTO;
import org.seng302.project.serviceLayer.exceptions.BadRequestException;
import org.seng302.project.serviceLayer.exceptions.NotAcceptableException;
import org.seng302.project.serviceLayer.exceptions.user.ForbiddenUserException;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Test class for performing unit tests for the MessageService class and its methods.
 */
@SpringBootTest
class MessageServiceTest extends AbstractInitializer {

    @Autowired
    private MessageService messageService;

    @MockBean
    private MessageRepository messageRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CardRepository cardRepository;

    private User testSender;
    private User testReceiver;
    private Card testCard;
    private Message testMessage;
    private Message testReceivedMessage;

    /**
     * Initialises entities from AbstractInitializer
     */
    @BeforeEach
    void setup() {
        this.initialise();
        testSender = this.getTestUser();
        Mockito.when(userRepository.findByEmail(testSender.getEmail()))
            .thenReturn(List.of(testSender));
        Mockito.when(userRepository.findById(testSender.getId()))
                .thenReturn(Optional.of(testSender));

        testReceiver = this.getTestUserBusinessAdmin();
        Mockito.when(userRepository.findById(testReceiver.getId()))
                .thenReturn(Optional.of(testReceiver));

        testCard = new Card(testReceiver, "Exchange", "Apples for your bananas",
                "Will exchange my apples for bananas", Collections.emptySet());
        testCard.setId(1);
        Mockito.when(cardRepository.findById(testCard.getId()))
                .thenReturn(Optional.of(testCard));

        testMessage = new Message("Is this still available?", testReceiver, testCard, testSender);
        testMessage.setId(1);

        testReceivedMessage = new Message("Yes it is still available", testSender, testCard, testReceiver);
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

        CreateMessageDTO requestDTO = new CreateMessageDTO(
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

        CreateMessageDTO requestDTO = new CreateMessageDTO(
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

        CreateMessageDTO requestDTO = new CreateMessageDTO();
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

        CreateMessageDTO requestDTO = new CreateMessageDTO(
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

        Mockito.when(cardRepository.findById(Mockito.any(Integer.class)))
                .thenReturn(Optional.empty());

        AppUserDetails appUser = new AppUserDetails(testSender);

        CreateMessageDTO requestDTO = new CreateMessageDTO(
                testReceiver.getId(), testCard.getId(), testMessage.getText());

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

        List<Message> receivedMessages = messageService.getMessages(testSender.getId(), appUser);

        Assertions.assertEquals(1, receivedMessages.size());

        Assertions.assertEquals("Yes it is still available", receivedMessages.get(0).getText());
        Assertions.assertEquals(testReceiver, receivedMessages.get(0).getSender());
        Assertions.assertEquals(testCard, receivedMessages.get(0).getCard());
        Assertions.assertEquals(testSender, receivedMessages.get(0).getReceiver());
    }

    /**
     * Tests that retrieving messages for a user that is
     * not the currently logged in user throws an exception
     */
    @Test
    void getMessages_notValidUser_error() {
       AppUserDetails appUser = new AppUserDetails(testSender);

        Assertions.assertThrows(NotAcceptableException.class,
                () -> messageService.getMessages(100, appUser));
    }

    /**
     * Tests that retrieving messages for a non-existent user
     * throws an exception
     */
    @Test
    void getMessages_nonExistentUser_error() {
        AppUserDetails appUser = new AppUserDetails(testSender);

        Assertions.assertThrows(ForbiddenUserException.class,
                () -> messageService.getMessages(3, appUser));
    }

}
