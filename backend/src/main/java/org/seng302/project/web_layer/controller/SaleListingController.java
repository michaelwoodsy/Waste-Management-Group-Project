package org.seng302.project.web_layer.controller;

import net.minidev.json.JSONObject;
import org.seng302.project.service_layer.dto.sale_listings.GetSaleListingDTO;
import org.seng302.project.service_layer.dto.sale_listings.PostSaleListingDTO;
import org.seng302.project.service_layer.dto.sale_listings.SearchSaleListingsDTO;
import org.seng302.project.service_layer.exceptions.BadRequestException;
import org.seng302.project.service_layer.exceptions.NotAcceptableException;
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


/**
 * Rest controller for sale listings.
 */
@RestController
public class SaleListingController {

    private static final Logger logger = LoggerFactory.getLogger(SaleListingController.class.getName());


    private final SaleListingService saleListingService;

    @Autowired
    public SaleListingController(
            SaleListingService saleListingService) {
        this.saleListingService = saleListingService;
    }

    /**
     * Searches all sale listings by supplied parameters
     *
     * @param searchQuery              query to search by
     * @param matchingProductName      whether you want to search by product name
     * @param matchingBusinessName     whether you want to search by business name
     * @param matchingBusinessLocation whether you want to search by business location
     * @param priceRangeLower          the lower price range (can be null)
     * @param priceRangeUpper          the upper price range (can be null)
     * @param closingDateLower         the lower closing date range (can be null)
     * @param closingDateUpper         the upper closing date range (can be null)
     * @param pageNumber               the page number to get
     * @param sortBy                   the sorting parameter
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
            @RequestParam("sortBy") String sortBy) {
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
     * Buys a sale listing acting as the logged in user.
     *
     * @param listingId Sales Listing to purchase
     * @param appUser   Logged in user to purchase the Sale Listing
     */
    @PostMapping("/listings/{listingId}/buy")
    public void buySaleListing(@PathVariable int listingId, @AuthenticationPrincipal AppUserDetails appUser) {
        try {
            saleListingService.buySaleListing(listingId, appUser);
        } catch (NotAcceptableException handledException) {
            logger.error(handledException.getMessage());
            throw handledException;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while buying sale listing: %s",
                    unhandledException.getMessage()));
            throw unhandledException;
        }
    }

    /**
     * Gets a list of sale listings for a business.
     *
     * @param businessId Business to get the sale listings from.
     * @param appUser    The user that made the request.
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
     *
     * @param businessId Business to get the sale listings from.
     * @param appUser    The user that made the request.
     */
    @PostMapping("/businesses/{businessId}/listings")
    @ResponseStatus(HttpStatus.CREATED)
    public void newBusinessListing(
            @PathVariable int businessId, @Valid @RequestBody PostSaleListingDTO requestDTO,
            @AuthenticationPrincipal AppUserDetails appUser) {
        saleListingService.newBusinessListing(requestDTO, businessId, appUser);
    }

    /**
     * Likes a sale listing,
     * @param listingId The sale listing ID the user is trying to like
     * @param appUser The user that is trying to like a sale listing
     */
    @PutMapping("/listings/{listingId}/like")
    @ResponseStatus(HttpStatus.OK)
    public void likeSaleListing(@PathVariable Integer listingId,
                                @AuthenticationPrincipal AppUserDetails appUser) {
        try {
            logger.info("Request to like a sale listing with ID: {}", listingId);
            saleListingService.likeSaleListing(listingId, appUser);
        } catch (NotAcceptableException | BadRequestException expectedException) {
            logger.info(expectedException.getMessage());
            throw expectedException;
        } catch (Exception exception) {
            logger.error(String.format("Unexpected error while liking sale listing : %s", exception.getMessage()));
            throw exception;
        }
    }

    /**
     * Unlikes a sale listing,
     * @param listingId The sale listing ID the user is trying to unlike
     * @param user The user that is trying to like a sale listing
     */
    @PatchMapping("/listings/{listingId}/unlike")
    @ResponseStatus(HttpStatus.OK)
    public void unlikeSaleListing(@PathVariable Integer listingId,
                                  @AuthenticationPrincipal AppUserDetails user) {
        try{
            logger.info("Request to unlike a sale listing with ID: {}", listingId);
            saleListingService.unlikeSaleListing(listingId, user);
        } catch (NotAcceptableException | BadRequestException expectedException) {
            logger.info(expectedException.getMessage());
            throw expectedException;
        } catch (Exception exception) {
            logger.error(String.format("Unexpected error while unliking sale listing : %s", exception.getMessage()));
            throw exception;
        }
    }

    /**
     * Handles request for a user to tag a sale listing
     * @param listingId the id of the listing to tag
     * @param requestBody request body containing the tag for the listing
     * @param user the AppUserDetails of the user tagging the listing
     */
    @PatchMapping("/listings/{listingId}/tag")
    @ResponseStatus(HttpStatus.OK)
    public void tagSaleListing(@PathVariable Integer listingId,
                               @RequestBody JSONObject requestBody,
                               @AuthenticationPrincipal AppUserDetails user) {
        String tag = requestBody.getAsString("tag");
        saleListingService.tagSaleListing(listingId, tag, user);
    }

    @PatchMapping("/listings/{listingId}/star")
    @ResponseStatus(HttpStatus.OK)
    public void starSaleListing(@PathVariable Integer listingId,
                                @RequestBody JSONObject requestBody,
                                @AuthenticationPrincipal AppUserDetails user) {
        boolean star;
        try{
            star = (boolean) requestBody.get("star");
        } catch (ClassCastException | NullPointerException exception) {
            String message = "Value of \"star\" must be a boolean";
            logger.warn(message);
            throw new BadRequestException(message);
        }
        saleListingService.starSaleListing(listingId, star, user);
    }
}
