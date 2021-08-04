package org.seng302.project.repository_layer.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


/**
 * An archive of a Product, used embedded in InventoryItemArchive to preserve the sale history.
 */
@Embeddable
@Data
@NoArgsConstructor
public class ProductArchive {

    private String productId;
    private String name;
    private String description;
    private String manufacturer;
    private Double recommendedRetailPrice;
    private String currencyCountry;

    public ProductArchive(Product product) {
        this.productId = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.manufacturer = product.getManufacturer();
        this.recommendedRetailPrice = product.getRecommendedRetailPrice();
        this.currencyCountry = product.getCurrencyCountry();
    }

}
