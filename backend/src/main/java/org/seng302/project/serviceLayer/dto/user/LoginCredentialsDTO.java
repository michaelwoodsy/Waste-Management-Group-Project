package org.seng302.project.serviceLayer.dto.user;

import lombok.Data;

/**
 * Response DTO for User entities.
 */
@Data
public class LoginCredentialsDTO {

    private String email;
    private String password;

    public LoginCredentialsDTO(String username, String password) {
        this.email = username;
        this.password = password;
    }
}
