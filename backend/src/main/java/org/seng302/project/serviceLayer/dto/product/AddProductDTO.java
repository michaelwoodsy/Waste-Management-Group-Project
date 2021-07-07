package org.seng302.project.serviceLayer.dto.product;

import lombok.Data;
import org.seng302.project.serviceLayer.dto.validators.ValidPrice;
import org.seng302.project.serviceLayer.dto.validators.ValidProductId;
import org.seng302.project.webLayer.authentication.AppUserDetails;


import javax.validation.constraints.NotEmpty;

/**
 * DTO for a request to add a new product
 */
@Data
public class AddProductDTO {

    @NotEmpty(message = "Product id is a mandatory field")
    @ValidProductId
    private String id; // Chosen by the business

    @NotEmpty(message = "Product name is a mandatory field")
    private String name;
    private String description;
    private String manufacturer;

    @ValidPrice
    private Double recommendedRetailPrice;

    private Integer businessId;
    private AppUserDetails appUser;

    public AddProductDTO(String id, String name, String description, String manufacturer,
                         Double recommendedRetailPrice) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.manufacturer = manufacturer;
        this.recommendedRetailPrice = recommendedRetailPrice;
    }
}
