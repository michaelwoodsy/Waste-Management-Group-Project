package org.seng302.project.serviceLayer.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.seng302.project.repositoryLayer.model.Business;
import org.seng302.project.repositoryLayer.model.Image;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.serviceLayer.dto.address.AddressResponseDTO;
import org.seng302.project.serviceLayer.dto.business.BusinessResponseDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Response DTO for User entities.
 */
@Data
public class GetUserDTO {

    private Integer id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String nickname;
    private String bio;
    private String email;
    private String dateOfBirth;
    private String phoneNumber;
    private AddressResponseDTO homeAddress;
    private String role;
    private List<BusinessResponseDTO> businessesAdministered;
    private LocalDateTime created;
    private Integer primaryImageId;
    private List<Image> images;

    public GetUserDTO(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.middleName = user.getMiddleName();
        this.nickname = user.getNickname();
        this.bio = user.getBio();
        this.email = user.getEmail();
        this.dateOfBirth = user.getDateOfBirth();
        this.phoneNumber = user.getPhoneNumber();
        this.homeAddress = new AddressResponseDTO(user.getHomeAddress());
        this.role = user.getRole();
        this.created = user.getCreated();
        this.businessesAdministered = new ArrayList<>();
        for (Business business : user.getBusinessesAdministered()) {
            this.businessesAdministered.add(new BusinessResponseDTO(business));
        }
        this.primaryImageId = user.getPrimaryImageId();
        this.images = new ArrayList<>(user.getImages());
    }

    @JsonIgnoreProperties("administrators") // Stops infinite nesting when used in BusinessResponseDTO
    public List<BusinessResponseDTO> getBusinessesAdministered() {
        return this.businessesAdministered;
    }

}
