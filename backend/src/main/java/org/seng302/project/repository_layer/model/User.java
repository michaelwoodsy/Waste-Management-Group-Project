package org.seng302.project.repository_layer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.seng302.project.service_layer.dto.user.PostUserDTO;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * User class for storing individual user accounts.
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class User {

    @Id // this field (attribute) is the primary key of the table
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement the ID
    @Column(name = "user_id")
    private Integer id; // automatically generated and assigned by the server

    private String firstName;

    private String lastName;

    private String middleName;

    private String nickname;

    private String bio;

    private String email;

    private String dateOfBirth;

    private String phoneNumber;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id")
    private Address homeAddress;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    // One of [ user, globalApplicationAdmin, defaultGlobalApplicationAdmin ]
    // This property should only be shown to Global org.seng302.project.controller.Application Admins
    private String role;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_administers_business",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "business_id")
    )
    @JsonIgnoreProperties("administrators")
    private List<Business> businessesAdministered = new ArrayList<>();

    private LocalDateTime created = LocalDateTime.now();

    private Integer primaryImageId;

    @OneToMany(targetEntity = Image.class)
    private List<Image> images = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(targetEntity = LikedSaleListing.class)
    private List<LikedSaleListing> likedSaleListings = new ArrayList<>();

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
    public void addImage(Image newImage) {
        this.images.add(newImage);
        //Checks if the new image is the first in the list, and makes it the primary image
        if (images.size() == 1) {
            primaryImageId = newImage.getId();
        }
    }

    /**
     * Function used to remove an image from the list of images associated with a user
     */
    public void removeImage(Image image) {
        this.images.remove(image);
    }

    /**
     * Adds a liked sale listing to the user's list of liked sale listings
     *
     * @param listing liked listing to add
     */
    public void addLikedListing(LikedSaleListing listing) {
        this.likedSaleListings.add(listing);
    }

    /**
     * Removes a liked sale listing from the user's list of liked sale listings
     *
     * @param listing liked listing to remove
     */
    public void removeLikedListing(LikedSaleListing listing) {
        this.likedSaleListings.remove(listing);
    }

}