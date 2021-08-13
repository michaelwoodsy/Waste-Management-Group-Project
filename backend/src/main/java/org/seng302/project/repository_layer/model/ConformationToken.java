package org.seng302.project.repository_layer.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Conformation Token class used when validating a users forgotten password request.
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class ConformationToken {

    @Id // this field (attribute) is the primary key of the table
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement the ID
    private Integer id; // automatically generated and assigned by the server

    private String token;

    private LocalDateTime created = LocalDateTime.now();

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public ConformationToken(String token, User user) {
        this.token = token;
        this.user = user;
    }
}
