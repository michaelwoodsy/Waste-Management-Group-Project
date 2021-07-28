package org.seng302.project.service_layer.dto.product;

import lombok.Data;
import org.seng302.project.service_layer.dto.validators.ValidPrice;
import org.seng302.project.service_layer.dto.validators.ValidProductId;
import org.seng302.project.web_layer.authentication.AppUserDetails;

import javax.validation.constraints.NotEmpty;

/**
 * DTO for a request to edit a product
 */
@Data
public class EditProductDTO {


    @NotEmpty(message = "Product id is a mandatory field")
    @ValidProductId
    private String id; // What productId has possibly been changed to

    @NotEmpty(message = "Product name is a mandatory field")
    private String name;
    private String description;
    private String manufacturer;

    @ValidPrice
    private Double recommendedRetailPrice;

    private Integer businessId;
    private String productId;
    private AppUserDetails appUser;

    public EditProductDTO(String id, String name, String description, String manufacturer, Double recommendedRetailPrice) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.manufacturer = manufacturer;
        this.recommendedRetailPrice = recommendedRetailPrice;
    }
}
