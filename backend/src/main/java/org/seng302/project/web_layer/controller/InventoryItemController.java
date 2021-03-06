package org.seng302.project.web_layer.controller;

import net.minidev.json.JSONObject;
import org.seng302.project.repository_layer.model.*;
import org.seng302.project.repository_layer.repository.*;
import org.seng302.project.service_layer.dto.inventory_item.GetInventoryItemDTO;
import org.seng302.project.service_layer.exceptions.*;
import org.seng302.project.service_layer.exceptions.business.BusinessNotFoundException;
import org.seng302.project.service_layer.exceptions.businessAdministrator.ForbiddenAdministratorActionException;
import org.seng302.project.service_layer.exceptions.product.NoProductExistsException;
import org.seng302.project.web_layer.authentication.AppUserDetails;
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
import java.util.stream.Collectors;

/**
 * Rest controller for products.
 */
@RestController
public class InventoryItemController {

    private static final Logger logger = LoggerFactory.getLogger(InventoryItemController.class.getName());
    private final BusinessRepository businessRepository;
    private final ProductRepository productRepository;
    private final SaleListingRepository saleListingRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final UserRepository userRepository;

    @Autowired
    public InventoryItemController(
            BusinessRepository businessRepository,
            ProductRepository productRepository,
            InventoryItemRepository inventoryItemRepository,
            SaleListingRepository saleListingRepository,
            UserRepository userRepository) {
        this.businessRepository = businessRepository;
        this.productRepository = productRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.saleListingRepository = saleListingRepository;
        this.userRepository = userRepository;
    }

    /**
     * REST Request to retrieve a business' inventory.
     *
     * @param businessId The ID of the business to get the inventory of.
     * @param appUser    The currently logged in user.
     * @return The inventory of the business in list form.
     */
    @GetMapping("/businesses/{businessId}/inventory")
    @ResponseStatus(HttpStatus.OK)
    public List<GetInventoryItemDTO> getInventory(@PathVariable Integer businessId, @AuthenticationPrincipal AppUserDetails appUser) {

        try {
            Business business = businessRepository.findById(businessId).orElse(null);
            User loggedInUser = userRepository.findByEmail(appUser.getUsername()).get(0);

            if (business == null) {
                BusinessNotFoundException noBusinessException = new BusinessNotFoundException(businessId);
                logger.warn(noBusinessException.getMessage());
                throw noBusinessException;
            } else if (!business.userIsAdmin(loggedInUser.getId()) && !loggedInUser.isGAA()) {
                ForbiddenAdministratorActionException notAdminException = new ForbiddenAdministratorActionException(businessId);
                logger.warn(notAdminException.getMessage());
                throw notAdminException;
            }
            List<InventoryItem> items = inventoryItemRepository.findAllByBusinessId(businessId);
            return items.stream().map(GetInventoryItemDTO::new).collect(Collectors.toList());

        } catch (BusinessNotFoundException | ForbiddenAdministratorActionException exception) {
            throw exception;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while getting business inventory: %s",
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
                BusinessNotFoundException exception = new BusinessNotFoundException(businessId);
                logger.warn(exception.getMessage());
                throw exception;
            }
            Business business = businessResult.get();

            // Check if the logged in user is the business owner / administrator or a GAA
            if (!(business.userIsAdmin(loggedInUser.getId()) ||
                    business.getPrimaryAdministratorId().equals(loggedInUser.getId())) && !loggedInUser.isGAA()) {
                ForbiddenAdministratorActionException exception = new ForbiddenAdministratorActionException(businessId);
                logger.warn(exception.getMessage());
                throw exception;
            }

            //Get product id
            String productId = json.getAsString("productId");
            try {
                if (productId.isEmpty()) { //Empty string
                    MissingProductIdException exception = new MissingProductIdException();
                    logger.warn(exception.getMessage());
                    throw exception;
                }
            } catch (NullPointerException nullPointerException) { //Field not in json
                MissingProductIdException exception = new MissingProductIdException();
                logger.warn(exception.getMessage());
                throw exception;
            }

            //Check if product exists in businesses product catalogue
            Optional<Product> retrievedProductOptions = productRepository.findByIdAndBusinessId(productId, businessId);
            if (retrievedProductOptions.isEmpty()) {
                NoProductExistsException exception = new NoProductExistsException(productId, businessId);
                logger.warn(exception.getMessage());
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
                        InvalidQuantityException exception = new InvalidQuantityException();
                        logger.warn(exception.getMessage());
                        throw exception;
                    }
                }
                if (quantity == null) { //Empty string
                    InvalidQuantityException exception = new InvalidQuantityException();
                    logger.warn(exception.getMessage());
                    throw exception;
                }
            } catch (NullPointerException nullPointerException) { //Field not in json
                MissingProductIdException exception = new MissingProductIdException();
                logger.warn(exception.getMessage());
                throw exception;
            } catch (NumberFormatException numberFormatException) { //Field is not a number
                InvalidNumberFormatException exception = new InvalidNumberFormatException("quantity");
                logger.warn(exception.getMessage());
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
                        logger.warn(exception.getMessage());
                        throw exception;
                    }
                }
            } catch (NumberFormatException numberFormatException) { //Field is not a number
                InvalidNumberFormatException exception = new InvalidNumberFormatException("price per item");
                logger.warn(exception.getMessage());
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
                        logger.warn(exception.getMessage());
                        throw exception;
                    }
                }
            } catch (NumberFormatException numberFormatException) { //Field is not a number
                InvalidNumberFormatException exception = new InvalidNumberFormatException("total price");
                logger.warn(exception.getMessage());
                throw exception;
            }

            InventoryItem inventoryItem = new InventoryItem(product, quantity, pricePerItem, totalPrice,
                    manufactureDateString, sellByDateString, bestBeforeDateString,
                    formatter.format(expiryDate));

            inventoryItemRepository.save(inventoryItem);
        } catch (BusinessNotFoundException | ForbiddenAdministratorActionException | MissingProductIdException |
                NoProductExistsException | MissingInventoryItemExpiryException |
                InvalidQuantityException | ItemExpiredException | InvalidDateException |
                InvalidManufactureDateException | InvalidSellByDateException | NumberFormatException |
                InvalidBestBeforeDateException handledException) {
            throw handledException;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while adding business inventory item: %s",
                    unhandledException.getMessage()));
            throw unhandledException;
        }
    }


    /**
     * Edits inventory item with id itemId
     *
     * @param businessId      ID of the business the product is under.
     * @param inventoryItemId ID of the inventory item
     * @param appUser         AppUserDetails of current user
     * @param json            The fields of the product to edit
     */
    @PutMapping("/businesses/{businessId}/inventory/{inventoryItemId}")
    @ResponseStatus(HttpStatus.OK)
    public void editInventoryItem(@PathVariable int businessId, @PathVariable int inventoryItemId,
                                  @RequestBody JSONObject json, @AuthenticationPrincipal AppUserDetails appUser) {
        try {
            // Get the logged in user from the user's email
            String userEmail = appUser.getUsername();
            User loggedInUser = userRepository.findByEmail(userEmail).get(0);

            logger.info("User with user id: " + loggedInUser.getId() + " Editing inventory item with id " +
                    inventoryItemId + " from business with id " + businessId);

            // Get the business
            Optional<Business> businessResult = businessRepository.findById(businessId);

            // Check if the business exists
            if (businessResult.isEmpty()) {
                BusinessNotFoundException exception = new BusinessNotFoundException(businessId);
                logger.warn(exception.getMessage());
                throw exception;
            }
            Business business = businessResult.get();

            // Check if the logged in user is the business owner / administrator or a GAA
            if (!(business.userIsAdmin(loggedInUser.getId()) ||
                    business.getPrimaryAdministratorId().equals(loggedInUser.getId())) && !loggedInUser.isGAA()) {
                ForbiddenAdministratorActionException exception = new ForbiddenAdministratorActionException(businessId);
                logger.warn(exception.getMessage());
                throw exception;
            }

            // Get the item
            Optional<InventoryItem> itemResult = inventoryItemRepository.findById(inventoryItemId);

            // Check if the item exists
            if (itemResult.isEmpty()) {
                NoInventoryItemExistsException exception = new NoInventoryItemExistsException(
                        inventoryItemId, businessId);
                logger.warn(exception.getMessage());
                throw exception;
            }

            InventoryItem item = itemResult.get();
            InventoryItem originalItem = itemResult.get();

            //Quantity
            try {
                if (json.containsKey("quantity")) {
                    Number newQuantity = json.getAsNumber("quantity");
                    if (newQuantity == null) {
                        InvalidQuantityException exception = new InvalidQuantityException();
                        logger.warn(exception.getMessage());
                        throw exception;
                    } else if (originalItem.getQuantity() != newQuantity.doubleValue()) {
                        //If Quantity is at or below 0
                        if (newQuantity.intValue() <= 0) {
                            InvalidQuantityException exception = new InvalidQuantityException();
                            logger.warn(exception.getMessage());
                            throw exception;
                        }
                        //Check if there is enough inventory items
                        //Calculates the quantity used of this Inventory item in other sales listings, if any
                        Integer quantityUsed = 0;
                        List<SaleListing> listings = saleListingRepository.findAllByBusinessIdAndInventoryItemId(businessId, inventoryItemId);
                        for(SaleListing listing: listings) {
                            quantityUsed += listing.getQuantity();
                        }
                        //Check if there is enough of the inventory item
                        if (newQuantity.intValue() < quantityUsed) {
                            NotEnoughOfInventoryItemException exception = new NotEnoughOfInventoryItemException(inventoryItemId, item.getQuantity() - quantityUsed, quantityUsed);
                            logger.warn(exception.getMessage());
                            throw exception;
                        }



                        item.setQuantity(newQuantity.intValue());
                    }
                }
            } catch (NumberFormatException e) {
                InvalidQuantityException exception = new InvalidQuantityException();
                logger.warn(exception.getMessage());
                throw exception;
            }
            //Price Per Item
            try {
                if (json.containsKey("pricePerItem")) {
                    Number newPPI = json.getAsNumber("pricePerItem");
                    if (newPPI == null) {
                        item.setPricePerItem(null);
                    } else if (originalItem.getPricePerItem() == null ||
                            originalItem.getPricePerItem() != newPPI.doubleValue()) {
                        //If Price per item is below 0
                        if (newPPI.doubleValue() < 0) {
                            InvalidPriceException exception = new InvalidPriceException("price per item");
                            logger.warn(exception.getMessage());
                            throw exception;
                        }
                        item.setPricePerItem(newPPI.doubleValue());
                    }
                }
            } catch (NumberFormatException e) {
                InvalidPriceException exception = new InvalidPriceException("price per item");
                logger.warn(exception.getMessage());
                throw exception;
            }
            //Total Price
            try {
                if (json.containsKey("totalPrice")) {
                    Number newTotalPrice = json.getAsNumber("totalPrice");
                    if (newTotalPrice == null) {
                        item.setTotalPrice(null);
                    } else if (originalItem.getTotalPrice() == null ||
                            originalItem.getTotalPrice() != newTotalPrice.doubleValue()) {
                        //If Total price is below 0
                        if (newTotalPrice.doubleValue() < 0) {
                            InvalidPriceException exception = new InvalidPriceException("total price");
                            logger.warn(exception.getMessage());
                            throw exception;
                        }
                        item.setTotalPrice(newTotalPrice.doubleValue());
                    }
                }
            } catch (NumberFormatException e) {
                InvalidPriceException exception = new InvalidPriceException("total price");
                logger.warn(exception.getMessage());
                throw exception;
            }

            //Dates
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date currentDate = new Date();
            try {
                //Manufacture date
                if (json.containsKey("manufactured")) {
                    String manufactureDateString = json.getAsString("manufactured");
                    if (manufactureDateString == null) {
                        item.setManufactured(null);
                    } else if (originalItem.getManufactured() == null || !originalItem.getManufactured().equals(manufactureDateString)) {
                        Date manufactureDate = formatter.parse(manufactureDateString);

                        //Manufacture date should be in the past
                        //Throw exception if manufacture date is in the future
                        if (currentDate.before(manufactureDate)) {
                            InvalidManufactureDateException exception = new InvalidManufactureDateException();
                            logger.warn(exception.getMessage());
                            throw exception;
                        }
                        item.setManufactured(formatter.format(manufactureDate));
                    }
                }
                //Sell by date
                if (json.containsKey("sellBy")) {
                    String sellByDateString = json.getAsString("sellBy");
                    if (sellByDateString == null) {
                        item.setSellBy(null);
                    } else if (originalItem.getSellBy() == null || !originalItem.getSellBy().equals(sellByDateString)) {
                        Date sellByDate = formatter.parse(sellByDateString);

                        //Sell by date should be in the future
                        //Throw exception if sell by date is in the past
                        if (currentDate.after(sellByDate)) {
                            InvalidSellByDateException exception = new InvalidSellByDateException();
                            logger.warn(exception.getMessage());
                            throw exception;
                        }
                        item.setSellBy(formatter.format(sellByDate));
                    }
                }
                //Best Before date
                if (json.containsKey("bestBefore")) {
                    String bestBeforeDateString = json.getAsString("bestBefore");
                    if (bestBeforeDateString == null) {
                        item.setBestBefore(null);
                    } else if (originalItem.getBestBefore() == null || !originalItem.getBestBefore().equals(bestBeforeDateString)) {
                        Date bestBeforeDate = formatter.parse(bestBeforeDateString);

                        //Best before date should be in the future
                        //Throw exception if best before date is in the past
                        if (currentDate.after(bestBeforeDate)) {
                            InvalidBestBeforeDateException exception = new InvalidBestBeforeDateException();
                            logger.warn(exception.getMessage());
                            throw exception;
                        }
                        item.setBestBefore(formatter.format(bestBeforeDate));
                    }
                }
                //Expiry date
                String expiryDateString = json.getAsString("expires");
                if (json.containsKey("expires") && !originalItem.getExpires().equals(expiryDateString)) {
                    if (expiryDateString == null) { //Expiry date is required
                        MissingInventoryItemExpiryException exception = new MissingInventoryItemExpiryException();
                        logger.warn(exception.getMessage());
                        throw exception;
                    } else if (!originalItem.getExpires().equals(expiryDateString)) {
                        Date expiryDate = formatter.parse(expiryDateString);
                        if (currentDate.after(expiryDate)) { //Expiry date should not be in the past
                            ItemExpiredException exception = new ItemExpiredException();
                            logger.warn(exception.getMessage());
                            throw exception;
                        }
                        item.setExpires(formatter.format(expiryDate));
                    }
                }
            } catch (ParseException parseException) {
                InvalidDateException invalidDateException = new InvalidDateException();
                logger.warn(invalidDateException.getMessage());
                throw invalidDateException;
            } catch (InvalidManufactureDateException | InvalidSellByDateException | ItemExpiredException |
                    InvalidBestBeforeDateException | MissingInventoryItemExpiryException handledException) {
                throw handledException;
            } catch (Exception exception) {
                logger.error(String.format("Unexpected error while parsing date: %s", exception.getMessage()));
                throw exception;
            }
            //ProductId
            String newProductId = json.getAsString("productId");
            if (json.containsKey("productId") && !originalItem.getProduct().getId().equals(newProductId)) {
                if (newProductId == null || newProductId.equals("")) {
                    MissingProductIdException exception = new MissingProductIdException();
                    logger.warn(exception.getMessage());
                    throw exception;
                }
                // Get the product
                Optional<Product> productResult = productRepository.findByIdAndBusinessId(newProductId, businessId);

                // Check if the product exists
                if (productResult.isEmpty()) {
                    NoProductExistsException exception = new NoProductExistsException(newProductId, businessId);
                    logger.warn(exception.getMessage());
                    throw exception;
                }
                Product product = productResult.get();
                item.setProduct(product);
            }
            inventoryItemRepository.save(item);
        } catch (BusinessNotFoundException | NoProductExistsException | MissingProductIdException |
                ForbiddenAdministratorActionException | InvalidPriceException | InvalidManufactureDateException |
                InvalidSellByDateException | ItemExpiredException | InvalidBestBeforeDateException |
                MissingInventoryItemExpiryException | InvalidQuantityException handledException) {
            throw handledException;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while editing inventory item: %s",
                    unhandledException.getMessage()));
            throw unhandledException;
        }
    }
}
