package org.seng302.project.service_layer.dto.message;

import lombok.Data;
import org.seng302.project.repository_layer.model.Message;
import org.seng302.project.service_layer.dto.card.GetCardResponseDTO;
import org.seng302.project.service_layer.dto.user.GetUserDTO;

import java.time.LocalDateTime;

@Data
public class GetMessageDTO {

    private Integer id;
    private String text;
    private GetUserDTO receiver;
    private GetCardResponseDTO card;
    private GetUserDTO sender;
    private LocalDateTime created;
    private boolean read;

    public GetMessageDTO(Message message) {
        this.id = message.getId();
        this.text = message.getText();
        this.receiver = new GetUserDTO(message.getReceiver());
        this.card = new GetCardResponseDTO(message.getCard());
        this.sender = new GetUserDTO(message.getSender());
        this.created = message.getCreated();
        this.read = message.isRead();
    }

}
