package org.seng302.project.serviceLayer.dto.address;

import lombok.Data;
import org.seng302.project.repositoryLayer.model.Address;

/**
 * Response DTO for Address entities
 */
@Data
public class AddressResponseDTO {

    private Integer id;
    private String streetNumber;
    private String streetName;
    private String city;
    private String region;
    private String country;
    private String postcode;

    public AddressResponseDTO(Address address) {
        this.id = address.getId();
        this.streetNumber = address.getStreetNumber();
        this.streetName = address.getStreetName();
        this.city = address.getCity();
        this.region = address.getRegion();
        this.country = address.getCountry();
        this.postcode = address.getPostcode();
    }

}
