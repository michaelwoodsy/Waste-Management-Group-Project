package org.seng302.project.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Business class for storing data about a specific business.
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class Business {

    private Integer id; //Automatically generated and assigned by the server
    private String name;
    private String description;
    private String address; // TODO Use Address class once implemented.
    private String businessType;
    private Integer primaryAdministratorId;
    private List<User> administrators;
    private LocalDateTime created = LocalDateTime.now();

    public Business(String name, String description, String address, String businessType,
                    Integer primaryAdministratorId) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.businessType = businessType;
        this.primaryAdministratorId = primaryAdministratorId;
    }

    @Id // this field (attribute) is the table primary key
    @GeneratedValue // autoincrement the ID
    @Column(name = "id_business")
    public Integer getId() {
        return this.id;
    }

    //The API says this should be a list of User objects, so we can leave as is :)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_administers_business",
            joinColumns = @JoinColumn(name = "id_business"),
            inverseJoinColumns = @JoinColumn(name = "id_user")
    )
    public List<User> getAdministrators() {
        return this.administrators;
    }

}
