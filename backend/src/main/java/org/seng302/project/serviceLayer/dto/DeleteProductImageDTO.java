package org.seng302.project.serviceLayer.dto;

import org.seng302.project.repositoryLayer.model.Image;
import org.seng302.project.repositoryLayer.repository.BusinessRepository;
import org.seng302.project.repositoryLayer.repository.ImageRepository;
import org.seng302.project.repositoryLayer.repository.ProductRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.exceptions.NoBusinessExistsException;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.ForbiddenAdministratorActionException;
import org.seng302.project.serviceLayer.exceptions.productImages.NoProductImageWithIdException;
import org.seng302.project.serviceLayer.exceptions.productImages.ProductNotFoundException;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DeleteProductImageDTO {

    private final Integer businessId;
    private final String productId;
    private final Integer imageId;
    private final AppUserDetails appUser;

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;

    private static final Logger logger = LoggerFactory.getLogger(DeleteProductImageDTO.class.getName());

    public DeleteProductImageDTO(UserRepository userRepository,
                                     BusinessRepository businessRepository,
                                     ProductRepository productRepository,
                                     ImageRepository imageRepository,
                                     Integer businessId, String productId,
                                     Integer imageId, AppUserDetails appUser) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.productRepository = productRepository;
        this.imageRepository = imageRepository;
        this.businessId = businessId;
        this.productId = productId;
        this.imageId = imageId;
        this.appUser = appUser;
    }

    public void executeRequest() throws IOException {
        logger.info("Request to delete image for product {} of business {}", productId, businessId);

        var business = businessRepository.findById(businessId).orElseThrow(() -> new NoBusinessExistsException(businessId));

        String userEmail = appUser.getUsername();
        var loggedInUser = userRepository.findByEmail(userEmail).get(0);

        if (!(business.userIsAdmin(loggedInUser.getId()) || business.getPrimaryAdministratorId().equals(loggedInUser.getId())) && !loggedInUser.isGAA()) {
            throw new ForbiddenAdministratorActionException(businessId);
        }

        var product = productRepository.findByIdAndBusinessId(productId, businessId).orElseThrow(() -> new ProductNotFoundException(productId, businessId));

        Image imageToDelete = null;
        Image imageToReplace = null;
        var imageInProductImages = false;
        for (Image image: product.getImages()) {
            if (image.getId().equals(imageId)) {
                imageInProductImages = true;
                imageToDelete = image;
                break;
            } else {
                if (imageToReplace == null) {
                    imageToReplace = image;
                }
            }
        }

        if (imageInProductImages) {
            product.removeImage(imageToDelete);
            if (imageToReplace != null) {
                product.setPrimaryImageId(imageToReplace.getId());
            }
            String filename = imageToDelete.getFilename();
            var imagePath = Paths.get("../media/" + filename);
            Files.delete(imagePath);

            imageRepository.delete(imageToDelete);
            productRepository.save(product);
        } else {
            throw new NoProductImageWithIdException(productId, imageId);
        }
    }
}
