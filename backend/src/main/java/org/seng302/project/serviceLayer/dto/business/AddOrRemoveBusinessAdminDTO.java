package org.seng302.project.serviceLayer.dto.business;

import lombok.Data;
import org.seng302.project.webLayer.authentication.AppUserDetails;

/**
 * DTO for a request to add an admin to a business
 */
@Data
public class AddOrRemoveBusinessAdminDTO {

    private Integer businessId;
    private Integer userId;

    private AppUserDetails appUser;

    public AddOrRemoveBusinessAdminDTO(Integer userId) {
        this.userId = userId;
    }

}
