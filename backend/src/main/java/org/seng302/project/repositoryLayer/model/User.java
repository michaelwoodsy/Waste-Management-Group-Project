package org.seng302.project.repositoryLayer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.seng302.project.serviceLayer.dto.user.PostUserDTO;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * User class for storing individual user accounts.
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class User {

    private Integer id; // automatically generated and assigned by the server
    private String firstName;
    private String lastName;
    private String middleName;
    private String nickname;
    private String bio;
    private String email;
    private String dateOfBirth;
    private String phoneNumber;
    private Address homeAddress;
    private String password;
    // One of [ user, globalApplicationAdmin, defaultGlobalApplicationAdmin ]
    private String role; // This property should only be shown to Global org.seng302.project.controller.Application Admins
    private List<Business> businessesAdministered = new ArrayList<>();
    private LocalDateTime created = LocalDateTime.now();
    private Integer primaryImageId;
    private Set<Image> images = new HashSet<>();

    public User(String firstName, String lastName, String middleName,
                String nickname, String bio, String email, String dateOfBirth,
                String phoneNumber, Address homeAddress, String password) {
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

    public User(PostUserDTO dto, Address address) {
        this.firstName = dto.getFirstName();
        this.lastName = dto.getLastName();
        this.middleName = dto.getMiddleName();
        this.nickname = dto.getNickname();
        this.bio = dto.getBio();
        this.email = dto.getEmail();
        this.dateOfBirth = dto.getDateOfBirth();
        this.phoneNumber = dto.getPhoneNumber();
        this.homeAddress = address;
        this.password = dto.getPassword();
        this.role = "user";
    }

    @Id // this field (attribute) is the primary key of the table
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement the ID
    @Column(name = "user_id")
    public Integer getId() {
        return this.id;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String getPassword() {
        return this.password;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id")
    public Address getHomeAddress() {
        return this.homeAddress;
    }

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_administers_business",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "business_id")
    )
    @JsonIgnoreProperties("administrators")
    public List<Business> getBusinessesAdministered() {
        return this.businessesAdministered;
    }

    @OneToMany(targetEntity=Image.class, fetch= FetchType.EAGER)
    public Set<Image> getImages() {
        return this.images;
    }


    public boolean businessIsAdministered(Integer businessId) {
        for (Business business : this.businessesAdministered) {
            if (businessId.equals(business.getId())) {
                return true;
            }
        }
        return false;
    }

    @Transient
    public boolean isGAA() {
        return role.equals("globalApplicationAdmin") || role.equals("defaultGlobalApplicationAdmin");
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