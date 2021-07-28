package org.seng302.project.service_layer.dto.product;

import lombok.Data;
import org.seng302.project.web_layer.authentication.AppUserDetails;

@Data
public class SetPrimaryProductImageDTO {

    private final Integer businessId;
    private final String productId;
    private final Integer imageId;
    private final AppUserDetails appUser;

    public SetPrimaryProductImageDTO(Integer businessId, String productId,
                                     Integer imageId, AppUserDetails appUser) {
        this.businessId = businessId;
        this.productId = productId;
        this.imageId = imageId;
        this.appUser = appUser;
    }

}
