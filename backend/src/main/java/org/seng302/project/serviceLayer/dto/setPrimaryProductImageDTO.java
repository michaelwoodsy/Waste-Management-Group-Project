package org.seng302.project.serviceLayer.dto;

import org.seng302.project.webLayer.authentication.AppUserDetails;

public class setPrimaryProductImageDTO {

    private Integer businessId;
    private String productId;
    private Integer imageId;
    private AppUserDetails appUser;

    public setPrimaryProductImageDTO(Integer businessId, String productId,
                                     Integer imageId, AppUserDetails appUser) {
        this.businessId = businessId;
        this.productId = productId;
        this.imageId = imageId;
        this.appUser = appUser;
    }


}
