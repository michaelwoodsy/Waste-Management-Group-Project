package org.seng302.project.webLayer.controller;

import org.seng302.project.serviceLayer.dto.AddProductImageDTO;
import org.seng302.project.serviceLayer.dto.AddProductImageResponseDTO;
import org.seng302.project.serviceLayer.dto.SetPrimaryProductImageDTO;
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

import java.io.IOException;

@RestController
public class ProductImagesController {

    private static final Logger logger = LoggerFactory.getLogger(ProductImagesController.class.getName());

    private final ProductImageService productImageService;

    @Autowired
    public ProductImagesController(ProductImageService productImageService) {
        this.productImageService = productImageService;
    }

    /**
     * Handles request to add a new image for a product
     * @param businessId ID of the business
     * @param productId ID of the product the image is being added to
     * @param user The user requesting to add the image
     * @param imageFile The image file
     * @return TheResponseDTO if an image is added successfully, containing the image ID
     */
    @PostMapping("/businesses/{businessId}/products/{productId}/images")
    @ResponseStatus(HttpStatus.CREATED)
    public AddProductImageResponseDTO addImage(@PathVariable Integer businessId,
                                               @PathVariable String productId,
                                               @AuthenticationPrincipal AppUserDetails user,
                                               @RequestParam("file") MultipartFile imageFile) throws IOException {
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
