package org.seng302.project.repositoryLayer.model;

import lombok.Data;

/**
 * Object containing a username and password used for authentication.
 */
@Data
public class LoginCredentials {

    private String email;
    private String password;

    public LoginCredentials(String username, String password) {
        this.email = username;
        this.password = password;
    }

}
