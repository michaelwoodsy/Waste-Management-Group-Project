package org.seng302.project.serviceLayer.service;

import org.seng302.project.repositoryLayer.repository.MessageRepository;
import org.seng302.project.serviceLayer.dto.message.CreateMessageDTO;
import org.seng302.project.serviceLayer.dto.message.CreateMessageResponseDTO;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class with methods for handling Messages
 */
@Service
public class MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageService.class.getName());
    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    /**
     * Creates a new message
     * @return CreateMessageResponseDTO containing the new message's id
     */
    public CreateMessageResponseDTO createMessage(CreateMessageDTO requestDTO, AppUserDetails appUser) {

        logger.info("Request to create message about card with id {}", requestDTO.getCardId());

        //Use appUser to determine who sent the message
        //TODO: implement this function
        return new CreateMessageResponseDTO(1);

    }
}

