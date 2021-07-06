package org.seng302.project.serviceLayer.service;

import org.seng302.project.repositoryLayer.model.Image;
import org.seng302.project.repositoryLayer.repository.BusinessRepository;
import org.seng302.project.repositoryLayer.repository.ImageRepository;
import org.seng302.project.repositoryLayer.repository.ProductRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.dto.AddProductImageDTO;
import org.seng302.project.serviceLayer.dto.AddProductImageResponseDTO;
import org.seng302.project.serviceLayer.dto.product.SetPrimaryProductImageDTO;
import org.seng302.project.serviceLayer.exceptions.business.BusinessNotFoundException;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.ForbiddenAdministratorActionException;
import org.seng302.project.serviceLayer.exceptions.product.ProductImageInvalidException;
import org.seng302.project.serviceLayer.exceptions.product.ProductImageNotFoundException;
import org.seng302.project.serviceLayer.exceptions.product.ProductNotFoundException;
import org.seng302.project.serviceLayer.util.ImageUtil;
import org.seng302.project.serviceLayer.util.SpringEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Service
public class ProductImageService {

    private static final Logger logger = LoggerFactory.getLogger(ProductImageService.class.getName());

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;
    private final ImageUtil imageUtil;
    private final SpringEnvironment springEnvironment;

    @Autowired
    public ProductImageService(UserRepository userRepository,
                               BusinessRepository businessRepository,
                               ProductRepository productRepository,
                               ImageRepository imageRepository,
                               ImageUtil imageUtil,
                               SpringEnvironment springEnvironment) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.productRepository = productRepository;
        this.imageRepository = imageRepository;
        this.imageUtil = imageUtil;
        this.springEnvironment = springEnvironment;
    }

    /**
     * Called by the addImage method in ProductImagesController.
     * Handles the business logic for adding an image for a product,
     * throws exceptions up to the controller to handle
     *
     * @param dto RequestDTO containing necessary details for adding an image
     * @return ResponseDTO, confirming a successful request, which is sent to the controller
     */
    public AddProductImageResponseDTO addProductImage(AddProductImageDTO dto) {
        System.out.println(springEnvironment.getMediaFolderPath());
        logger.info("Request to add an image image for product {} of business {}", dto.getProductId(), dto.getBusinessId());

        String fileType = dto.getImageFile().getContentType();
        if (fileType == null || !fileType.contains("image")) {
            throw new ProductImageInvalidException();
        }

        var currBusiness = businessRepository.findById(dto.getBusinessId()).orElseThrow(
                () -> new BusinessNotFoundException(dto.getBusinessId())
        );

        //Check if user making request is business admin/gaa/dgaa
        String userEmail = dto.getAppUser().getUsername();
        var loggedInUser = userRepository.findByEmail(userEmail).get(0);

        if (!currBusiness.userCanDoAction(loggedInUser)) {
            throw new ForbiddenAdministratorActionException(dto.getBusinessId());
        }

        var product = productRepository
                .findByIdAndBusinessId(dto.getProductId(), dto.getBusinessId())
                .orElseThrow(() -> new ProductNotFoundException(dto.getProductId(), dto.getBusinessId()));

        try {
            var imageInput = imageUtil.readImageFromMultipartFile(dto.getImageFile());
            String extension = fileType.split("/")[1];
            logger.info("New image has extension: {}", extension);
            String imageFileName = UUID.randomUUID() + "." + extension;
            String imageFilePath = springEnvironment.getMediaFolderPath() + "/media/" + imageFileName;
            imageUtil.saveImage(imageInput, imageFilePath);
            String thumbnailPath = imageUtil.createThumbnail(imageFilePath);

            var image = new Image("/media/" + imageFileName, thumbnailPath);
            imageRepository.save(image);
            product.addImage(image);
            productRepository.save(product);
            return new AddProductImageResponseDTO(image.getId());
        } catch (IOException ex) {
            var exception = new ProductImageInvalidException();
            logger.error(exception.getMessage());
            throw exception;
        }

    }

    /**
     * Called by the setPrimaryImage() method in ProductImagesController.
     * Handles the business logic for updating a product's primary image,
     * throws exceptions up to the controller to handle
     */
    public void setPrimaryImage(SetPrimaryProductImageDTO dto) {
        logger.info("Request to update primary image for product {} of business {}", dto.getProductId(), dto.getBusinessId());

        var currBusiness = businessRepository.findById(dto.getBusinessId()).orElseThrow(() -> new BusinessNotFoundException(dto.getBusinessId()));

        //Check if user making request is business admin/gaa/dgaa
        String userEmail = dto.getAppUser().getUsername();
        var loggedInUser = userRepository.findByEmail(userEmail).get(0);

        if (!currBusiness.userCanDoAction(loggedInUser)) {
            throw new ForbiddenAdministratorActionException(dto.getBusinessId());
        }

        var product = productRepository.findByIdAndBusinessId(dto.getProductId(), dto.getBusinessId())
                .orElseThrow(() -> new ProductNotFoundException(dto.getProductId(), dto.getBusinessId()));

        //Check if image exists for product
        var productImages = product.getImages();
        var imageInProductImages = false;
        for (Image image : productImages) {
            if (image.getId().equals(dto.getImageId())) {
                imageInProductImages = true;
                break;
            }
        }

        if (imageInProductImages) {
            product.setPrimaryImageId(dto.getImageId());
            productRepository.save(product);
        } else {
            throw new ProductImageNotFoundException(dto.getProductId(), dto.getImageId());
        }
    }

}
