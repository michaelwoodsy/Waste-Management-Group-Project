package org.seng302.project.webLayer.controller;

import org.seng302.project.serviceLayer.dto.business.DeleteBusinessImageDTO;
import org.seng302.project.serviceLayer.exceptions.business.BusinessImageNotFoundException;
import org.seng302.project.serviceLayer.exceptions.business.BusinessNotFoundException;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.ForbiddenAdministratorActionException;
import org.seng302.project.serviceLayer.service.BusinessImageService;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BusinessImageController {

    private static final Logger logger = LoggerFactory.getLogger(BusinessImageController.class.getName());

    private final BusinessImageService businessImageService;

    @Autowired
    public BusinessImageController(BusinessImageService businessImageService) {
        this.businessImageService = businessImageService;
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
