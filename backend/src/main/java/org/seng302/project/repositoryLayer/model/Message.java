package org.seng302.project.repositoryLayer.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Entity for Messages between marketplace users, about a Card
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class Message {

    private Integer id;
    private String text;

    //Use to get receiverId when creating response DTO
    private User receiver;
    private Card card;
    private User sender;

    @Id // this field (attribute) is the primary key of the table
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement the ID
    @Column(name = "message_id")
    public Integer getId() {
        return this.id;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receiver_id")
    public User getReceiver() {
        return this.receiver;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "card_id")
    public Card getCard() {
        return this.card;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id")
    public User getSender() {
        return this.sender;
    }
}
