package org.seng302.project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.time.LocalDateTime;

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
    private String images; //TODO: change this to a list of image objects
    @JsonIgnore
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
}
