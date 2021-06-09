package org.seng302.project.serviceLayer.dto;

import org.seng302.project.repositoryLayer.model.Image;
import org.seng302.project.repositoryLayer.repository.BusinessRepository;
import org.seng302.project.repositoryLayer.repository.ProductRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.exceptions.NoBusinessExistsException;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.ForbiddenAdministratorActionException;
import org.seng302.project.serviceLayer.exceptions.productImages.NoProductImageWithIdException;
import org.seng302.project.serviceLayer.exceptions.productImages.ProductNotFoundException;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SetPrimaryProductImageDTO {

    private final Integer businessId;
    private final String productId;
    private final Integer imageId;
    private final AppUserDetails appUser;

    private final UserRepository userRepository;

    private final BusinessRepository businessRepository;

    private final ProductRepository productRepository;

    private static final Logger logger = LoggerFactory.getLogger(SetPrimaryProductImageDTO.class.getName());

    public SetPrimaryProductImageDTO(UserRepository userRepository,
                                     BusinessRepository businessRepository,
                                     ProductRepository productRepository,
                                     Integer businessId, String productId,
                                     Integer imageId, AppUserDetails appUser) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.productRepository = productRepository;
        this.businessId = businessId;
        this.productId = productId;
        this.imageId = imageId;
        this.appUser = appUser;
    }

    /**
     * Called by the setPrimaryImage() method in ProductImagesController.
     * Handles the business logic for updating a product's primary image,
     * throws exceptions up to the controller to handle
     */
    public void executeRequest() {
        logger.info("Request to update primary image for product {} of business {}", productId, businessId);

        var currBusiness = businessRepository.findById(businessId).orElseThrow(() -> new NoBusinessExistsException(businessId));

        //Check if user making request is business admin/gaa/dgaa
        String userEmail = appUser.getUsername();
        var loggedInUser = userRepository.findByEmail(userEmail).get(0);

        if (!(currBusiness.userIsAdmin(loggedInUser.getId()) ||
                currBusiness.getPrimaryAdministratorId().equals(loggedInUser.getId())) && !loggedInUser.isGAA()) {
            throw new ForbiddenAdministratorActionException(businessId);
        }

        var product = productRepository.findByIdAndBusinessId(productId, businessId)
                .orElseThrow(() -> new ProductNotFoundException(productId, businessId));

        //Check if image exists for product
        var productImages = product.getImages();
        var imageInProductImages = false;
        for (Image image: productImages) {
            if (image.getId().equals(imageId)) {
                imageInProductImages = true;
                break;
            }
        }

        if (imageInProductImages) {
            product.setPrimaryImageId(imageId);
            productRepository.save(product);
        } else {
            throw new NoProductImageWithIdException(productId, imageId);
        }
    }

}
