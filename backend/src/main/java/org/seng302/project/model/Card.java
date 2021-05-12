package org.seng302.project.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Card class for storing created marketplace cards.
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class Card {

    private Integer id; // automatically generated and assigned by the server
    private Integer creatorId;
    private String section;
    private String title;
    private String description;
    private List<CardKeyword> keywords = new ArrayList<CardKeyword>();
    //TODO: create Keyword class

    public Card(Integer creatorId, String section, String title, String description, ArrayList<Integer> keywordIds) {
        this.creatorId = creatorId;
        this.section = section;
        this.title = title;
        this.description = description;
        //for k in keywordIds
        //this.keywords.add Keyword.get(k)
    }

    @Id // this field (attribute) is the primary key of the table
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement the ID
    @Column(name = "card_id")
    public Integer getId() {
        return this.id;
    }
}
