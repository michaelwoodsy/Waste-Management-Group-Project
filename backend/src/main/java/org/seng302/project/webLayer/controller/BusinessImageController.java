package org.seng302.project.webLayer.controller;

import org.seng302.project.serviceLayer.dto.business.AddBusinessImageDTO;
import org.seng302.project.serviceLayer.dto.business.AddBusinessImageResponseDTO;
import org.seng302.project.serviceLayer.exceptions.ForbiddenException;
import org.seng302.project.serviceLayer.exceptions.NoUserExistsException;
import org.seng302.project.serviceLayer.exceptions.NotAcceptableException;
import org.seng302.project.serviceLayer.exceptions.user.ForbiddenUserException;
import org.seng302.project.serviceLayer.service.BusinessImageService;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class BusinessImageController {

    private static final Logger logger = LoggerFactory.getLogger(BusinessImageController.class.getName());

    private final BusinessImageService businessImageService;

    /**
     * Handles request to add an image for a business.
     */
    @Autowired
    public BusinessImageController(BusinessImageService businessImageService) {
        this.businessImageService = businessImageService;
    }

    @PostMapping("/businesses/{businessId}/images")
    @ResponseStatus(HttpStatus.CREATED)
    public AddBusinessImageResponseDTO addImage(@PathVariable Integer businessId,
                                                @AuthenticationPrincipal AppUserDetails user,
                                                @RequestParam(value = "file") MultipartFile imageFile) {
        var requestDTO = new AddBusinessImageDTO(businessId, user, imageFile);
        return businessImageService.addBusinessImage(requestDTO);
    }

    /**
     * Handles request to set a primary image for a business.
     * @param businessId The Id of the business that you wish to change primary image of
     * @param imageId The Id of the image you wish to make the primary image
     * @param appUser The user that is trying to perform this action
     */
    @PutMapping("/businesses/{businessId}/images/{imageId}/makeprimary")
    @ResponseStatus(HttpStatus.OK)
    public void setPrimaryImage(@PathVariable Integer businessId, @PathVariable Integer imageId,
                                @AuthenticationPrincipal AppUserDetails appUser) {

        try {
            businessImageService.setPrimaryImage(businessId, imageId, appUser);
        } catch (NotAcceptableException | ForbiddenException handledException) {
            logger.error(handledException.getMessage());
            throw handledException;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while updating Businesses primary image: %s", unhandledException.getMessage()));
        }
    }

    /**
     * Handles request to delete a business image.
     */
    @DeleteMapping("/businesses/{businessId}/images/{imageId}")
    public void deleteImage(@PathVariable int businessId,
                            @PathVariable int imageId,
                            @AuthenticationPrincipal AppUserDetails appUser) {

        try {
            var requestDto = new DeleteBusinessImageDTO(businessId, imageId, appUser);
            businessImageService.deleteImage(requestDto);
        } catch (BusinessNotFoundException |
                ForbiddenAdministratorActionException |
                BusinessImageNotFoundException handledException) {
            logger.error(handledException.getMessage());
            throw handledException;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while deleting business image: %s", unhandledException.getMessage()));
        }
    }
}
