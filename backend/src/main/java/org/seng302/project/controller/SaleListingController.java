package org.seng302.project.controller;

import org.seng302.project.controller.authentication.AppUserDetails;
import org.seng302.project.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Rest controller for sale listings.
 */
@RestController
public class SaleListingController {

    private static final Logger logger = LoggerFactory.getLogger(ProductCatalogueController.class.getName());
    private final BusinessRepository businessRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final UserRepository userRepository;

    @Autowired
    public SaleListingController(
            BusinessRepository businessRepository,
            InventoryItemRepository inventoryItemRepository,
            UserRepository userRepository) {
        this.businessRepository = businessRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.userRepository = userRepository;
    }

    /**
     * Returns the current logged in user that made the request.
     * @param appUser The AppUserDetails object passed in from the authentication principle.
     * @return User: the user that made the request.
     */
    private User getLoggedInUser(AppUserDetails appUser) {
        String userEmail = appUser.getUsername();
        return userRepository.findByEmail(userEmail).get(0);
    }

    /**
     * Gets a list of sale listings for a business.
     * @param businessId Business to get the sale listings from.
     * @param appUser The user that made the request.
     * @return List of sale listings.
     */
    @GetMapping("/businesses/{businessId}/listings")
    public List<SaleListing> getBusinessesListings(
            @PathVariable int businessId,
            @AuthenticationPrincipal AppUserDetails appUser) {
        logger.info(getLoggedInUser(appUser).getEmail());
        return List.of();
    }

}
