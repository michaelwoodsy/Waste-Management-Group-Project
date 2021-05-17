package org.seng302.project.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "card_id")
    private Integer id;

    private String section;
    private String title;
    private String description;
    private LocalDateTime created = LocalDateTime.now();

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Id
    private Integer creatorId; // The id of the user that created this card

    public Card(Integer creatorId, String section, String title, String description) {
        this.creatorId = creatorId;
        this.section = section;
        this.title = title;
        this.description = description;
    }
}
