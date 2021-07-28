package org.seng302.project.service_layer.dto.saleListings;

import lombok.Data;
import org.seng302.project.repository_layer.model.*;
import org.seng302.project.service_layer.dto.business.GetBusinessDTO;

import java.time.LocalDateTime;

/**
 * Response DTO for business entities.
 */
@Data
public class GetSalesListingDTO {

    private Integer id;
    private GetBusinessDTO business;
    private InventoryItem inventoryItem;
    private Double price;
    private String moreInfo;
    private LocalDateTime closes;
    private LocalDateTime created;
    private Integer quantity;

    public GetSalesListingDTO(SaleListing listing) {
        this.id = listing.getId();
        this.business = new GetBusinessDTO(listing.getBusiness());
        this.inventoryItem = listing.getInventoryItem();
        this.price = listing.getPrice();
        this.moreInfo = listing.getMoreInfo();
        this.closes = listing.getCloses();
        this.created = listing.getCreated();
        this.quantity = listing.getQuantity();
    }

}
