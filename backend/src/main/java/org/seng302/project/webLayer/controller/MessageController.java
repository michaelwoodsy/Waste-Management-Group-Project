package org.seng302.project.webLayer.controller;

import net.minidev.json.JSONObject;
import org.seng302.project.repositoryLayer.model.Message;
import org.seng302.project.serviceLayer.dto.message.CreateMessageDTO;
import org.seng302.project.serviceLayer.dto.message.CreateMessageResponseDTO;
import org.seng302.project.serviceLayer.service.MessageService;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Rest controller for Messages.
 */
@RestController
public class MessageController {

    public final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }


    /**
     * POST method for requests to create a message
     */
    @PostMapping("/users/{userId}/cards/{cardId}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateMessageResponseDTO createMessage(@PathVariable Integer userId,
                                                  @PathVariable Integer cardId,
                                                  @RequestBody JSONObject requestBody,
                                                  @AuthenticationPrincipal AppUserDetails appUser) {
        var text = requestBody.getAsString("text");
        var requestDTO = new CreateMessageDTO(userId, cardId, text);
        return messageService.createMessage(requestDTO, appUser);
    }

    /**
     * GET method for requests to retrieve a user's messages
     *
     * @param userId  The ID of the user whose messages we are retrieving
     * @param appUser The user currently logged in
     * @return List of the given user's messages (if they have any)
     */
    @GetMapping("/users/{userId}/messages")
    @ResponseStatus(HttpStatus.OK)
    public List<Message> getUserMessages(@PathVariable Integer userId,
                                         @AuthenticationPrincipal AppUserDetails appUser) {
        return messageService.getMessages(userId, appUser);
    }

    /**
     * Endpoint for deleting a message
     *
     * @param userId    the user Id of who the message was sent to
     * @param messageId the message Id
     * @param appUser   user details to check if the current user is allowed to delete this message
     */
    @DeleteMapping("/users/{userId}/messages/{messageId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteMessage(@PathVariable Integer userId,
                              @PathVariable Integer messageId,
                              @AuthenticationPrincipal AppUserDetails appUser) {
        messageService.deleteMessage(userId, messageId, appUser);
    }

}
