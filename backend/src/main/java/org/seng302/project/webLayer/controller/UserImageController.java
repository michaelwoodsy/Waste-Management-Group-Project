package org.seng302.project.webLayer.controller;

import org.seng302.project.serviceLayer.dto.user.AddUserImageResponseDTO;
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

    private static final Logger logger = LoggerFactory.getLogger(ProductImageController.class.getName());

    private final UserImageService userImageService;

    @Autowired
    public UserImageController(UserImageService userImageService) {
        this.userImageService = userImageService;
    }

    @PostMapping("/users/{userId}/images")
    @ResponseStatus(HttpStatus.CREATED)
    public AddUserImageResponseDTO addImage(@PathVariable Integer userId,
                                            @AuthenticationPrincipal AppUserDetails user,
                                            @RequestParam(value = "file") MultipartFile imageFile) {
        return null;
    }
}
