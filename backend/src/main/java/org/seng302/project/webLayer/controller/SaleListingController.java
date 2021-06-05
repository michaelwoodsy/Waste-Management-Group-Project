package org.seng302.project.webLayer.controller;

import net.minidev.json.JSONObject;
import org.seng302.project.repositoryLayer.model.*;
import org.seng302.project.repositoryLayer.repository.BusinessRepository;
import org.seng302.project.repositoryLayer.repository.InventoryItemRepository;
import org.seng302.project.repositoryLayer.repository.SaleListingRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.exceptions.*;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.ForbiddenAdministratorActionException;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
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
    private final InventoryItemRepository inventoryItemRepository;

    @Autowired
    public SaleListingController(
            BusinessRepository businessRepository,
            SaleListingRepository saleListingRepository,
            UserRepository userRepository,
            InventoryItemRepository inventoryItemRepository) {
        this.businessRepository = businessRepository;
        this.saleListingRepository = saleListingRepository;
        this.userRepository = userRepository;
        this.inventoryItemRepository = inventoryItemRepository;
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
     * Gets a list of sale listings for a business.
     * @param businessId Business to get the sale listings from.
     * @param appUser The user that made the request.
     * @return List of sale listings.
     */
    @GetMapping("/businesses/{businessId}/listings")
    public List<SaleListing> getBusinessesListings(
            @PathVariable int businessId,
            @AuthenticationPrincipal AppUserDetails appUser) {
        try {
            // Get the user that made the request
            User user = getLoggedInUser(appUser);

            logger.info("User with id " + user.getId() +
                    " trying to get sale listings of business with id " + businessId  + ".");

            // Get the business of the request
            Business business = getBusiness(businessId);

            // Get the sale listings of the business
            return saleListingRepository.findAllByBusinessId(businessId);

        } catch (NoBusinessExistsException | ForbiddenAdministratorActionException exception) {
            throw exception;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while getting business sale listings: %s",
                    unhandledException.getMessage()));
            throw unhandledException;
        }
    }

    /**
     * Adds a new sale listing to a business.
     * @param businessId Business to get the sale listings from.
     * @param appUser The user that made the request.
     */
    @PostMapping("/businesses/{businessId}/listings")
    @ResponseStatus(HttpStatus.CREATED)
    public void newBusinessesListing(
            @PathVariable int businessId, @RequestBody JSONObject json,
            @AuthenticationPrincipal AppUserDetails appUser) {
        try {

            // Get the user that made the request
            User user = getLoggedInUser(appUser);

            logger.info("User with id " + user.getId() +
                    " trying to get sale listings of business with id " + businessId  + ".");

            // Get the business of the request
            Business business = getBusiness(businessId);

            // Check the user is an admin of the business
            checkUserIsAdminOfBusiness(user, business);

            //Inventory item
            if (json.getAsNumber("inventoryItemId") == null) { //Not supplied
                MissingInventoryItemIdException exception = new MissingInventoryItemIdException();
                logger.warn(exception.getMessage());
                throw exception;
            }
            Integer inventoryItemId = json.getAsNumber("inventoryItemId").intValue();
            //Check if inventory item exists in businesses inventory items
            Optional<InventoryItem> retrievedItemOptions = inventoryItemRepository.findById(inventoryItemId);
            if (retrievedItemOptions.isEmpty()) {
                NoInventoryItemExistsException exception = new NoInventoryItemExistsException(inventoryItemId, businessId);
                logger.warn(exception.getMessage());
                throw exception;
            }
            InventoryItem item = retrievedItemOptions.get();

            //Quantity
            int quantity;
            try {
                if (json.getAsNumber("quantity") != null) {
                    quantity = json.getAsNumber("quantity").intValue();
                    //If quantity is at or below 0
                    if (quantity <= 0) {
                        InvalidQuantityException exception = new InvalidQuantityException();
                        logger.warn(exception.getMessage());
                        throw exception;
                    }
                } else {
                    InvalidQuantityException exception = new InvalidQuantityException();
                    logger.warn(exception.getMessage());
                    throw exception;
                }
                List<SaleListing> listings = saleListingRepository.findAllByBusinessIdAndInventoryItemId(businessId, inventoryItemId);

                //Calculates the quantity used of this Inventory item in other sales listings, if any
                Integer quantityUsed = 0;
                for(SaleListing listing: listings) {
                    quantityUsed += listing.getQuantity();
                }
                //Check if there is enough of the inventory item
                if (quantity > (item.getQuantity() - quantityUsed)) {
                    NotEnoughOfInventoryItemException exception = new NotEnoughOfInventoryItemException(inventoryItemId, item.getQuantity() - quantityUsed, quantityUsed);
                    logger.warn(exception.getMessage());
                    throw exception;
                }

            } catch (NullPointerException nullPointerException) { //Field not in json
                InvalidQuantityException exception = new InvalidQuantityException();
                logger.warn(exception.getMessage());
                throw exception;
            } catch (NumberFormatException numberFormatException) { //Field is not a number
                InvalidNumberFormatException exception = new InvalidNumberFormatException("quantity");
                logger.warn(exception.getMessage());
                throw exception;
            }

            //Price
            double price;
            try {
                if (json.getAsNumber("price") != null) {
                    price = json.getAsNumber("price").doubleValue();
                    //If price per item is below 0
                    if (price < 0) {
                        InvalidPriceException exception = new InvalidPriceException("price");
                        logger.warn(exception.getMessage());
                        throw exception;
                    }
                } else {
                    MissingPriceException exception = new MissingPriceException();
                    logger.warn(exception.getMessage());
                    throw exception;
                }
            } catch (NumberFormatException numberFormatException) { //Field is not a number
                InvalidNumberFormatException exception = new InvalidNumberFormatException("price");
                logger.warn(exception.getMessage());
                throw exception;
            }

            //More Info
            String moreInfo = json.getAsString("moreInfo");

            //Closes
            //Closes string should be in the format: "yyyy-mm-ddThh:mm:ss.sssZ", e.g: "2021-05-29T04:34:55.931Z"
            String closesDateString = json.getAsString("closes");
            LocalDateTime closesDateTime;
            try {
                if (closesDateString != null && !closesDateString.equals("")) {
                    closesDateTime = LocalDateTime.parse(closesDateString, DateTimeFormatter.ISO_DATE_TIME);

                    //Check if closes date is in the past
                    if ((LocalDateTime.now()).isAfter(closesDateTime)) {
                        InvalidClosesDateException exception = new InvalidClosesDateException();
                        logger.warn(exception.getMessage());
                        throw exception;
                    }
                } else {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date expiryDate = formatter.parse(item.getExpires());
                    closesDateTime = expiryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                }
            } catch (DateTimeParseException | ParseException parseException) {
                InvalidDateException invalidDateException = new InvalidDateException();
                logger.warn(invalidDateException.getMessage());
                throw invalidDateException;
            } catch (InvalidClosesDateException handledException) {
                throw handledException;
            } catch (Exception exception) {
                logger.error(String.format("Unexpected error while parsing date: %s", exception.getMessage()));
                throw exception;
            }
            System.out.println(closesDateTime);

            SaleListing saleListing = new SaleListing(businessId, item, price, moreInfo, closesDateTime, quantity);
            saleListingRepository.save(saleListing);

        } catch (NoBusinessExistsException | ForbiddenAdministratorActionException | NotEnoughOfInventoryItemException |
                MissingInventoryItemIdException | NoInventoryItemExistsException |
                InvalidQuantityException | InvalidNumberFormatException | InvalidPriceException |
                MissingPriceException | InvalidClosesDateException | InvalidDateException exception) {
            throw exception;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while adding sales listing: %s",
                    unhandledException.getMessage()));
            throw unhandledException;
        }
    }

}
