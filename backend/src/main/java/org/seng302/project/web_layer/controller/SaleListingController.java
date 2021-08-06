package org.seng302.project.web_layer.controller;

import org.seng302.project.repository_layer.model.*;
import org.seng302.project.repository_layer.repository.BusinessRepository;
import org.seng302.project.repository_layer.repository.InventoryItemRepository;
import org.seng302.project.repository_layer.repository.SaleListingRepository;
import org.seng302.project.repository_layer.repository.UserRepository;
import org.seng302.project.service_layer.dto.sale_listings.PostSaleListingDTO;
import org.seng302.project.service_layer.dto.sale_listings.GetSaleListingDTO;
import org.seng302.project.service_layer.dto.sale_listings.SearchSaleListingsDTO;
import org.seng302.project.service_layer.exceptions.business.BusinessNotFoundException;
import org.seng302.project.service_layer.exceptions.businessAdministrator.ForbiddenAdministratorActionException;
import org.seng302.project.service_layer.service.SaleListingService;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;


/**
 * Rest controller for sale listings.
 */
@RestController
public class SaleListingController {

    private static final Logger logger = LoggerFactory.getLogger(SaleListingController.class.getName());
    private final BusinessRepository businessRepository;
    private final SaleListingRepository saleListingRepository;
    private final UserRepository userRepository;
    private final InventoryItemRepository inventoryItemRepository;

    private final SaleListingService saleListingService;

    @Autowired
    public SaleListingController(
            BusinessRepository businessRepository,
            SaleListingRepository saleListingRepository,
            UserRepository userRepository,
            InventoryItemRepository inventoryItemRepository,
            SaleListingService saleListingService) {
        this.businessRepository = businessRepository;
        this.saleListingRepository = saleListingRepository;
        this.userRepository = userRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.saleListingService = saleListingService;
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
     * @throws BusinessNotFoundException Thrown if the business doesn't exist.
     */
    private Business getBusiness(Integer businessId) throws BusinessNotFoundException {
        // Get business from repository
        Optional<Business> foundBusiness = businessRepository.findById(businessId);

        // Check if the business exists
        if (foundBusiness.isEmpty()) {
            BusinessNotFoundException exception = new BusinessNotFoundException(businessId);
            logger.warn(exception.getMessage());
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
        // Check if the logged in user is the business owner / administrator or a GAA
        if (!(business.userIsAdmin(user.getId()) ||
                business.getPrimaryAdministratorId().equals(user.getId())) && !user.isGAA()) {
            ForbiddenAdministratorActionException exception = new ForbiddenAdministratorActionException(business.getId());
            logger.warn(exception.getMessage());
            throw exception;
        }
    }

    /**
     * Searches all sale listings by supplied parameters
     *
     * @param searchQuery               query to search by
     * @param matchingProductName       whether you want to search by product name
     * @param matchingBusinessName      whether you want to search by business name
     * @param matchingBusinessLocation  whether you want to search by business location
     * @param priceRangeLower           the lower price range (can be null)
     * @param priceRangeUpper           the upper price range (can be null)
     * @param closingDateLower          the lower closing date range (can be null)
     * @param closingDateUpper          the upper closing date range (can be null)
     * @param pageNumber                the page number to get
     * @param sortBy                    the sorting parameter
     * @return A list of sale listings, with the specified sorting and page applied
     */
    @GetMapping("/listings")
    public List<Object> searchSaleListings(
            @RequestParam("searchQuery") String searchQuery,
            @RequestParam("matchingProductName") boolean matchingProductName,
            @RequestParam("matchingBusinessName") boolean matchingBusinessName,
            @RequestParam("matchingBusinessLocation") boolean matchingBusinessLocation,
            @RequestParam("matchingBusinessType") boolean matchingBusinessType,
            @RequestParam(name = "priceRangeLower", required = false) Double priceRangeLower,
            @RequestParam(name = "priceRangeUpper", required = false) Double priceRangeUpper,
            @RequestParam(name = "closingDateLower", required = false) String closingDateLower,
            @RequestParam(name = "closingDateUpper", required = false) String closingDateUpper,
            @RequestParam("pageNumber") Integer pageNumber,
            @RequestParam("sortBy") String sortBy)

    {
        try {
            SearchSaleListingsDTO dto = new SearchSaleListingsDTO(
                    searchQuery,
                    matchingProductName,
                    matchingBusinessName,
                    matchingBusinessLocation,
                    matchingBusinessType,
                    priceRangeLower,
                    priceRangeUpper,
                    closingDateLower,
                    closingDateUpper,
                    sortBy,
                    pageNumber);

            return saleListingService.searchSaleListings(dto);
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while searching sales listings: %s",
                    unhandledException.getMessage()));
            throw unhandledException;
        }
    }

    /**
     * Gets a list of sale listings for a business.
     * @param businessId Business to get the sale listings from.
     * @param appUser The user that made the request.
     * @return List of sale listings.
     */
    @GetMapping("/businesses/{businessId}/listings")
    public List<GetSaleListingDTO> getBusinessListings(
            @PathVariable int businessId,
            @AuthenticationPrincipal AppUserDetails appUser) {
        return saleListingService.getBusinessListings(businessId, appUser);
    }

    /**
     * Adds a new sale listing to a business.
     * @param businessId Business to get the sale listings from.
     * @param appUser The user that made the request.
     */
    @PostMapping("/businesses/{businessId}/listings")
    @ResponseStatus(HttpStatus.CREATED)
    public void newBusinessListing(
            @PathVariable int businessId, @Valid @RequestBody PostSaleListingDTO requestDTO,
            @AuthenticationPrincipal AppUserDetails appUser) {
        saleListingService.newBusinessListing(requestDTO, businessId, appUser);
    }
}
