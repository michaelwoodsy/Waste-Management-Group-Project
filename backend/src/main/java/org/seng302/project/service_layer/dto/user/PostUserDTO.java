package org.seng302.project.service_layer.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.seng302.project.service_layer.dto.address.AddressDTO;
import org.seng302.project.service_layer.dto.validators.ValidAddress;
import org.seng302.project.service_layer.dto.validators.user.ValidDateOfBirth;
import org.seng302.project.service_layer.dto.validators.user.ValidEmail;
import org.seng302.project.service_layer.dto.validators.user.ValidPassword;
import org.seng302.project.service_layer.dto.validators.user.ValidPhone;

import javax.validation.constraints.NotEmpty;

/**
 * Request DTO for posting a new user.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostUserDTO {

    @NotEmpty(message = "MissingData: First Name is a mandatory field")
    private String firstName;
    @NotEmpty(message = "MissingData: Last Name is a mandatory field")
    private String lastName;

    private String middleName;

    private String nickname;

    private String bio;

    @NotEmpty(message = "MissingData: Email is a mandatory field")
    @ValidEmail
    private String email;

    @NotEmpty(message = "MissingData: Date of Birth is a mandatory field")
    @ValidDateOfBirth
    private String dateOfBirth;

    @ValidPhone
    private String phoneNumber;

    @ValidAddress
    private AddressDTO homeAddress;

    @NotEmpty(message = "MissingData: Password is a mandatory field")
    @ValidPassword
    private String password;
}
