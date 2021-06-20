package org.seng302.project.repositoryLayer.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Keyword class for storing individual keywords.
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class Keyword {

    private Integer Id;
    private String name;
    private LocalDateTime created = LocalDateTime.now();

    public Keyword(String name) {
        this.name = name;
    }

    @Id // this field (attribute) is the primary key of the table
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement the ID
    @Column(name = "keyword_id")
    public Integer getId() {
        return this.Id;
    }


}
