package org.seng302.project.serviceLayer.service;

import org.seng302.project.repositoryLayer.model.Business;
import org.seng302.project.repositoryLayer.model.Image;
import org.seng302.project.repositoryLayer.repository.BusinessRepository;
import org.seng302.project.repositoryLayer.repository.ImageRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.dto.business.AddBusinessImageDTO;
import org.seng302.project.serviceLayer.dto.business.AddBusinessImageResponseDTO;
import org.seng302.project.serviceLayer.exceptions.business.BusinessImageInvalidException;
import org.seng302.project.serviceLayer.exceptions.business.NoBusinessExistsException;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.ForbiddenAdministratorActionException;
import org.seng302.project.serviceLayer.util.ImageUtil;
import org.seng302.project.serviceLayer.util.SpringEnvironment;
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
        if (!loggedInUser.businessIsAdministered(business.getId()) && !loggedInUser.isGAA()) {
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
}
