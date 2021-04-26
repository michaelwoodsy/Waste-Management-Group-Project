package org.seng302.project.controller;

import net.minidev.json.JSONObject;
import org.seng302.project.controller.authentication.AppUserDetails;
import org.seng302.project.exceptions.*;
import org.seng302.project.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Rest controller for products.
 */
@RestController
public class InventoryItemController {

    private static final Logger logger = LoggerFactory.getLogger(InventoryItemController.class.getName());
    private final BusinessRepository businessRepository;
    private final ProductRepository productRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final UserRepository userRepository;

    @Autowired
    public InventoryItemController(
            BusinessRepository businessRepository,
            ProductRepository productRepository,
            InventoryItemRepository inventoryItemRepository,
            UserRepository userRepository) {
        this.businessRepository = businessRepository;
        this.productRepository = productRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.userRepository = userRepository;
    }

    /**
     * REST Request to retrieve a business' inventory.
     *
     * @param businessId The ID of the business to get the inventory of.
     * @param appUser The currently logged in user.
     * @return The inventory of the business in list form.
     */
    @GetMapping("/businesses/{businessId}/inventory")
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryItem> getInventory(@PathVariable Integer businessId, @AuthenticationPrincipal AppUserDetails appUser) {

        try {
            Business business = businessRepository.findById(businessId).orElse(null);
            User loggedInUser = userRepository.findByEmail(appUser.getUsername()).get(0);

            if (business == null) {
                NoBusinessExistsException noBusinessException = new NoBusinessExistsException(businessId);
                logger.warn(noBusinessException.getMessage());
                throw noBusinessException;
            } else if (!business.userIsAdmin(loggedInUser.getId())) {
                ForbiddenAdministratorActionException notAdminException = new ForbiddenAdministratorActionException(businessId);
                logger.warn(notAdminException.getMessage());
                throw notAdminException;
            }

            return inventoryItemRepository.findAllByBusinessId(businessId);

        } catch (NoBusinessExistsException | ForbiddenAdministratorActionException exception) {
            throw exception;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while adding business inventory item: %s",
                    unhandledException.getMessage()));
            throw unhandledException;
        }

    }

    /**
     * @param businessId ID of the business to add inventory item to.
     * @param appUser    AppUserDetails of current user
     * @param json       The fields of the new inventory item
     */
    @PostMapping("/businesses/{businessId}/inventory")
    @ResponseStatus(HttpStatus.CREATED)
    public void newInventoryItem(@PathVariable int businessId, @RequestBody JSONObject json, @AuthenticationPrincipal AppUserDetails appUser) {

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            // Get the logged in user from the user's email
            String userEmail = appUser.getUsername();
            User loggedInUser = userRepository.findByEmail(userEmail).get(0);

            logger.info("User with user id: " + loggedInUser.getId() + " Adding inventory item to business with ID: " + businessId);

            // Get the business
            Optional<Business> businessResult = businessRepository.findById(businessId);

            // Check if the business exists
            if (businessResult.isEmpty()) {
                NoBusinessExistsException exception = new NoBusinessExistsException(businessId);
                logger.error(exception.getMessage());
                throw exception;
            }
            Business business = businessResult.get();

            // Check if the logged in user is the business owner / administrator
            if (!(business.userIsAdmin(loggedInUser.getId()) || business.getPrimaryAdministratorId().equals(loggedInUser.getId()))) {
                ForbiddenAdministratorActionException exception = new ForbiddenAdministratorActionException(businessId);
                logger.error(exception.getMessage());
                throw exception;
            }

            //Get product id
            String productId = json.getAsString("productId");
            try {
                if (productId.isEmpty()) { //Empty string
                    MissingProductIdException exception = new MissingProductIdException();
                    logger.error(exception.getMessage());
                    throw exception;
                }
            } catch (NullPointerException nullPointerException) { //Field not in json
                MissingProductIdException exception = new MissingProductIdException();
                logger.error(exception.getMessage());
                throw exception;
            }

            //Check if product exists in businesses product catalogue
            Optional<Product> retrievedProductOptions = productRepository.findByIdAndBusinessId(productId, businessId);
            if (retrievedProductOptions.isEmpty()) {
                NoProductExistsException exception = new NoProductExistsException(productId, businessId);
                logger.error(exception.getMessage());
                throw exception;
            }
            Product product = retrievedProductOptions.get();

            //Get quantity
            Integer quantity = null;
            try {
                if (json.getAsNumber("quantity") != null) {
                    quantity = json.getAsNumber("quantity").intValue();
                    //If quantity is at or below 0
                    if (quantity <= 0) {
                        InvalidInventoryItemQuantityException exception = new InvalidInventoryItemQuantityException();
                        logger.error(exception.getMessage());
                        throw exception;
                    }
                }
                if (quantity == null) { //Empty string
                    InvalidInventoryItemQuantityException exception = new InvalidInventoryItemQuantityException();
                    logger.error(exception.getMessage());
                    throw exception;
                }
            } catch (NullPointerException nullPointerException) { //Field not in json
                MissingProductIdException exception = new MissingProductIdException();
                logger.error(exception.getMessage());
                throw exception;
            } catch (NumberFormatException numberFormatException) { //Field is not a number
                InvalidNumberFormatException exception = new InvalidNumberFormatException("quantity");
                logger.error(exception.getMessage());
                throw exception;
            }

            //Get dates
            Date expiryDate;
            String manufactureDateString = json.getAsString("manufactured");
            String sellByDateString = json.getAsString("sellBy");
            String bestBeforeDateString = json.getAsString("bestBefore");
            Date currentDate = new Date();
            try {
                String expiryDateString = json.getAsString("expires");
                if (expiryDateString == null || expiryDateString.equals("")) {
                    MissingInventoryItemExpiryException exception = new MissingInventoryItemExpiryException();
                    logger.warn(exception.getMessage());
                    throw exception;
                }
                expiryDate = formatter.parse(expiryDateString);

                if (manufactureDateString != null && !manufactureDateString.equals("")) {
                    Date manufactureDate = formatter.parse(manufactureDateString);

                    //Check if manufacture date is in the past
                    if (currentDate.before(manufactureDate)) {
                        InvalidManufactureDateException exception = new InvalidManufactureDateException();
                        logger.warn(exception.getMessage());
                        throw exception;
                    }
                } else {
                    manufactureDateString = null;
                }

                if (sellByDateString != null && !sellByDateString.equals("")) {
                    Date sellByDate = formatter.parse(sellByDateString);

                    //Check if sell by date is in the future
                    if (currentDate.after(sellByDate)) {
                        InvalidSellByDateException exception = new InvalidSellByDateException();
                        logger.warn(exception.getMessage());
                        throw exception;
                    }
                } else {
                    sellByDateString = null;
                }

                if (bestBeforeDateString != null && !bestBeforeDateString.equals("")) {
                    Date bestBeforeDate = formatter.parse(bestBeforeDateString);

                    //Check if best before date is in the future
                    if (currentDate.after(bestBeforeDate)) {
                        InvalidBestBeforeDateException exception = new InvalidBestBeforeDateException();
                        logger.warn(exception.getMessage());
                        throw exception;
                    }
                } else {
                    bestBeforeDateString = null;
                }
            } catch (ParseException parseException) {
                InvalidDateException invalidDateException = new InvalidDateException();
                logger.warn(invalidDateException.getMessage());
                throw invalidDateException;
            } catch (InvalidManufactureDateException | InvalidSellByDateException |
                    InvalidBestBeforeDateException | MissingInventoryItemExpiryException handledException) {
                throw handledException;
            } catch (Exception exception) {
                logger.error(String.format("Unexpected error while parsing date: %s", exception.getMessage()));
                throw exception;
            }
            if (currentDate.after(expiryDate)) {
                ItemExpiredException exception = new ItemExpiredException();
                logger.warn(exception.getMessage());
                throw exception;
            }

            //Get price per item if it exists
            Double pricePerItem = null;
            try {
                if (json.getAsNumber("pricePerItem") != null) {
                    pricePerItem = json.getAsNumber("pricePerItem").doubleValue();
                    //If price per item is below 0
                    if (pricePerItem < 0) {
                        InvalidPriceException exception = new InvalidPriceException("price per item");
                        logger.error(exception.getMessage());
                        throw exception;
                    }
                }
            } catch (NumberFormatException numberFormatException) { //Field is not a number
                InvalidNumberFormatException exception = new InvalidNumberFormatException("price per item");
                logger.error(exception.getMessage());
                throw exception;
            }

            //Get total price if it exists
            Double totalPrice = null;
            try {
                if (json.getAsNumber("totalPrice") != null) {
                    totalPrice = json.getAsNumber("totalPrice").doubleValue();
                    //If total price is below 0
                    if (totalPrice < 0) {
                        InvalidPriceException exception = new InvalidPriceException("total price");
                        logger.error(exception.getMessage());
                        throw exception;
                    }
                }
            } catch (NumberFormatException numberFormatException) { //Field is not a number
                InvalidNumberFormatException exception = new InvalidNumberFormatException("total price");
                logger.error(exception.getMessage());
                throw exception;
            }

            InventoryItem inventoryItem = new InventoryItem(product, quantity, pricePerItem, totalPrice,
                    manufactureDateString, sellByDateString, bestBeforeDateString,
                    formatter.format(expiryDate));

            inventoryItemRepository.save(inventoryItem);
        } catch (NoBusinessExistsException | ForbiddenAdministratorActionException | MissingProductIdException |
                NoProductExistsException | MissingInventoryItemExpiryException |
                InvalidInventoryItemQuantityException | ItemExpiredException | InvalidDateException |
                InvalidManufactureDateException | InvalidSellByDateException | NumberFormatException |
                InvalidBestBeforeDateException handledException) {
            throw handledException;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while adding business inventory item: %s",
                    unhandledException.getMessage()));
            throw unhandledException;
        }
    }


}
