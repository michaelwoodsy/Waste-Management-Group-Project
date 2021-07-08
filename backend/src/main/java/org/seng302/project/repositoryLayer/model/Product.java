package org.seng302.project.repositoryLayer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Product class for storing individual products.
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
@IdClass(ProductId.class) // class for the id of the product
public class Product {

    @Id
    private String id; // Chosen by the business
    private String name;
    private String description;
    private String manufacturer;
    private Double recommendedRetailPrice;
    private LocalDateTime created = LocalDateTime.now();
    private Integer primaryImageId;

    @OneToMany(targetEntity=Image.class, fetch= FetchType.EAGER)
    private List<Image> images = new ArrayList<>();
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Id
    private Integer businessId; // The id of the business that offers this product

    public Product(String id, String name, String description, String manufacturer, Double recommendedRetailPrice, Integer businessId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.manufacturer = manufacturer;
        this.recommendedRetailPrice = recommendedRetailPrice;
        this.businessId = businessId;
    }

    /**
     * Function used to add an image to the list of images associated with a product
     */
    public void addImage(Image newImage){
        this.images.add(newImage);
    }

    /**
     * Function used to remove an image from the list of images associated with a product
     */
    public void removeImage(Image image){ this.images.remove(image); }

}
