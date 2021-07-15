package org.seng302.project.serviceLayer.dto.business;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.seng302.project.serviceLayer.dto.address.AddressDTO;
import org.seng302.project.serviceLayer.dto.validators.ValidAddress;
import org.seng302.project.serviceLayer.dto.validators.ValidBusinessType;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PutBusinessDTO {

    @NotEmpty(message = "Business name is a mandatory field")
    public String name;
    public String description;
    @ValidAddress
    public AddressDTO address;
    @NotEmpty(message = "Business type is a mandatory field")
    @ValidBusinessType
    public String businessType;
    public Integer primaryAdministratorId;

}
