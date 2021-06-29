package org.seng302.project.serviceLayer.dto.business;

import lombok.Data;
import org.seng302.project.repositoryLayer.model.Address;
import org.seng302.project.serviceLayer.dto.validators.ValidAddress;
import org.seng302.project.serviceLayer.dto.validators.ValidBusinessType;

import javax.validation.constraints.NotEmpty;

/**
 * DTO for a request to add a new business
 */
@Data
public class AddBusinessDTO {

    @NotEmpty(message = "Business name is a mandatory field")
    private String name;
    private String description;
    @ValidAddress
    private Address address;

    @NotEmpty(message = "Business type is a mandatory field")
    @ValidBusinessType
    private String businessType;
    private Integer primaryAdministratorId;

    public AddBusinessDTO(String name, String description, Address address, String businessType,
                          Integer primaryAdministratorId) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.businessType = businessType;
        this.primaryAdministratorId = primaryAdministratorId;
    }
}
