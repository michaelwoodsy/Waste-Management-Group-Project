package org.seng302.project.serviceLayer.dto.business;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.seng302.project.serviceLayer.dto.address.AddressDTO;
import org.seng302.project.serviceLayer.dto.validators.ValidAddress;
import org.seng302.project.serviceLayer.dto.validators.ValidBusinessType;

import javax.validation.constraints.NotEmpty;

/**
 * DTO for a request to add a new business
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostBusinessDTO {

    @NotEmpty(message = "Business name is a mandatory field")
    private String name;
    private String description;
    @ValidAddress
    private AddressDTO address;
    @NotEmpty(message = "Business type is a mandatory field")
    @ValidBusinessType
    private String businessType;
    private Integer primaryAdministratorId;

}
