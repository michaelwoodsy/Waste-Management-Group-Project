package org.seng302.project.webLayer.controller;

import org.seng302.project.serviceLayer.dto.business.AddBusinessImageDTO;
import org.seng302.project.serviceLayer.dto.business.AddBusinessImageResponseDTO;
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
}
