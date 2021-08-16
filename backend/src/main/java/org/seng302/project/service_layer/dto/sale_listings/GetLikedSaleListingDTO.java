package org.seng302.project.service_layer.dto.sale_listings;

import lombok.Data;
import org.seng302.project.repository_layer.model.LikedSaleListing;
import org.seng302.project.service_layer.dto.user.GetUserDTO;

/**
 * Response DTO for LikedSaleListing entities.
 */
@Data
public class GetLikedSaleListingDTO {

    private Integer id;
    private GetUserDTO user;
    private GetSaleListingDTO listing;
    private boolean starred;
    private String tag;

    public GetLikedSaleListingDTO(LikedSaleListing listing) {
        this.id = listing.getId();
        this.user = new GetUserDTO(listing.getUser());
        this.listing = new GetSaleListingDTO(listing.getListing());//Set to null as we cant get that here
        this.starred = listing.isStarred();
        this.tag = listing.getTag().name();
        this.listing.setUserStarred(this.starred);
    }

}
