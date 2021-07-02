package org.seng302.project.repositoryLayer.model;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Card class for storing created marketplace cards.
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class Card {

    private Integer id;
    private User creator;
    private String section;
    private String title;
    private String description;
    private List<Keyword> keywords = new ArrayList<>();
    private LocalDateTime created = LocalDateTime.now();
    private LocalDateTime displayPeriodEnd = created.plusWeeks(2); // Display period is currently set at 2 weeks in Backlog

    /**
     * Constructor for creating a new Card object.
     *
     * @param creator                     User creating card.
     * @param section                     Marketplace section the Card falls to.
     * @param title                       Title of the card.
     * @param description                 Description of the card.
     */
    public Card(User creator, String section, String title, String description, List<Keyword> keywords) {
        this.creator = creator;
        this.section = section;
        this.title = title;
        this.description = description;
        this.keywords = keywords;
    }

    @Id // this field (attribute) is the primary key of the table
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement the ID
    @Column(name = "card_id")
    public Integer getId() {
        return this.id;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    public User getCreator() {
        return this.creator;
    }


    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "card_has_keyword",
            joinColumns = @JoinColumn(name = "card_id"),
            inverseJoinColumns = @JoinColumn(name = "keyword_id")
    )
    public Set<Keyword> getKeywords() {
        return new HashSet(this.keywords);
    }

    /**
     * Checks if a user has permission to edit the card
     *
     * @param user user trying to edit the card
     * @return true if the user can edit the card
     */
    public boolean userCanEdit(User user) {
        return user.isGAA() || user.getId().equals(this.creator.getId());
    }
}
