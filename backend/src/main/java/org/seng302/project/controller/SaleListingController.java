package org.seng302.project.controller;

import net.minidev.json.JSONObject;
import org.seng302.project.controller.authentication.AppUserDetails;
import org.seng302.project.exceptions.*;
import org.seng302.project.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
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
        try {
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
                logger.error(exception.getMessage());
                throw exception;
            }
            Integer inventoryItemId = json.getAsNumber("inventoryItemId").intValue();
            //Check if inventory item exists in businesses inventory items
            Optional<InventoryItem> retrievedItemOptions = inventoryItemRepository.findById(inventoryItemId);
            if (retrievedItemOptions.isEmpty()) {
                NoInventoryItemExistsException exception = new NoInventoryItemExistsException(inventoryItemId, businessId);
                logger.error(exception.getMessage());
                throw exception;
            }
            InventoryItem item = retrievedItemOptions.get();

            //Quantity
            Integer quantity = null;
            try {
                if (json.getAsNumber("quantity") != null) {
                    quantity = json.getAsNumber("quantity").intValue();
                    //If quantity is at or below 0
                    if (quantity <= 0) {
                        InvalidQuantityException exception = new InvalidQuantityException();
                        logger.error(exception.getMessage());
                        throw exception;
                    }
                } else {
                    InvalidQuantityException exception = new InvalidQuantityException();
                    logger.error(exception.getMessage());
                    throw exception;
                }
                //Check if there is enough of the inventory item
                if (quantity > item.getQuantity()) {
                    NotEnoughOfInventoryItemException exception = new NotEnoughOfInventoryItemException(inventoryItemId, item.getQuantity());
                    logger.error(exception.getMessage());
                    throw exception;
                }

            } catch (NullPointerException nullPointerException) { //Field not in json
                InvalidQuantityException exception = new InvalidQuantityException();
                logger.error(exception.getMessage());
                throw exception;
            } catch (NumberFormatException numberFormatException) { //Field is not a number
                InvalidNumberFormatException exception = new InvalidNumberFormatException("quantity");
                logger.error(exception.getMessage());
                throw exception;
            }

            //Price
            Double price = null;
            try {
                if (json.getAsNumber("price") != null) {
                    price = json.getAsNumber("price").doubleValue();
                    //If price per item is below 0
                    if (price < 0) {
                        InvalidPriceException exception = new InvalidPriceException("price");
                        logger.error(exception.getMessage());
                        throw exception;
                    }
                }
            } catch (NumberFormatException numberFormatException) { //Field is not a number
                InvalidNumberFormatException exception = new InvalidNumberFormatException("price");
                logger.error(exception.getMessage());
                throw exception;
            }

            //More Info
            String moreInfo = json.getAsString("moreInfo");

            //Closes
            String closesDateString = json.getAsString("closes");
            LocalDateTime closesDate;
            try {
                if (closesDateString != null && !closesDateString.equals("")) {
                    closesDate = LocalDateTime.parse(closesDateString);

                    //Check if closes date is in the past
                    if ((LocalDateTime.now()).isAfter(closesDate)) {
                        InvalidManufactureDateException exception = new InvalidManufactureDateException();
                        logger.warn(exception.getMessage());
                        throw exception;
                    }
                } else {
                    closesDate = LocalDateTime.parse(item.getExpires());
                }
            } catch (DateTimeParseException dateTimeParseException) {
                InvalidDateException invalidDateException = new InvalidDateException();
                logger.warn(invalidDateException.getMessage());
                throw invalidDateException;
            } catch (InvalidClosesDateException handledException) {
                throw handledException;
            } catch (Exception exception) {
                logger.error(String.format("Unexpected error while parsing date: %s", exception.getMessage()));
                throw exception;
            }
            SaleListing saleListing = new SaleListing(businessId, item, price, moreInfo, closesDate, quantity);

        } catch (NoBusinessExistsException | ForbiddenAdministratorActionException |
                MissingInventoryItemIdException | NoInventoryItemExistsException |
                InvalidQuantityException | InvalidNumberFormatException | InvalidPriceException exception) {
            throw exception;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while getting business sale listings: %s",
                    unhandledException.getMessage()));
            throw unhandledException;
        }
    }

}
