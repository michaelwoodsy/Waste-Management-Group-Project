package org.seng302.project.serviceLayer.dto.product;

import lombok.Data;
import org.seng302.project.webLayer.authentication.AppUserDetails;

@Data
public class DeleteProductImageDTO {

    private final Integer businessId;
    private final String productId;
    private final Integer imageId;
    private final AppUserDetails appUser;

    public DeleteProductImageDTO(Integer businessId, String productId,
                                     Integer imageId, AppUserDetails appUser) {
        this.businessId = businessId;
        this.productId = productId;
        this.imageId = imageId;
        this.appUser = appUser;
    }

}
