package org.seng302.project.repository_layer.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity for a sale listing that has been purchased.
 */
@Data
@NoArgsConstructor
@Entity
public class Sale {

    @Id
    private Integer id;  // Same as the sale listing id that was sold
    @ManyToOne
    @JoinColumn(name = "business_id")
    private Business business;
    @Embedded
    private InventoryItemArchive inventoryItem;
    private Double price;
    private String moreInfo;
    private LocalDateTime closes;
    private LocalDateTime dateSold = LocalDateTime.now();
    private LocalDateTime created;
    private Integer quantity;

    public Sale(SaleListing saleListing) {
        this.id = saleListing.getId();
        this.business = saleListing.getBusiness();
        this.inventoryItem = new InventoryItemArchive(saleListing.getInventoryItem());
        this.price = saleListing.getPrice();
        this.moreInfo = saleListing.getMoreInfo();
        this.closes = saleListing.getCloses();
        this.created = saleListing.getCreated();
        this.quantity = saleListing.getQuantity();
    }

}
