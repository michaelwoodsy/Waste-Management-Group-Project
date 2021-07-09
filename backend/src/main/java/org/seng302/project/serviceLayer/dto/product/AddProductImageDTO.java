package org.seng302.project.serviceLayer.dto.product;

import lombok.Data;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AddProductImageDTO {

    private Integer businessId;
    private String productId;
    private AppUserDetails appUser;
    private MultipartFile imageFile;

    public AddProductImageDTO(Integer businessId, String productId, AppUserDetails appUser, MultipartFile imageFile) {
        this.businessId = businessId;
        this.productId = productId;
        this.appUser = appUser;
        this.imageFile = imageFile;
    }

}
