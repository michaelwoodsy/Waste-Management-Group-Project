package org.seng302.project.service_layer.dto.product;

import lombok.Data;
import org.seng302.project.web_layer.authentication.AppUserDetails;

/**
 * DTO for a request to the
 * DELETE '/businesses/{businessId}/products/{productId}/images/{imageId}'
 * endpoint
 */
@Data
public class DeleteProductImageDTO {

    private final Integer businessId;
    private final String productId;
    private final Integer imageId;
    private final AppUserDetails appUser;

    public DeleteProductImageDTO(Integer businessId, String productId,
                                     Integer imageId, AppUserDetails appUser) {
        this.businessId = businessId;
        this.productId = productId;
        this.imageId = imageId;
        this.appUser = appUser;
    }

}
