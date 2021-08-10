package org.seng302.project.service_layer.dto.business;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.seng302.project.repository_layer.model.Business;
import org.seng302.project.repository_layer.model.Image;
import org.seng302.project.repository_layer.model.User;
import org.seng302.project.service_layer.dto.address.AddressDTO;
import org.seng302.project.service_layer.dto.user.GetUserDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Response DTO for business entities.
 */
@Data
public class GetBusinessDTO {

    private Integer id;
    private String name;
    private String description;
    private AddressDTO address;
    private String businessType;
    private Integer primaryAdministratorId;
    private List<GetUserDTO> administrators;
    private LocalDateTime created;
    private Integer primaryImageId;
    private List<Image> images;

    public GetBusinessDTO(Business business) {
        this.id = business.getId();
        this.name = business.getName();
        this.description = business.getDescription();
        this.address = new AddressDTO(business.getAddress());
        this.businessType = business.getBusinessType();
        this.primaryAdministratorId = business.getPrimaryAdministratorId();
        this.administrators = new ArrayList<>();
        this.created = business.getCreated();
        this.primaryImageId = business.getPrimaryImageId();
        this.images = business.getImages();
    }

    /**
     * Method which attaches a business' administrators to the DTO
     *
     * @param business Business to get admins from
     */
    public void attachAdministrators(Business business) {
        for (User user : business.getAdministrators()) {
            this.administrators.add(new GetUserDTO(user));
        }
    }

}
