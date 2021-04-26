package org.seng302.project.controller;

import org.seng302.project.controller.authentication.AppUserDetails;
import org.seng302.project.exceptions.ForbiddenAdministratorActionException;
import org.seng302.project.exceptions.NoBusinessExistsException;
import org.seng302.project.exceptions.UserNotAdministratorException;
import org.seng302.project.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * Rest controller for sale listings.
 */
@RestController
public class SaleListingController {

    private static final Logger logger = LoggerFactory.getLogger(ProductCatalogueController.class.getName());
    private final BusinessRepository businessRepository;
    private final SaleListingRepository saleListingRepository;
    private final UserRepository userRepository;

    @Autowired
    public SaleListingController(
            BusinessRepository businessRepository,
            SaleListingRepository saleListingRepository,
            UserRepository userRepository) {
        this.businessRepository = businessRepository;
        this.saleListingRepository = saleListingRepository;
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
     * Gets a business with the provided id.
     * @param businessId The id of the business to look for.
     * @return The business with the corresponding id.
     * @throws NoBusinessExistsException Thrown if the business doesn't exist.
     */
    private Business getBusiness(Integer businessId) throws NoBusinessExistsException {
        // Get business from repository
        Optional<Business> foundBusiness = businessRepository.findById(businessId);

        // Check if the business exists
        if (foundBusiness.isEmpty()) {
            NoBusinessExistsException exception = new NoBusinessExistsException(businessId);
            logger.error(exception.getMessage());
            throw exception;
        }

        // Return the found business
        return foundBusiness.get();
    }

    /**
     * Checks if the user is the owner or administrator of the business. Throws an exception if they aren't
     * @param user The user to check.
     * @param business The business to check.
     * @throws ForbiddenAdministratorActionException Thrown if the user isn't and owner or admin of the business.
     */
    private void checkUserIsAdminOfBusiness(User user, Business business) throws ForbiddenAdministratorActionException {
        // Check if the logged in user is the business owner / administrator
        if (!(business.userIsAdmin(user.getId()) || business.getPrimaryAdministratorId().equals(user.getId()))) {
            ForbiddenAdministratorActionException exception = new ForbiddenAdministratorActionException(business.getId());
            logger.error(exception.getMessage());
            throw exception;
        }
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

        // Get the user that made the request
        User user = getLoggedInUser(appUser);

        logger.info("User with id " + user.getId() +
                " trying to get sale listings of business with id " + businessId  + ".");

        // Get the business of the request
        Business business = getBusiness(businessId);

        // Check the user is an admin of the business
        checkUserIsAdminOfBusiness(user, business);

        // Get the sale listings of the business
        return saleListingRepository.findAllByBusinessId(businessId);
    }

}
