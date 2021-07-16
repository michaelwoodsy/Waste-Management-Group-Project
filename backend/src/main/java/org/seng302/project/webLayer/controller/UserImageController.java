package org.seng302.project.webLayer.controller;

import org.seng302.project.serviceLayer.dto.user.AddUserImageDTO;
import org.seng302.project.serviceLayer.dto.user.AddUserImageResponseDTO;
import org.seng302.project.serviceLayer.dto.user.DeleteUserImageDTO;
import org.seng302.project.serviceLayer.exceptions.NotAcceptableException;
import org.seng302.project.serviceLayer.exceptions.user.ForbiddenUserException;
import org.seng302.project.serviceLayer.exceptions.user.UserImageNotFoundException;
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
     * Handles request to delete an image for a user.
     */
    @DeleteMapping("/users/{userId}/images/{imageId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteImage(@PathVariable int userId,
                            @PathVariable int imageId,
                            @AuthenticationPrincipal AppUserDetails appUser) {
        try {
            var requestDTO = new DeleteUserImageDTO(userId, imageId, appUser);
            userImageService.deleteImage(requestDTO);
        } catch (UserImageNotFoundException | ForbiddenUserException
                | NotAcceptableException handledException) {
            logger.error(handledException.getMessage());
            throw handledException;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while deleting user's image: %s", unhandledException.getMessage()));
        }
    }
}
