package org.seng302.project.service_layer.dto.user;

import lombok.Data;
import org.seng302.project.web_layer.authentication.AppUserDetails;

/**
 * DTO for a request to the
 * DELETE 'users/{userId}/images/{imageId}'
 * endpoint
 */
@Data
public class DeleteUserImageDTO {
    private final Integer userId;
    private final Integer imageId;
    private final AppUserDetails appUser;

    public DeleteUserImageDTO(Integer userId, Integer imageId, AppUserDetails appUser) {
        this.userId = userId;
        this.imageId = imageId;
        this.appUser = appUser;
    }
}
