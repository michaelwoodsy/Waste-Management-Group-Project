package org.seng302.project.serviceLayer.dto.message;

import lombok.Data;
import org.seng302.project.webLayer.authentication.AppUserDetails;

/**
 * DTO for a request to the
 * DELETE '/users/{userId}/messages/{messageId}'
 * endpoint
 */
@Data
public class DeleteMessageDTO {
    private final Integer userId;
    private final Integer messageId;
    private final AppUserDetails appUser;

    public DeleteMessageDTO(Integer userId, Integer messageId, AppUserDetails appUser) {
        this.userId = userId;
        this.messageId = messageId;
        this.appUser = appUser;
    }
}
