package org.seng302.project.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * InventoryItem class for storing inventory items:
 * "Entity representing an instance of a stock of some product"
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class InventoryItem {

    private Integer id;
    private String product; //TODO: this will be a Product object
    private Integer productId; //TODO: reference to Product object when Product object exists in same branch
    private Integer quantity;
    private Double pricePerItem;
    private Double totalPrice;

    //dates
    private String manufactured;
    private String sellBy;
    private String bestBefore;
    private String expires; //required


    public InventoryItem(String product, Integer productId, Integer quantity,
                         Double pricePerItem, Double totalPrice, String manufactured,
                         String sellBy, String bestBefore, String expires) {
        this.product = product;
        this.productId = productId;
        this.quantity = quantity;
        this.pricePerItem = pricePerItem;
        this.totalPrice = totalPrice;
        this.manufactured = manufactured;
        this.sellBy = sellBy;
        this.bestBefore = bestBefore;
        this.expires = expires;
    }


    @Id // this field (attribute) is the primary key of the table
    @GeneratedValue // autoincrement the ID
    @Column(name = "inventory_item_id")
    public Integer getId() {
        return this.id;
    }
}
