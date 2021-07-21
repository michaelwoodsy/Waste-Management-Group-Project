package org.seng302.project.serviceLayer.service;

import org.seng302.project.repositoryLayer.model.Image;
import org.seng302.project.repositoryLayer.repository.BusinessRepository;
import org.seng302.project.repositoryLayer.repository.ImageRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.dto.business.DeleteBusinessImageDTO;
import org.seng302.project.serviceLayer.exceptions.business.BusinessImageInvalidException;
import org.seng302.project.serviceLayer.exceptions.business.BusinessImageNotFoundException;
import org.seng302.project.serviceLayer.exceptions.business.BusinessNotFoundException;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.ForbiddenAdministratorActionException;
import org.seng302.project.serviceLayer.util.ImageUtil;
import org.seng302.project.serviceLayer.util.SpringEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Set;

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
        Set<Image> businesssImages = business.getImages();
        Image imageToDelete = null;
        Image newPrimaryImage = null;
        var imageIsPresent = false;
        for (Image image : userImages) {
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
            throw new BusinessImageNotFoundException(dto.getBusinessId(), dto.getImageId());

        }
    }
