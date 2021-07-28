package org.seng302.project.service_layer.dto.user;

import lombok.Data;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AddUserImageDTO {

    private Integer userId;
    private AppUserDetails appUser;
    private MultipartFile imageFile;

    public AddUserImageDTO(Integer userId, AppUserDetails appUser, MultipartFile imageFile) {
        this.userId = userId;
        this.appUser = appUser;
        this.imageFile = imageFile;
    }

}
