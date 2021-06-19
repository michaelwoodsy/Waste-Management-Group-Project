package org.seng302.project.webLayer.controller;


import org.seng302.project.repositoryLayer.repository.BusinessRepository;
import org.seng302.project.repositoryLayer.repository.ImageRepository;
import org.seng302.project.repositoryLayer.repository.ProductRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.dto.DeleteProductImageDTO;
import org.seng302.project.serviceLayer.dto.SetPrimaryProductImageDTO;
import org.seng302.project.serviceLayer.exceptions.NoBusinessExistsException;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.ForbiddenAdministratorActionException;
import org.seng302.project.serviceLayer.exceptions.productImages.NoProductImageWithIdException;
import org.seng302.project.serviceLayer.exceptions.productImages.ProductNotFoundException;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * REST controller for handling requests to do with product images.
 */
@RestController
public class ProductImagesController {

    private static final Logger logger = LoggerFactory.getLogger(ProductImagesController.class.getName());

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;

    @Autowired
    public ProductImagesController(
            UserRepository userRepository,
            BusinessRepository businessRepository,
            ProductRepository productRepository,
            ImageRepository imageRepository
            ) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.productRepository = productRepository;
        this.imageRepository = imageRepository;
    }

    /**
     * Handles request to set a primary image for a product.
     */
    @PutMapping("/businesses/{businessId}/products/{productId}/images/{imageId}/makeprimary")
    @ResponseStatus(HttpStatus.OK)
    public void setPrimaryImage(@PathVariable int businessId, @PathVariable String productId,
                                @PathVariable int imageId, @AuthenticationPrincipal AppUserDetails appUser) {

        try {
            var requestDTO = new SetPrimaryProductImageDTO(userRepository, businessRepository, productRepository,
                    businessId, productId, imageId, appUser);
            requestDTO.executeRequest();
        } catch (NoBusinessExistsException | ForbiddenAdministratorActionException
                | ProductNotFoundException | NoProductImageWithIdException handledException) {
            logger.error(handledException.getMessage());
            throw handledException;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while updating product's primary image: %s", unhandledException.getMessage()));
        }

    }

    @DeleteMapping("/businesses/{businessId}/products/{productId}/images/{imageId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteImage(@PathVariable int businessId, @PathVariable String productId, @PathVariable int imageId, @AuthenticationPrincipal AppUserDetails appUser) throws IOException {
        try {
            var requestDTO = new DeleteProductImageDTO(userRepository, businessRepository, productRepository, imageRepository,
                    businessId, productId, imageId, appUser);
            requestDTO.executeRequest();
        } catch (NoBusinessExistsException | ForbiddenAdministratorActionException | ProductNotFoundException
                | NoProductImageWithIdException | IOException exception) {
            logger.error(exception.getMessage());
            throw exception;
        } catch (Exception exception) {
            logger.error(String.format("Unexpected error while deleting product's image: %s", exception.getMessage()));
        }
    }
}
