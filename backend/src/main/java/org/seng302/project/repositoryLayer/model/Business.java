package org.seng302.project.repositoryLayer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.UserNotAdministratorException;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    private Address address;
    private String businessType;
    private Integer primaryAdministratorId;
    private List<User> administrators = new ArrayList<>();
    private LocalDateTime created = LocalDateTime.now();
    private Integer primaryImageId;
    private Set<Image> images = new HashSet<>();

    /**
     * Constructor for creating a new Business object.
     *
     * @param name                   Name of the business.
     * @param description            Description of the business.
     * @param address                Address of the business.
     * @param businessType           Type of the business. Valid types defined in BusinessType.
     * @param primaryAdministratorId ID of the User who creates the Business.
     */
    public Business(String name, String description, Address address, String businessType,
                    Integer primaryAdministratorId) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.businessType = businessType;
        this.primaryAdministratorId = primaryAdministratorId;
    }

    @Id // this field (attribute) is the table primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement the ID
    @Column(name = "business_id")
    public Integer getId() {
        return this.id;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id")
    public Address getAddress() {
        return this.address;
    }

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_administers_business",
            joinColumns = @JoinColumn(name = "business_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnoreProperties("businessesAdministered")
    public List<User> getAdministrators() {
        return this.administrators;
    }

    @OneToMany(targetEntity=Image.class, fetch= FetchType.EAGER)
    public Set<Image> getImages() {
        return this.images;
    }


    /**
     * Adds a User to the list of administrators of a business.
     *
     * @param admin User to add to list of administrators.
     */
    public void addAdministrator(User admin) {
        if (userIsAdmin(admin.getId())) {
            return;
        }
        this.administrators.add(admin);
    }

    /**
     * Removes a User to the list of administrators of a business.
     *
     * @param admin User to remove from list of administrators.
     */
    public void removeAdministrator(User admin) {
        if (userIsAdmin(admin.getId())) {
            this.administrators.remove(admin);
        } else {
            throw new UserNotAdministratorException(admin.getId(), id);
        }
    }

    /**
     * Checks to see if a User with a specific user ID is already managing a business.
     *
     * @param userId user ID to search for in administrators list.
     * @return true if user already administers business, false otherwise.
     */
    public boolean userIsAdmin(Integer userId) {
        for (User user : this.administrators) {
            if (userId.equals(user.getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if a user can do an admin action for this business.
     *
     * @param user the user trying to do an admin action.
     * @return true if the user is allowed to do the action, false otherwise.
     */
    public boolean userCanDoAction(User user) {
        return user.isGAA() || primaryAdministratorId.equals(user.getId()) || userIsAdmin(user.getId());
    }

    /**
     * Function used to add an image to the list of images associated with a user
     */
    public void addImage(Image newImage){
        this.images.add(newImage);
        //Checks if the new image is the first in the list, and makes it the primary image
        if (images.size() == 1) {
            primaryImageId = newImage.getId();
        }
    }

    /**
     * Function used to remove an image from the list of images associated with a user
     */
    public void removeImage(Image image){ this.images.remove(image); }

}
