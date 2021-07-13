package org.seng302.project.serviceLayer.dto.product;

import lombok.Data;
import org.seng302.project.repositoryLayer.model.Image;
import org.seng302.project.repositoryLayer.model.Product;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for sending back products
 */
@Data
public class GetProductDTO {

    private Integer businessId; // The id of the business that offers this product
    private String id; // Chosen by the business
    private String name;
    private String description;
    private String manufacturer;
    private Double recommendedRetailPrice;
    private LocalDateTime created;
    private Integer primaryImageId;
    private List<Image> images;


    public GetProductDTO(Product product) {
        this.businessId = product.getBusinessId();
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.manufacturer = product.getManufacturer();
        this.recommendedRetailPrice = product.getRecommendedRetailPrice();
        this.created = product.getCreated();
        this.primaryImageId = product.getPrimaryImageId();
        this.images = product.getImages();

    }

}
