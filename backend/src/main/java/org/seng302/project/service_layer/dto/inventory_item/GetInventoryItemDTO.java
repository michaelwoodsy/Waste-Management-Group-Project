package org.seng302.project.service_layer.dto.inventory_item;

import lombok.Data;
import org.seng302.project.repository_layer.model.InventoryItem;
import org.seng302.project.service_layer.dto.product.GetProductDTO;

/**
 * Class used when returning inventory items to the frontend
 */
@Data
public class GetInventoryItemDTO {

    private Integer id;
    private GetProductDTO product;
    private Integer quantity;
    private Double pricePerItem;
    private Double totalPrice;
    private String manufactured;
    private String sellBy;
    private String bestBefore;
    private String expires;

    public GetInventoryItemDTO(InventoryItem inventoryItem) {
        this.id = inventoryItem.getId();
        this.product = new GetProductDTO(inventoryItem.getProduct());
        this.quantity = inventoryItem.getQuantity();
        this.pricePerItem = inventoryItem.getPricePerItem();
        this.totalPrice = inventoryItem.getTotalPrice();
        this.manufactured = inventoryItem.getManufactured();
        this.sellBy = inventoryItem.getSellBy();
        this.bestBefore = inventoryItem.getBestBefore();
        this.expires = inventoryItem.getExpires();
    }

}
