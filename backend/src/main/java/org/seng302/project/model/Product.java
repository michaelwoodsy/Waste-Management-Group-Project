package org.seng302.project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * Product class for storing individual products.
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class Product {

    private String id; // Chosen by the business
    private String name;
    private String description;
    private Double recommendedRetailPrice;
    private LocalDateTime created = LocalDateTime.now();
    private String images; //TODO: change this to a list of image objects
    @JsonIgnore
    private Integer businessId; // The id of the business that offers this product


    @Id // this field (attribute) is the primary key of the table
    @Column(name = "product_id")
    public String getId() {
        return this.id;
    }


    public Product(String id, String name, String description, Double recommendedRetailPrice,
                   Integer businessId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.recommendedRetailPrice = recommendedRetailPrice;
        this.businessId = businessId;
    }
}
