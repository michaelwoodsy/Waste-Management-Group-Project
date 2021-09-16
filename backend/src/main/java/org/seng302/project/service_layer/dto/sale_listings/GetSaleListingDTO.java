package org.seng302.project.service_layer.dto.sale_listings;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.seng302.project.repository_layer.model.*;
import org.seng302.project.service_layer.dto.business.GetBusinessDTO;

import java.time.LocalDateTime;

/**
 * Response DTO for SaleListing entities.
 */
@Data
@NoArgsConstructor
public class GetSaleListingDTO {

    private Integer id;
    private GetBusinessDTO business;
    private InventoryItem inventoryItem;
    private Double price;
    private String moreInfo;
    private LocalDateTime closes;
    private LocalDateTime created;
    private Integer quantity;
    private Integer likes;
    private boolean userLikes;

    public GetSaleListingDTO(SaleListing listing) {
        this.id = listing.getId();
        this.business = new GetBusinessDTO(listing.getBusiness());
        this.business.attachAdministrators(listing.getBusiness());
        this.inventoryItem = listing.getInventoryItem();
        this.price = listing.getPrice();
        this.moreInfo = listing.getMoreInfo();
        this.closes = listing.getCloses();
        this.created = listing.getCreated();
        this.quantity = listing.getQuantity();
    }

    /**
     * Method that attaches a listings' like data to the DTO
     *
     * @param likes number of likes the sale listing has
     * @param userLikes whether the logged-in user likes the sale listing or not
     */
    public void attachLikeData(Integer likes, boolean userLikes) {
        this.likes = likes;
        this.userLikes = userLikes;
    }

}
