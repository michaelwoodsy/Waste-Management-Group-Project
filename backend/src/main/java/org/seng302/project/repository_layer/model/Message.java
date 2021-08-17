package org.seng302.project.repository_layer.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity for Messages between marketplace users, about a Card
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class Message {

    @Id // this field (attribute) is the primary key of the table
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement the ID
    @Column(name = "message_id")
    private Integer id;

    private String text;

    //Use to get receiverId when creating response DTO
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "card_id")
    private Card card;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id")
    private User sender;

    @Column(name = "created")
    private LocalDateTime created = LocalDateTime.now();

    @Column(name = "has_read")
    private boolean read;

    public Message(String text, User receiver, Card card, User sender) {
        this.text = text;
        this.receiver = receiver;
        this.card = card;
        this.sender = sender;
        this.read = false;
    }

}
