package org.seng302.project.repositoryLayer.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * InventoryItem class for storing inventory items:
 * "Entity representing an instance of a stock of some product"
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class InventoryItem {

    private Integer id;
    private Product product;
    private Integer quantity;
    private Double pricePerItem;
    private Double totalPrice;

    //dates
    private String manufactured;
    private String sellBy;
    private String bestBefore;
    private String expires; //required


    public InventoryItem(Product product, Integer quantity,
                         Double pricePerItem, Double totalPrice, String manufactured,
                         String sellBy, String bestBefore, String expires) {
        this.product = product;
        this.quantity = quantity;
        this.pricePerItem = pricePerItem;
        this.totalPrice = totalPrice;
        this.manufactured = manufactured;
        this.sellBy = sellBy;
        this.bestBefore = bestBefore;
        this.expires = expires;
    }


    @Id // this field (attribute) is the primary key of the table
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement the ID
    @Column(name = "inventory_item_id")
    public Integer getId() {
        return this.id;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    @JoinColumn(name = "business_id", referencedColumnName = "business_id")
    public Product getProduct() {
        return this.product;
    }
}
