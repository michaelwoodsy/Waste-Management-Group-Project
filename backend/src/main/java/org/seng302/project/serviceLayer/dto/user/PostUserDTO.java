package org.seng302.project.serviceLayer.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.seng302.project.repositoryLayer.model.Address;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.serviceLayer.dto.validators.ValidAddress;
import org.seng302.project.serviceLayer.dto.validators.registering.ValidDateOfBirth;
import org.seng302.project.serviceLayer.dto.validators.registering.ValidEmail;
import org.seng302.project.serviceLayer.dto.validators.registering.ValidPassword;
import org.seng302.project.serviceLayer.dto.validators.registering.ValidPhone;

import javax.validation.constraints.NotEmpty;

/**
 * Response DTO for User entities.
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
    private Address homeAddress;

    @NotEmpty(message = "MissingData: Password is a mandatory field")
    @ValidPassword
    private String password;
}
