package org.seng302.project.serviceLayer.dto.business;

import lombok.Data;
import org.seng302.project.webLayer.authentication.AppUserDetails;

/**
 * DTO for a request made to
 * DELETE '/businesses/{businessId}/images/{imageId}'
 * endpoint
 */

@Data
public class DeleteBusinessImageDTO {

    private final Integer businessId;
    private final Integer imageId;
    private final AppUserDetails appUser;

    public DeleteBusinessImageDTO(Integer businessId, Integer imageId, AppUserDetails appUser) {
        this.businessId = businessId;
        this.imageId = imageId;
        this.appUser = appUser;
    }


}
