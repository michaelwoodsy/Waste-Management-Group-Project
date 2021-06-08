package org.seng302.project.webLayer.controller;

import org.seng302.project.serviceLayer.dto.AddImageResponseDTO;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ProductImageController {

    @PostMapping("/businesses/{businessId}/products/{productId}/images")
    public AddImageResponseDTO addImage(@PathVariable Integer businessId,
                                        @PathVariable String productId,
                                        @AuthenticationPrincipal AppUserDetails user,
                                        @RequestBody MultipartFile imageFile) {
        return null;
    }

}
