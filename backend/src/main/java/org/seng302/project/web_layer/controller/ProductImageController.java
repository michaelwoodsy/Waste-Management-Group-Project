package org.seng302.project.web_layer.controller;

import org.seng302.project.service_layer.dto.AddProductImageDTO;
import org.seng302.project.service_layer.dto.AddProductImageResponseDTO;
import org.seng302.project.service_layer.dto.SetPrimaryProductImageDTO;
import org.seng302.project.service_layer.exceptions.NoBusinessExistsException;
import org.seng302.project.service_layer.exceptions.businessAdministrator.ForbiddenAdministratorActionException;
import org.seng302.project.service_layer.exceptions.productImages.NoProductImageWithIdException;
import org.seng302.project.service_layer.exceptions.productImages.ProductNotFoundException;
import org.seng302.project.service_layer.service.ProductImageService;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ProductImageController {

    private static final Logger logger = LoggerFactory.getLogger(ProductImageController.class.getName());

    private final ProductImageService productImageService;

    @Autowired
    public ProductImageController(ProductImageService productImageService) {
        this.productImageService = productImageService;
    }

    @PostMapping("/businesses/{businessId}/products/{productId}/images")
    public AddProductImageResponseDTO addImage(@PathVariable Integer businessId,
                                               @PathVariable String productId,
                                               @AuthenticationPrincipal AppUserDetails user,
                                               @RequestParam("file") MultipartFile imageFile) {
        var requestDTO = new AddProductImageDTO(businessId, productId, user, imageFile);
        return productImageService.addProductImage(requestDTO);
    }

    /**
     * Handles request to set a primary image for a product.
     */
    @PutMapping("/businesses/{businessId}/products/{productId}/images/{imageId}/makeprimary")
    @ResponseStatus(HttpStatus.OK)
    public void setPrimaryImage(@PathVariable int businessId, @PathVariable String productId,
                                @PathVariable int imageId, @AuthenticationPrincipal AppUserDetails appUser) {

        try {
            var requestDTO = new SetPrimaryProductImageDTO(businessId, productId, imageId, appUser);
            productImageService.setPrimaryImage(requestDTO);
        } catch (NoBusinessExistsException | ForbiddenAdministratorActionException
                | ProductNotFoundException | NoProductImageWithIdException handledException) {
            logger.error(handledException.getMessage());
            throw handledException;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while updating product's primary image: %s", unhandledException.getMessage()));
        }

    }

}
