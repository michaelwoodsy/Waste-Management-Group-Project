package org.seng302.project.serviceLayer.dto;

import lombok.Data;
import org.seng302.project.webLayer.authentication.AppUserDetails;

import javax.validation.constraints.NotEmpty;

@Data
public class EditProductDTO {


    @NotEmpty(message = "Product id is a mandatory field")
    private String id; // What productId has possibly been changed to

    @NotEmpty(message = "Product name is a mandatory field")
    private String name;
    private String description;
    private String manufacturer;
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
