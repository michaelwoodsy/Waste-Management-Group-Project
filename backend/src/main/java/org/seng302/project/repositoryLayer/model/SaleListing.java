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
@IdClass(SaleListingId.class)
public class SaleListing {

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Id
    private Integer businessId; // The id of the business that offers this product
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "listing_id")
    private Integer id;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "inventory_item_id")
    private InventoryItem inventoryItem;
    private Double price;
    private String moreInfo;
    private LocalDateTime closes;
    private LocalDateTime created = LocalDateTime.now();
    private Integer quantity;

    public SaleListing(Integer businessId, InventoryItem inventoryItem, Double price, String moreInfo,
                       LocalDateTime closes, Integer quantity) {
        this.businessId = businessId;
        this.inventoryItem = inventoryItem;
        this.price = price;
        this.moreInfo = moreInfo;
        this.closes = closes;
        this.quantity = quantity;
    }
}
