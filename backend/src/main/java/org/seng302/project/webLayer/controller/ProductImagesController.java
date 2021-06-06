package org.seng302.project.webLayer.controller;


import org.seng302.project.serviceLayer.dto.setPrimaryProductImageDTO;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling requests to do with product images.
 */
@RestController
public class ProductImagesController {

    /**
     * Handles request to set a primary image for a product.
     */
    @PutMapping("/businesses/{businessId}/products/{productId}/images/{imageId}/makeprimary")
    @ResponseStatus(HttpStatus.OK)
    public void setPrimaryImage(@PathVariable int businessId, @PathVariable String productId, @PathVariable int imageId,
                                @AuthenticationPrincipal AppUserDetails appUser) {

        setPrimaryProductImageDTO requestDTO = new setPrimaryProductImageDTO(businessId, productId, imageId, appUser);

        //TODO in DTO:
        //Check if business exists
        //406 response

        //Check if user making request is business admin/gaa/dgaa
        //403 response

        //Check if product exists
        //Check if image exists for product
        //403 response

    }
}
