package org.seng302.project.serviceLayer.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.seng302.project.serviceLayer.dto.address.AddressDTO;
import org.seng302.project.serviceLayer.dto.validators.ValidAddress;
import org.seng302.project.serviceLayer.dto.validators.user.ValidDateOfBirth;
import org.seng302.project.serviceLayer.dto.validators.user.ValidEditedPassword;
import org.seng302.project.serviceLayer.dto.validators.user.ValidEmail;
import org.seng302.project.serviceLayer.dto.validators.user.ValidPhone;

import javax.validation.constraints.NotEmpty;

/**
 * Response DTO for User entities.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PutUserDTO {

    private Integer id;

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

    //new password can be null as you dont have to change your password.
    // When changing password, the current password also needs to be supplied
    @ValidEditedPassword
    private String newPassword;

    //Required when user is updating their password or email
    private String currentPassword;
}
