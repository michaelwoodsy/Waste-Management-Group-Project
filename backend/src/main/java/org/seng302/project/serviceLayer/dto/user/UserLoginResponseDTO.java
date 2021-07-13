package org.seng302.project.serviceLayer.dto.user;

import lombok.Data;

/**
 * Response DTO for User entities.
 */
@Data
public class UserLoginResponseDTO {

    private Integer userId;


    public UserLoginResponseDTO(Integer userId) {
        this.userId = userId;
    }
}
