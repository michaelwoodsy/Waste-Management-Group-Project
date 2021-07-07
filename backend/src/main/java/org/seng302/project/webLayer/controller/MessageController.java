package org.seng302.project.webLayer.controller;

import net.minidev.json.JSONObject;
import org.seng302.project.serviceLayer.dto.message.CreateMessageDTO;
import org.seng302.project.serviceLayer.dto.message.CreateMessageResponseDTO;
import org.seng302.project.serviceLayer.service.MessageService;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
}
