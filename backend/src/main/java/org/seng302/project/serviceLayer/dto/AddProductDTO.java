package org.seng302.project.serviceLayer.dto;

import lombok.Data;
import org.seng302.project.webLayer.authentication.AppUserDetails;

/**
 * DTO for a request to add a new product
 */
@Data
public class AddProductDTO {

    //TODO: validation

    private String id; // Chosen by the business
    private String name;
    private String description;
    private String manufacturer;
    private Double recommendedRetailPrice;

    private Integer businessId;
    private AppUserDetails appUser;

    public AddProductDTO(String id, String name, String description, String manufacturer, Double recommendedRetailPrice) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.manufacturer = manufacturer;
        this.recommendedRetailPrice = recommendedRetailPrice;
    }
}
