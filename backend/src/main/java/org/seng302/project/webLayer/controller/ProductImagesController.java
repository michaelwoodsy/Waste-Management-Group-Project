package org.seng302.project.webLayer.controller;


import org.seng302.project.serviceLayer.dto.SetPrimaryProductImageDTO;
import org.seng302.project.serviceLayer.exceptions.NoBusinessExistsException;
import org.seng302.project.serviceLayer.exceptions.NoProductExistsException;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.ForbiddenAdministratorActionException;
import org.seng302.project.serviceLayer.exceptions.productImages.NoProductImageWithIdException;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling requests to do with product images.
 */
@RestController
public class ProductImagesController {

    private static final Logger logger = LoggerFactory.getLogger(ProductImagesController.class.getName());

    /**
     * Handles request to set a primary image for a product.
     */
    @PutMapping("/businesses/{businessId}/products/{productId}/images/{imageId}/makeprimary")
    @ResponseStatus(HttpStatus.OK)
    public void setPrimaryImage(@PathVariable int businessId, @PathVariable String productId, @PathVariable int imageId,
                                @AuthenticationPrincipal AppUserDetails appUser) {

        try {
            var requestDTO = new SetPrimaryProductImageDTO(businessId, productId, imageId, appUser);
            requestDTO.executeRequest();
        } catch (NoBusinessExistsException | ForbiddenAdministratorActionException
                | NoProductExistsException | NoProductImageWithIdException handledException) {
            logger.error(handledException.getMessage());
            throw handledException;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while updating product's primary image: %s", unhandledException.getMessage()));
        }

    }
}
