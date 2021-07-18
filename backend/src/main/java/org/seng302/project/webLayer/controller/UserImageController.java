package org.seng302.project.webLayer.controller;


import org.seng302.project.serviceLayer.dto.user.AddUserImageDTO;
import org.seng302.project.serviceLayer.dto.user.AddUserImageResponseDTO;
import org.seng302.project.serviceLayer.exceptions.NoUserExistsException;
import org.seng302.project.serviceLayer.exceptions.NotAcceptableException;
import org.seng302.project.serviceLayer.exceptions.user.ForbiddenUserException;
import org.seng302.project.serviceLayer.service.UserImageService;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UserImageController {

    private static final Logger logger = LoggerFactory.getLogger(UserImageController.class.getName());

    private final UserImageService userImageService;

    @Autowired
    public UserImageController(UserImageService userImageService) {
        this.userImageService = userImageService;
    }

    /**
     * Handles request to add an image for a user.
     */
    @PostMapping("/users/{userId}/images")
    @ResponseStatus(HttpStatus.CREATED)
    public AddUserImageResponseDTO addImage(@PathVariable Integer userId,
                                            @AuthenticationPrincipal AppUserDetails user,
                                            @RequestParam(value = "file") MultipartFile imageFile) {
        var requestDTO = new AddUserImageDTO(userId, user, imageFile);
        return userImageService.addUserImage(requestDTO);
    }

    /**
     * Handles request to set a primary image for a user.
     * @param userId The Id of the user that you wish to change primary image of
     * @param imageId The Id of the image you wish to make the primary image
     * @param appUser The user that is trying to perform this action
     */
    @PutMapping("/users/{userId}/images/{imageId}/makeprimary")
    @ResponseStatus(HttpStatus.OK)
    public void setPrimaryImage(@PathVariable Integer userId, @PathVariable Integer imageId,
                                @AuthenticationPrincipal AppUserDetails appUser) {

        try {
            userImageService.setPrimaryImage(userId, imageId, appUser);
        } catch (NoUserExistsException | NotAcceptableException | ForbiddenUserException handledException) {
            logger.error(handledException.getMessage());
            throw handledException;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while updating user's primary image: %s", unhandledException.getMessage()));
        }

    }
}
