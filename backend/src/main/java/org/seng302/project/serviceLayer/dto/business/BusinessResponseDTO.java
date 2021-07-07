package org.seng302.project.serviceLayer.dto.business;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.seng302.project.repositoryLayer.model.Address;
import org.seng302.project.repositoryLayer.model.Business;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.serviceLayer.dto.address.AddressResponseDTO;
import org.seng302.project.serviceLayer.dto.user.UserResponseDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Response DTO for business entities.
 */
@Data
public class BusinessResponseDTO {

    private Integer id;
    private String name;
    private String description;
    private AddressResponseDTO address;
    private String businessType;
    private Integer primaryAdministratorId;
    private List<UserResponseDTO> administrators;
    private LocalDateTime created = LocalDateTime.now();

    public BusinessResponseDTO(Business business) {
        this.id = business.getId();
        this.name = business.getName();
        this.description = business.getDescription();
        this.address = new AddressResponseDTO(business.getAddress());
        this.businessType = business.getBusinessType();
        this.primaryAdministratorId = business.getPrimaryAdministratorId();
        this.administrators = new ArrayList<>();
        for (User user : business.getAdministrators()) {
            this.administrators.add(new UserResponseDTO(user));
        }
    }

    @JsonIgnoreProperties("businessesAdministered") // Stops infinite nesting when used in UserResponseDTO
    public List<UserResponseDTO> getAdministrators() {
        return this.administrators;
    }

}
