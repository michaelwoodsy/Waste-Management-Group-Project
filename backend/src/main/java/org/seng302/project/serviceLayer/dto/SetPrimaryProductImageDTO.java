package org.seng302.project.serviceLayer.dto;

import lombok.Data;
import org.seng302.project.repositoryLayer.model.Image;
import org.seng302.project.repositoryLayer.repository.BusinessRepository;
import org.seng302.project.repositoryLayer.repository.ProductRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.exceptions.NoBusinessExistsException;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.ForbiddenAdministratorActionException;
import org.seng302.project.serviceLayer.exceptions.productImages.NoProductImageWithIdException;
import org.seng302.project.serviceLayer.exceptions.productImages.ProductNotFoundException;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
public class SetPrimaryProductImageDTO {

    private final Integer businessId;
    private final String productId;
    private final Integer imageId;
    private final AppUserDetails appUser;

    public SetPrimaryProductImageDTO(Integer businessId, String productId,
                                     Integer imageId, AppUserDetails appUser) {
        this.businessId = businessId;
        this.productId = productId;
        this.imageId = imageId;
        this.appUser = appUser;
    }

}
