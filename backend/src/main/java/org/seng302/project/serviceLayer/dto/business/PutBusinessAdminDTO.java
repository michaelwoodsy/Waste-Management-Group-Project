package org.seng302.project.serviceLayer.dto.business;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.seng302.project.webLayer.authentication.AppUserDetails;

/**
 * DTO for a request to add an admin to a business
 */
@Data
@NoArgsConstructor
public class PutBusinessAdminDTO {

    private Integer userId;
    private Integer businessId;
    private AppUserDetails appUser;

    public PutBusinessAdminDTO(Integer userId) {
        this.userId = userId;
    }

}
