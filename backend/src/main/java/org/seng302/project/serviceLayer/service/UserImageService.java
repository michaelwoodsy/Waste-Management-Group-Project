package org.seng302.project.serviceLayer.service;

import org.seng302.project.repositoryLayer.model.Image;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.ImageRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.dto.user.AddUserImageDTO;
import org.seng302.project.serviceLayer.dto.user.AddUserImageResponseDTO;
import org.seng302.project.serviceLayer.dto.user.DeleteUserImageDTO;
import org.seng302.project.serviceLayer.exceptions.NotAcceptableException;
import org.seng302.project.serviceLayer.exceptions.user.ForbiddenUserException;
import org.seng302.project.serviceLayer.exceptions.user.UserImageInvalidException;
import org.seng302.project.serviceLayer.exceptions.user.UserImageNotFoundException;
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
public class UserImageService {

    private static final Logger logger = LoggerFactory.getLogger(UserImageService.class.getName());

    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final ImageUtil imageUtil;
    private final SpringEnvironment springEnvironment;

    @Autowired
    public UserImageService(UserRepository userRepository,
                            ImageRepository imageRepository,
                            ImageUtil imageUtil,
                            SpringEnvironment springEnvironment) {
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
        this.imageUtil = imageUtil;
        this.springEnvironment = springEnvironment;
    }

    /**
     * Called by the addImage method in UserImageController.
     * Handles the business logic for adding an image for a user,
     * throws exceptions up to the controller to handle
     *
     * @param dto RequestDTO containing necessary details for adding an image
     * @return ResponseDTO, confirming a successful request, which is sent to the controller
     */
    public AddUserImageResponseDTO addUserImage(AddUserImageDTO dto) {
        logger.info("Request to add an image for user {}", dto.getUserId());

        String fileType = dto.getImageFile().getContentType();
        if (fileType == null || !fileType.contains("image")) {
            throw new UserImageInvalidException();
        }

        // Get the logged in user from the users email
        String userEmail = dto.getAppUser().getUsername();
        var loggedInUser = userRepository.findByEmail(userEmail).get(0);

        // Get the user who we want to add an image for
        Optional<User> userResult = userRepository.findById(dto.getUserId());

        // Check if the user exists
        if (userResult.isEmpty()) {
            throw new NotAcceptableException(String.format("No User exists with ID %d", dto.getUserId()));
        }
        // We know user exists so retrieve user properly
        var user = userResult.get();

        // Check if the logged in user is the same user who we are adding an image for (or GAA)
        if (!loggedInUser.getId().equals(user.getId()) && !loggedInUser.isGAA()) {
            throw new ForbiddenUserException(dto.getUserId());
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
            user.addImage(image);
            userRepository.save(user);
            return new AddUserImageResponseDTO(image.getId());
        } catch (IOException ex) {
            var exception = new UserImageInvalidException();
            logger.error(exception.getMessage());
            throw exception;
        }
    }

    /**
     * Called by the deleteImage() method in UserImageController.
     * Handles the business logic for deleting a user image,
     * throws exceptions up to the controller to handle
     */
    public void deleteImage(DeleteUserImageDTO dto) {
        logger.info("Request to delete an image for user {}", dto.getUserId());

        // Get the logged in user from the users email
        String userEmail = dto.getAppUser().getUsername();
        var loggedInUser = userRepository.findByEmail(userEmail).get(0);

        // Get the user who we want to add an image for
        Optional<User> userResult = userRepository.findById(dto.getUserId());

        // Check if the user exists
        if (userResult.isEmpty()) {
            throw new NotAcceptableException(String.format("No User exists with ID %d", dto.getUserId()));
        }
        // We know user exists so retrieve user properly
        var user = userResult.get();

        // Check if the logged in user is the same user who we are adding an image for (or GAA)
        if (!loggedInUser.getId().equals(user.getId()) && !loggedInUser.isGAA()) {
            throw new ForbiddenUserException(dto.getUserId());
        }

        //Check if image exists for user
        var userImages = user.getImages();
        Image imageToDelete = null;
        Image newPrimaryImage = null;
        var imageInUserImages = false;
        for (Image image : userImages) {
            if (image.getId().equals(dto.getImageId())) {
                imageInUserImages = true;
                imageToDelete = image;
            } else {
                //Assign a candidate for new primary image
                if (newPrimaryImage == null) {
                    newPrimaryImage = image;
                }
            }
        }

        if (imageInUserImages) {

            String imageFilePath = springEnvironment.getMediaFolderPath() + imageToDelete.getFilename();
            String thumbnailFilePath = springEnvironment.getMediaFolderPath() + imageToDelete.getThumbnailFilename();

            user.removeImage(imageToDelete);

            //Reassign primary image if necessary
            if (newPrimaryImage != null && dto.getImageId().equals(user.getPrimaryImageId())) {
                user.setPrimaryImageId(newPrimaryImage.getId());
            }
            userRepository.save(user);
            imageRepository.delete(imageToDelete);

            try {
                imageUtil.deleteImage(imageFilePath);
                imageUtil.deleteImage(thumbnailFilePath);
            } catch (IOException ex) {
                var exception = new UserImageInvalidException();
                logger.error(exception.getMessage());
                throw exception;
            }
        } else {
            throw new UserImageNotFoundException(dto.getUserId(), dto.getImageId());
        }
    }
}
