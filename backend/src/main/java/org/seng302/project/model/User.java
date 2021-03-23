package org.seng302.project.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;


/**
 * User class for storing individual user accounts.
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method
@NamedEntityGraph( //Allows us to fetch businessesAdministered whenever getting a User (prevent LazyInitialisationException)
        name = "graph.userBusinessesAdministered",
        attributeNodes = @NamedAttributeNode("businessesAdministered")
)
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class User {

    private final LocalDateTime created = LocalDateTime.now();
    @Id // this field (attribute) is the primary key of the table
    @GeneratedValue // autoincrement the ID
    private Integer id; // automatically generated and assigned by the server
    private String firstName;
    private String lastName;
    private String middleName;
    private String nickname;
    private String bio;
    private String email;
    private String dateOfBirth;
    private String phoneNumber;
    private String homeAddress;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    // One of [ user, globalApplicationAdmin, defaultGlobalApplicationAdmin ]
    private String role; // This property should only be shown to Global org.seng302.project.controller.Application Admins

    // TODO: change this to a list of Business ids, not objects
    @ManyToMany(targetEntity = Business.class)
    private List<Business> businessesAdministered;

    public User(String firstName, String lastName, String middleName,
                String nickname, String bio, String email, String dateOfBirth,
                String phoneNumber, String homeAddress, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.nickname = nickname;
        this.bio = bio;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.homeAddress = homeAddress;
        this.password = password;
        this.role = "user";
    }

}