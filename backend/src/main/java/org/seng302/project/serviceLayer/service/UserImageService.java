package org.seng302.project.serviceLayer.service;

import org.seng302.project.repositoryLayer.model.Image;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.ImageRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.dto.user.AddUserImageDTO;
import org.seng302.project.serviceLayer.dto.user.AddUserImageResponseDTO;
import org.seng302.project.serviceLayer.exceptions.NoUserExistsException;
import org.seng302.project.serviceLayer.exceptions.NotAcceptableException;
import org.seng302.project.serviceLayer.exceptions.dgaa.ForbiddenSystemAdminActionException;
import org.seng302.project.serviceLayer.exceptions.user.ForbiddenUserException;
import org.seng302.project.serviceLayer.exceptions.user.UserImageInvalidException;
import org.seng302.project.serviceLayer.exceptions.user.UserImageNotFoundException;
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
        logger.info("Request to add an image image for user {}", dto.getUserId());

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
     * Called by the setPrimaryImage() method in UserImagesController.
     * Handles the User logic for updating a User's primary image,
     * throws exceptions up to the controller to handle
     *
     * @param userId The Id of the user that you wish to change primary image of
     * @param imageId The Id of the image you wish to make the primary image
     * @param appUser The user that is trying to perform this action
     */
    public void setPrimaryImage(Integer userId, Integer imageId, AppUserDetails appUser) {
        logger.info("Request to update primary image for User {}", userId);

        //Check if the userId given corresponds to a real user
        Optional<User> receivingUserOptional = userRepository.findById(userId);
        if (receivingUserOptional.isEmpty()) {
            throw new NoUserExistsException(userId);
        }
        var receivingUser = receivingUserOptional.get();

        // Check if the logged in user is the same user whose messages we are retrieving
        String userEmail = appUser.getUsername();
        var loggedInUser = userRepository.findByEmail(userEmail).get(0);

        //If the logged in user and the given user ID do not match and the user is not a GAA then they aren't allowed to perform that action
        if (!loggedInUser.getId().equals(userId) && !loggedInUser.isGAA()) {
            throw new ForbiddenSystemAdminActionException();
        }

        //Check if image exists for User
        var userImages = receivingUser.getImages();
        var imageInUserImages = false;
        for (Image image : userImages) {
            if (image.getId().equals(imageId)) {
                imageInUserImages = true;
                break;
            }
        }

        //If image exists in User's list of images, set the primary image
        if (imageInUserImages) {
            receivingUser.setPrimaryImageId(imageId);
            userRepository.save(receivingUser);
        } else {
            throw new UserImageNotFoundException(userId, imageId);
        }
    }
}
