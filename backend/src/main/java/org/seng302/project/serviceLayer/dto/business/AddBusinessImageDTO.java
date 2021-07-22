package org.seng302.project.serviceLayer.dto.business;

import lombok.Data;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AddBusinessImageDTO {

    private Integer businessId;
    private AppUserDetails appUser;
    private MultipartFile imageFile;

    public AddBusinessImageDTO(Integer businessId, AppUserDetails appUser, MultipartFile imageFile) {
        this.businessId = businessId;
        this.appUser = appUser;
        this.imageFile = imageFile;
    }
}
