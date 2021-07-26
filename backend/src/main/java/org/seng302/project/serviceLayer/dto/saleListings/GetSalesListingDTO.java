package org.seng302.project.serviceLayer.dto.saleListings;

import lombok.Data;
import org.seng302.project.repositoryLayer.model.*;

import java.time.LocalDateTime;

/**
 * Response DTO for business entities.
 */
@Data
public class GetSalesListingDTO {

    private Integer id;
    private Integer businessId;
    private InventoryItem inventoryItem;
    private Double price;
    private String moreInfo;
    private LocalDateTime closes;
    private LocalDateTime created;
    private Integer quantity;

    public GetSalesListingDTO(SaleListing listing) {
        this.id = listing.getId();
        this.businessId = listing.getBusiness().getId();
        this.inventoryItem = listing.getInventoryItem();
        this.price = listing.getPrice();
        this.moreInfo = listing.getMoreInfo();
        this.closes = listing.getCloses();
        this.created = listing.getCreated();
        this.quantity = listing.getQuantity();
    }

}
