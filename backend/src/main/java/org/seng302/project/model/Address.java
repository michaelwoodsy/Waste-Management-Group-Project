package org.seng302.project.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Address class for storing data about a specific address.
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class Address {

    private Integer id; // automatically generated and assigned by database connection.
    private String streetNumber;
    private String streetName;
    private String city;
    private String region;
    private String country;
    private String postcode;

    /**
     * Constructor for creating a new Address object.
     *
     * @param streetNumber  The Address' street number
     * @param streetName    The Address' street name
     * @param city          The Address' city
     * @param region        The Address' region
     * @param country       The Address' country
     * @param postcode      The Address' postcode
     */
    public Address(String streetNumber, String streetName, String city, String region,
                    String country, String postcode) {
        this.streetNumber = streetNumber;
        this.streetName = streetName;
        this.city = city;
        this.region = region;
        this.country = country;
        this.postcode = postcode;
    }

    @Id // this field (attribute) is the table primary key
    @GeneratedValue // autoincrement the ID
    @Column(name = "address_id")
    public Integer getId() {
        return this.id;
    }
}
