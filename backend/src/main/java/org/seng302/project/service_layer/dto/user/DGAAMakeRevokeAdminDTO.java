package org.seng302.project.service_layer.dto.user;

import lombok.Data;
import org.seng302.project.web_layer.authentication.AppUserDetails;

/**
 * Response DTO for User entities.
 */
@Data
public class DGAAMakeRevokeAdminDTO {

    private Integer id;
    private AppUserDetails appUser;

    public DGAAMakeRevokeAdminDTO(Integer id, AppUserDetails appUser) {
        this.id = id;
        this.appUser = appUser;
    }
}
