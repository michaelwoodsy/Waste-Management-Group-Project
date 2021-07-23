package org.seng302.project.serviceLayer.service;

import org.seng302.project.repositoryLayer.model.Business;
import org.seng302.project.repositoryLayer.model.Image;
import org.seng302.project.repositoryLayer.repository.BusinessRepository;
import org.seng302.project.repositoryLayer.repository.ImageRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.dto.business.AddBusinessImageDTO;
import org.seng302.project.serviceLayer.dto.business.AddBusinessImageResponseDTO;
import org.seng302.project.serviceLayer.dto.business.DeleteBusinessImageDTO;
import org.seng302.project.serviceLayer.exceptions.ForbiddenException;
import org.seng302.project.serviceLayer.exceptions.NotAcceptableException;
import org.seng302.project.serviceLayer.exceptions.business.BusinessImageInvalidException;
import org.seng302.project.serviceLayer.exceptions.business.BusinessNotFoundException;
import org.seng302.project.serviceLayer.exceptions.business.NoBusinessExistsException;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.ForbiddenAdministratorActionException;
import org.seng302.project.serviceLayer.util.ImageUtil;
import org.seng302.project.serviceLayer.util.SpringEnvironment;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
public class BusinessImageService {

    private static final Logger logger = LoggerFactory.getLogger(BusinessImageService.class.getName());

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final ImageRepository imageRepository;
    private final ImageUtil imageUtil;
    private final SpringEnvironment springEnvironment;

    @Autowired
    public BusinessImageService(UserRepository userRepository,
                                BusinessRepository businessRepository,
                                ImageRepository imageRepository,
                                ImageUtil imageUtil,
                                SpringEnvironment springEnvironment) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.imageRepository = imageRepository;
        this.imageUtil = imageUtil;
        this.springEnvironment = springEnvironment;
    }

    /**
     * Called by the addImage method in BusinessImageController.
     * Handles the business logic for adding an image for a business,
     * throws exceptions up to the controller to handle
     *
     * @param dto RequestDTO containing necessary details for adding an image
     * @return ResponseDTO, confirming a successful request, which is sent to the controller
     */
    public AddBusinessImageResponseDTO addBusinessImage(AddBusinessImageDTO dto) {
        logger.info("Request to add an image for business {}", dto.getBusinessId());

        String fileType = dto.getImageFile().getContentType();
        if (fileType == null || !fileType.contains("image")) {
            throw new BusinessImageInvalidException();
        }

        // Get the logged in user from the user's email
        String userEmail = dto.getAppUser().getUsername();
        var loggedInUser = userRepository.findByEmail(userEmail).get(0);

        // Get the business who we want to add an image for
        Optional<Business> businessResult = businessRepository.findById(dto.getBusinessId());

        // Check if the business exists
        if (businessResult.isEmpty()) {
            throw new NoBusinessExistsException(dto.getBusinessId());
        }
        // We know business exists so retrieve user properly
        var business = businessResult.get();

        // Check the logged in user is an admin for the business we are adding the image to (or GAA)
        if (!business.userCanDoAction(loggedInUser)) {
            throw new ForbiddenAdministratorActionException(dto.getBusinessId());
        }

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
            business.addImage(image);
            businessRepository.save(business);
            return new AddBusinessImageResponseDTO(image.getId());
        } catch (IOException ex) {
            var exception = new BusinessImageInvalidException();
            logger.error(exception.getMessage());
            throw exception;
        }
    }

    /**
     * Called by the setPrimaryImage() method in BusinessImageController.
     * Handles the Business logic for updating a Businesses primary image,
     * throws exceptions up to the controller to handle
     *
     * @param businessId The Id of the business that you wish to change primary image of
     * @param imageId    The Id of the image you wish to make the primary image
     * @param appUser    The user that is trying to perform this action
     */
    public void setPrimaryImage(Integer businessId, Integer imageId, AppUserDetails appUser) {
        logger.info("Request to update primary image for Business {}", businessId);

        //Check if the businessId given corresponds to a real user
        Optional<Business> receivingBusinessOptional = businessRepository.findById(businessId);
        if (receivingBusinessOptional.isEmpty()) {
            throw new NotAcceptableException(String.format("No Business with ID: %d exists", businessId));
        }
        var business = receivingBusinessOptional.get();

        // Check if the logged in user is a primary administrator of the business, or a GAA
        var loggedInUser = userRepository.findByEmail(appUser.getUsername()).get(0);
        if (!business.userCanDoAction(loggedInUser)) {
            throw new ForbiddenException("User is not authorised to update this businesses primary image");
        }

        //Check if image exists for Business
        var businessImages = business.getImages();
        var imageInBusinessImages = false;
        for (Image image : businessImages) {
            if (image.getId().equals(imageId)) {
                imageInBusinessImages = true;
                break;
            }
        }

        //If image exists in User's list of images, set the primary image
        if (imageInBusinessImages) {
            business.setPrimaryImageId(imageId);
            businessRepository.save(business);
        } else {
            throw new NotAcceptableException(String.format(
                    "Business with ID %d does not have an image with ID %d", businessId, imageId));
        }
    }


    public void deleteImage(DeleteBusinessImageDTO dto) {
        logger.info("Request to delete image business {}", dto.getBusinessId());

        var business = businessRepository.findById(dto.getBusinessId())
                .orElseThrow(() -> new BusinessNotFoundException(dto.getBusinessId()));

        //Check user is business admin/dgaa/gaa
        var loggedInUser = userRepository.findByEmail(dto.getAppUser().getUsername()).get(0);
        if (!business.userCanDoAction(loggedInUser)) {
            throw new ForbiddenAdministratorActionException(dto.getBusinessId());
        }

        //Retrieve image to be deleted.
        var businessImages = business.getImages();
        Image imageToDelete = null;
        Image newPrimaryImage = null;
        var imageIsPresent = false;
        for (Image image : businessImages) {
            if (image.getId().equals(dto.getImageId())) {
                imageIsPresent = true;
                imageToDelete = image;
            } else {
                //Assign a candidate for new primary image
                if (newPrimaryImage == null) {
                    newPrimaryImage = image;
                }
            }
        }

        //remove image if present
        if (imageIsPresent) {
            String imageFilePath = springEnvironment.getMediaFolderPath() + imageToDelete.getFilename();
            String thumbnailFilePath = springEnvironment.getMediaFolderPath() + imageToDelete.getThumbnailFilename();

            business.removeImage(imageToDelete);

            //Reassign primary image if necessary
            if (newPrimaryImage != null && dto.getImageId().equals(business.getPrimaryImageId())) {
                business.setPrimaryImageId(newPrimaryImage.getId());
            }

            businessRepository.save(business);
            imageRepository.delete(imageToDelete);

            try {
                imageUtil.deleteImage(imageFilePath);
                imageUtil.deleteImage(thumbnailFilePath);
            } catch (IOException ex) {
                var exception = new BusinessImageInvalidException();
                logger.error(exception.getMessage());
                throw exception;
            }
        } else {
            throw new NotAcceptableException(String.format(
                    "Business %s does not have an image with id %d", dto.getBusinessId(), dto.getImageId()));

        }
    }
}
