package org.seng302.project.serviceLayer.dto.business;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.seng302.project.repositoryLayer.model.Business;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.serviceLayer.dto.address.AddressResponseDTO;
import org.seng302.project.serviceLayer.dto.user.GetUserDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
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
    private List<GetUserDTO> administrators;
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
            //Removes infinite loop of businesses administers causing StackOverflowError
            user.setBusinessesAdministered(Collections.emptyList());
            this.administrators.add(new GetUserDTO(user));
        }
    }

    @JsonIgnoreProperties("businessesAdministered") // Stops infinite nesting when used in GetUserDTO
    public List<GetUserDTO> getAdministrators() {
        return this.administrators;
    }

}
