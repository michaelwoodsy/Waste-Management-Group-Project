package org.seng302.project.service_layer.dto.notifications;

import lombok.Data;
import org.seng302.project.web_layer.authentication.AppUserDetails;

/**
 * DTO for a request to the
 * DELETE '/businesses/{businessId}/products/{productId}/images/{imageId}'
 * endpoint
 */
@Data
public class DeleteUserNotificationDTO {

    private final Integer userId;
    private final Integer notificationId;
    private final AppUserDetails appUser;

    public DeleteUserNotificationDTO(Integer userId, Integer notificationId, AppUserDetails appUser) {
        this.userId = userId;
        this.notificationId = notificationId;
        this.appUser = appUser;
    }

}
