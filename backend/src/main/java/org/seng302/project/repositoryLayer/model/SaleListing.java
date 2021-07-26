package org.seng302.project.repositoryLayer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Class to model a sale listing.
 */
@Data
@NoArgsConstructor
@Entity
public class SaleListing {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "listing_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "business_id")
    private Business business;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "inventory_item_id")
    private InventoryItem inventoryItem;
    private Double price;
    private String moreInfo;
    private LocalDateTime closes;
    private LocalDateTime created = LocalDateTime.now();
    private Integer quantity;

    public SaleListing(Business business, InventoryItem inventoryItem, Double price, String moreInfo,
                       LocalDateTime closes, Integer quantity) {
        this.business = business;
        this.inventoryItem = inventoryItem;
        this.price = price;
        this.moreInfo = moreInfo;
        this.closes = closes;
        this.quantity = quantity;
    }
}
