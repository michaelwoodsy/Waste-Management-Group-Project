package org.seng302.project.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Card class for storing created marketplace cards.
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Integer id;
    private User creator;
    private String section;
    private String title;
    private String description;
    private LocalDateTime created = LocalDateTime.now();
    private LocalDateTime displayPeriodEnd; //TODO: complete initialisation

    /**
     * Constructor for creating a new Card object.
     *
     * @param creator                     User creating card.
     * @param section                     Marketplace section the Card falls to.
     * @param title                       Title of the card.
     * @param description                 Description of the card.
     */
    public Card(User creator, String section, String title, String description) {
        this.creator = creator;
        this.section = section;
        this.title = title;
        this.description = description;
    }
}
