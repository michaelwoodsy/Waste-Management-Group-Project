package org.seng302.project.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Business class for storing data about a specific business.
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class Business {

    private Integer id; // automatically generated and assigned by database connection.
    private String name;
    private String description;
    private String address; // TODO Use Address class once implemented.
    private String businessType;
    private Integer primaryAdministratorId;
    private Set<User> administrators = new HashSet<>();
    private LocalDateTime created = LocalDateTime.now();

    /**
     * Constructor for creating a new Business object.
     *
     * @param name Name of the business.
     * @param description Description of the business.
     * @param address Address of the business.
     * @param businessType Type of the business. Valid types defined in BusinessType.
     * @param primaryAdministratorId ID of the User who creates the Business.
     */
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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_administers_business",
            joinColumns = @JoinColumn(name = "id_business"),
            inverseJoinColumns = @JoinColumn(name = "id_user")
    )
    public Set<User> getAdministrators() {
        return this.administrators;
    }

    public void addAdministrator(User admin) {
        this.administrators.add(admin);
    }

}
