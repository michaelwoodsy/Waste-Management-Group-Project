package org.seng302.project.service_layer.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.seng302.project.repository_layer.model.Business;
import org.seng302.project.repository_layer.model.Image;
import org.seng302.project.repository_layer.model.User;
import org.seng302.project.service_layer.dto.address.AddressDTO;
import org.seng302.project.service_layer.dto.business.GetBusinessDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private AddressDTO homeAddress;
    private String role;
    private List<GetBusinessDTO> businessesAdministered;
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
        this.homeAddress = new AddressDTO(user.getHomeAddress());
        this.role = user.getRole();
        this.created = user.getCreated();
        this.businessesAdministered = new ArrayList<>();
        for (Business business : user.getBusinessesAdministered()) {
            this.businessesAdministered.add(new GetBusinessDTO(business));
        }
        this.primaryImageId = user.getPrimaryImageId();
        this.images = new ArrayList<>(user.getImages());
    }

    @JsonIgnoreProperties("administrators") // Stops infinite nesting when used in BusinessResponseDTO
    public List<GetBusinessDTO> getBusinessesAdministered() {
        return this.businessesAdministered;
    }

}
