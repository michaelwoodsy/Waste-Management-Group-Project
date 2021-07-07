package org.seng302.project.webLayer.controller;

import org.seng302.project.serviceLayer.dto.product.AddProductImageDTO;
import org.seng302.project.serviceLayer.dto.product.AddProductImageResponseDTO;
import org.seng302.project.serviceLayer.dto.product.SetPrimaryProductImageDTO;
import org.seng302.project.serviceLayer.exceptions.business.BusinessNotFoundException;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.ForbiddenAdministratorActionException;
import org.seng302.project.serviceLayer.exceptions.product.ProductImageNotFoundException;
import org.seng302.project.serviceLayer.exceptions.product.ProductNotFoundException;
import org.seng302.project.serviceLayer.service.ProductImageService;
import org.seng302.project.webLayer.authentication.AppUserDetails;
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
    @ResponseStatus(HttpStatus.CREATED)
    public AddProductImageResponseDTO addImage(@PathVariable Integer businessId,
                                               @PathVariable String productId,
                                               @AuthenticationPrincipal AppUserDetails user,
                                               @RequestParam(value = "file") MultipartFile imageFile) {
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
        } catch (BusinessNotFoundException | ForbiddenAdministratorActionException
                | ProductNotFoundException | ProductImageNotFoundException handledException) {
            logger.error(handledException.getMessage());
            throw handledException;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while updating product's primary image: %s", unhandledException.getMessage()));
        }

    }

}
