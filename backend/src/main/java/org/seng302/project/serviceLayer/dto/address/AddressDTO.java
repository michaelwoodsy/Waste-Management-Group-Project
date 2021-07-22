package org.seng302.project.serviceLayer.dto.address;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.seng302.project.repositoryLayer.model.Address;

/**
 * Response DTO for Address entities
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {

    private String streetNumber;
    private String streetName;
    private String city;
    private String region;
    private String country;
    private String postcode;

    public AddressDTO(Address address) {
        this.streetNumber = address.getStreetNumber();
        this.streetName = address.getStreetName();
        this.city = address.getCity();
        this.region = address.getRegion();
        this.country = address.getCountry();
        this.postcode = address.getPostcode();
    }

}
