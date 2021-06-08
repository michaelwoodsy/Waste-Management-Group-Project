package org.seng302.project.serviceLayer.service;

import org.seng302.project.repositoryLayer.model.Image;
import org.seng302.project.repositoryLayer.repository.BusinessRepository;
import org.seng302.project.repositoryLayer.repository.ProductRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.dto.SetPrimaryProductImageDTO;
import org.seng302.project.serviceLayer.exceptions.NoBusinessExistsException;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.ForbiddenAdministratorActionException;
import org.seng302.project.serviceLayer.exceptions.productImages.NoProductImageWithIdException;
import org.seng302.project.serviceLayer.exceptions.productImages.ProductNotFoundException;
import org.seng302.project.serviceLayer.util.ImageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class ProductImageService {

    private static final Logger logger = LoggerFactory.getLogger(ProductImageService.class.getName());

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final ProductRepository productRepository;
    private final ImageUtil imageUtil;

    @Autowired
    public ProductImageService(UserRepository userRepository,
                               BusinessRepository businessRepository,
                               ProductRepository productRepository,
                               ImageUtil imageUtil) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.productRepository = productRepository;
        this.imageUtil = imageUtil;
    }

    public void addProductImage(Integer userId, Integer businessId, String productId, MultipartFile imageFile) {
        String imageFileName = UUID.randomUUID().toString();
    }

    /**
     * Called by the setPrimaryImage() method in ProductImagesController.
     * Handles the business logic for updating a product's primary image,
     * throws exceptions up to the controller to handle
     */
    public void setPrimaryImage(SetPrimaryProductImageDTO dto) {
        logger.info("Request to update primary image for product {} of business {}", dto.getProductId(), dto.getBusinessId());

        var currBusiness = businessRepository.findById(dto.getBusinessId()).orElseThrow(() -> new NoBusinessExistsException(dto.getBusinessId()));

        //Check if user making request is business admin/gaa/dgaa
        String userEmail = dto.getAppUser().getUsername();
        var loggedInUser = userRepository.findByEmail(userEmail).get(0);

        if (!(currBusiness.userIsAdmin(loggedInUser.getId()) ||
                currBusiness.getPrimaryAdministratorId().equals(loggedInUser.getId())) && !loggedInUser.isGAA()) {
            throw new ForbiddenAdministratorActionException(dto.getBusinessId());
        }

        var product = productRepository.findByIdAndBusinessId(dto.getProductId(), dto.getBusinessId())
                .orElseThrow(() -> new ProductNotFoundException(dto.getProductId(), dto.getBusinessId()));

        //Check if image exists for product
        var productImages = product.getImages();
        var imageInProductImages = false;
        for (Image image: productImages) {
            if (image.getId().equals(dto.getImageId())) {
                imageInProductImages = true;
                break;
            }
        }

        if (imageInProductImages) {
            product.setPrimaryImageId(dto.getImageId());
            productRepository.save(product);
        } else {
            throw new NoProductImageWithIdException(dto.getProductId(), dto.getImageId());
        }
    }

}
