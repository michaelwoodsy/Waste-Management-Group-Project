package org.seng302.project.serviceLayer.dto;

import com.sun.xml.bind.v2.TODO;
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
import java.util.Optional;

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

    /**
     * Called by the deleteImage() method in ProductImagesController.
     * Handles the business logic for deleting a product image,
     * throws exceptions up to the controller to handle
     */
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
            /* Can't use yet because images are not currently added properly
            String filename = imageToDelete.getFilename();
            var imagePath = Paths.get(filename);
            Files.delete(imagePath);
             */
            //TODO: Figure out how to get images to delete from repository properly
            product.removeImage(imageToDelete);
            imageRepository.delete(imageToDelete);
            productRepository.save(product);
            logger.info("{}", productRepository.findByIdAndBusinessId("PP1", 1));
        } else {
            throw new NoProductImageWithIdException(productId, imageId);
        }
    }
}
