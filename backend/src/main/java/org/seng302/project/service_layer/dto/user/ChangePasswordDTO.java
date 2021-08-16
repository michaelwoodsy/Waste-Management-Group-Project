package org.seng302.project.service_layer.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.seng302.project.service_layer.dto.validators.user.ValidPassword;

import javax.validation.constraints.NotEmpty;

/**
 * Request DTO changing a users password with lost password token.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordDTO {

    @NotEmpty(message = "MissingData: token is a mandatory field")
    private String token;

    @NotEmpty(message = "MissingData: Password is a mandatory field")
    @ValidPassword
    private String password;
}
