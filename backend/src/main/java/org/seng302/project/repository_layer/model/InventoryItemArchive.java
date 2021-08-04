package org.seng302.project.repository_layer.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

/**
 * An archive of an InventoryItem used in the sale history.
 */
@Embeddable
@Data
@NoArgsConstructor
public class InventoryItemArchive {

    private Integer inventoryItemId;
    @Embedded
    private ProductArchive product;
    private Double pricePerItem;
    private String manufactured;
    private String sellBy;
    private String bestBefore;
    private String expires;

    public InventoryItemArchive(InventoryItem item) {
        this.inventoryItemId = item.getId();
        this.product = new ProductArchive(item.getProduct());
        this.pricePerItem = item.getPricePerItem();
        this.manufactured = item.getManufactured();
        this.sellBy = item.getSellBy();
        this.expires = item.getExpires();
        this.bestBefore = item.getBestBefore();
    }
}
