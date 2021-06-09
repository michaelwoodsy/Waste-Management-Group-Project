package org.seng302.project.web_layer.controller;

import net.minidev.json.JSONObject;
import org.seng302.project.repository_layer.model.*;
import org.seng302.project.repository_layer.repository.BusinessRepository;
import org.seng302.project.repository_layer.repository.InventoryItemRepository;
import org.seng302.project.repository_layer.repository.ProductRepository;
import org.seng302.project.repository_layer.repository.UserRepository;
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

import java.util.List;
import java.util.Optional;

/**
 * Rest controller for products.
 */
@RestController
public class ProductCatalogueController {

    private static final Logger logger = LoggerFactory.getLogger(ProductCatalogueController.class.getName());
    private final BusinessRepository businessRepository;
    private final ProductRepository productRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ProductCatalogueController(
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
     * Gets a list of products that belongs to a business.
     *
     * @param businessId ID of the business to get the products of.
     * @param appUser    AppUserDetails from spring security
     * @return List of products that belongs to the business.
     */
    @GetMapping("/businesses/{businessId}/products")
    public List<Product> getBusinessesProducts(@PathVariable int businessId, @AuthenticationPrincipal AppUserDetails appUser) {

        try {
            // Get the logged in user from the users email
            String userEmail = appUser.getUsername();
            User loggedInUser = userRepository.findByEmail(userEmail).get(0);

            logger.info("User with user id: " + loggedInUser.getId() + " Getting business with ID: " + businessId + " products");

            // Get the business
            Optional<Business> businessResult = businessRepository.findById(businessId);

            // Check if the business exists
            if (businessResult.isEmpty()) {
                BusinessNotFoundException exception = new BusinessNotFoundException(businessId);
                logger.error(exception.getMessage());
                throw exception;
            }
            Business business = businessResult.get();

            // Check if the logged in user is the business owner / administrator or a GAA
            if (!(business.userIsAdmin(loggedInUser.getId()) ||
                    business.getPrimaryAdministratorId().equals(loggedInUser.getId())) && !loggedInUser.isGAA()) {
                ForbiddenAdministratorActionException exception = new ForbiddenAdministratorActionException(businessId);
                logger.error(exception.getMessage());
                throw exception;
            }

            // Get the products for the businesses
            return productRepository.findAllByBusinessId(businessId);

        } catch (BusinessNotFoundException | ForbiddenAdministratorActionException handledException) {
            throw handledException;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while getting business products: %s",
                    unhandledException.getMessage()));
            throw unhandledException;
        }
    }


    /**
     * @param businessId ID of the business to add product to.
     * @param appUser    AppUserDetails of current user
     * @param json       The fields of the new product
     */
    @PostMapping("/businesses/{businessId}/products")
    @ResponseStatus(HttpStatus.CREATED)
    public void newProduct(@PathVariable int businessId, @RequestBody JSONObject json, @AuthenticationPrincipal AppUserDetails appUser) {

        try {
            // Get the logged in user from the user's email
            String userEmail = appUser.getUsername();
            User loggedInUser = userRepository.findByEmail(userEmail).get(0);

            logger.info("User with user id: " + loggedInUser.getId() + " Adding product to business with ID: " + businessId);

            // Get the business
            Optional<Business> businessResult = businessRepository.findById(businessId);

            // Check if the business exists
            if (businessResult.isEmpty()) {
                BusinessNotFoundException exception = new BusinessNotFoundException(businessId);
                logger.error(exception.getMessage());
                throw exception;
            }
            Business business = businessResult.get();

            // Check if the logged in user is the business owner / administrator or a GAA
            if (!(business.userIsAdmin(loggedInUser.getId()) ||
                    business.getPrimaryAdministratorId().equals(loggedInUser.getId())) && !loggedInUser.isGAA()) {
                ForbiddenAdministratorActionException exception = new ForbiddenAdministratorActionException(businessId);
                logger.error(exception.getMessage());
                throw exception;
            }

            String productId = json.getAsString("id");
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

            String name = json.getAsString("name");
            try {
                if (name.isEmpty()) { //Empty string
                    MissingProductNameException exception = new MissingProductNameException();
                    logger.error(exception.getMessage());
                    throw exception;
                }
            } catch (NullPointerException nullPointerException) { //Field not in json
                MissingProductNameException exception = new MissingProductNameException();
                logger.error(exception.getMessage());
                throw exception;
            }

            //These can be empty
            String description = json.getAsString("description");
            String manufacturer = json.getAsString("manufacturer");
            Double recommendedRetailPrice = null;
            if (json.getAsNumber("recommendedRetailPrice") != null) {
                recommendedRetailPrice = json.getAsNumber("recommendedRetailPrice").doubleValue();
                //If Recommended Retail Price is below 0
                if (recommendedRetailPrice < 0) {
                    InvalidPriceException exception = new InvalidPriceException("recommended retail price");
                    logger.error(exception.getMessage());
                    throw exception;
                }
            }

            //Return 400 if id not unique
            if (productRepository.findByIdAndBusinessId(productId, businessId).isPresent()) {
                ProductIdAlreadyExistsException exception = new ProductIdAlreadyExistsException();
                logger.warn(exception.getMessage());
                throw exception;
            }

            //Return 400 if id contains characters other than: letters, numbers, dashes
            String productIdRegEx = "^[a-zA-Z0-9\\-^\\S]+$";
            if (!productId.matches(productIdRegEx)) {
                InvalidProductIdCharactersException exception = new InvalidProductIdCharactersException();
                logger.warn(exception.getMessage());
                throw exception;
            }

            Product product = new Product(productId, name, description, manufacturer, recommendedRetailPrice, businessId);
            productRepository.save(product);

        } catch (BusinessNotFoundException | ForbiddenAdministratorActionException | MissingProductIdException |
                MissingProductNameException | ProductIdAlreadyExistsException handledException) {
            throw handledException;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while adding business product: %s",
                    unhandledException.getMessage()));
            throw unhandledException;
        }
    }


    /**
     * Edits product with id productId
     * @param businessId ID of the business the product is under.
     * @param productId ID of the product
     * @param appUser AppUserDetails of current user
     * @param json The fields of the product to edit
     */
    @PutMapping("/businesses/{businessId}/products/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public void editProduct(@PathVariable int businessId, @PathVariable String productId, @RequestBody JSONObject json, @AuthenticationPrincipal AppUserDetails appUser) {
        try {
            // Get the logged in user from the user's email
            String userEmail = appUser.getUsername();
            User loggedInUser = userRepository.findByEmail(userEmail).get(0);

            logger.info("User with user id: " + loggedInUser.getId() + " Editing product with id '" + productId + "' from business with id " + businessId);

            // Get the business
            Optional<Business> businessResult = businessRepository.findById(businessId);

            // Check if the business exists
            if (businessResult.isEmpty()) {
                BusinessNotFoundException exception = new BusinessNotFoundException(businessId);
                logger.error(exception.getMessage());
                throw exception;
            }
            Business business = businessResult.get();

            // Check if the logged in user is the business owner / administrator or a GAA
            if (!(business.userIsAdmin(loggedInUser.getId()) ||
                    business.getPrimaryAdministratorId().equals(loggedInUser.getId())) && !loggedInUser.isGAA()) {
                ForbiddenAdministratorActionException exception = new ForbiddenAdministratorActionException(businessId);
                logger.error(exception.getMessage());
                throw exception;
            }

            // Get the product
            Optional<Product> productResult = productRepository.findByIdAndBusinessId(productId, businessId);

            // Check if the product exists
            if (productResult.isEmpty()) {
                NoProductExistsException exception = new NoProductExistsException(productId, businessId);
                logger.error(exception.getMessage());
                throw exception;
            }

            Product product = productResult.get();
            Product originalProduct = productResult.get();

            String newName = json.getAsString("name");
            String newDescription = json.getAsString("description");
            String newManufacturer = json.getAsString("manufacturer");
            Number newNumberRRP = json.getAsNumber("recommendedRetailPrice");
            Double newRRP = originalProduct.getRecommendedRetailPrice();
            String newId = json.getAsString("id");

            //Edit fields if they are sent

            //Name
            if(json.containsKey("name") && !originalProduct.getName().equals(newName)) {
                if (newName == null || newName.equals("")) {
                    MissingProductNameException exception = new MissingProductNameException();
                    logger.warn(exception.getMessage());
                    throw exception;
                }
                product.setName(newName);
            }

            //Description
            if(json.containsKey("description") && !originalProduct.getDescription().equals(newDescription)) {
                product.setDescription(newDescription);
            }

            //Manufacturer
            if(json.containsKey("manufacturer") && !originalProduct.getManufacturer().equals(newManufacturer)) {
                product.setManufacturer(newManufacturer);
            }

            //Recommended Retail Price
            try {
                if (json.containsKey("recommendedRetailPrice")) {
                    if (newNumberRRP == null) {
                        product.setRecommendedRetailPrice(null);
                        newRRP = null;
                    } else if (originalProduct.getRecommendedRetailPrice() == null ||
                            originalProduct.getRecommendedRetailPrice() != newNumberRRP.doubleValue()) {
                        newRRP = json.getAsNumber("recommendedRetailPrice").doubleValue();
                        //If Recommended Retail Price is below 0
                        if (newRRP < 0) {
                            InvalidPriceException exception = new InvalidPriceException("recommended retail price");
                            logger.error(exception.getMessage());
                            throw exception;
                        }
                        product.setRecommendedRetailPrice(newRRP);
                    }
                }

            } catch(NumberFormatException e) {
                IncorrectRRPFormatException exception = new IncorrectRRPFormatException();
                logger.error(exception.getMessage());
                throw exception;
            }

            //Id
            if(json.containsKey("id") && !originalProduct.getId().equals(newId)) {
                if (newId == null || newId.equals("")) {
                    MissingProductIdException exception = new MissingProductIdException();
                    logger.warn(exception.getMessage());
                    throw exception;
                }
                //Return 400 if id not unique
                if (newId.equals(originalProduct.getId()) || !(originalProduct.getId().equals(newId)) &&
                        productRepository.findByIdAndBusinessId(newId, businessId).isPresent()) {
                    ProductIdAlreadyExistsException exception = new ProductIdAlreadyExistsException();
                    logger.warn(exception.getMessage());
                    throw exception;
                }
                //Return 400 if id contains characters other than: letters, numbers, dashes
                String productIdRegEx = "^[a-zA-Z0-9\\-^\\S]+$";
                if (!newId.matches(productIdRegEx)) {
                    InvalidProductIdCharactersException exception = new InvalidProductIdCharactersException();
                    logger.warn(exception.getMessage());
                    throw exception;
                }
                //Create new product
                Product newProduct = new Product(newId, newName, newDescription, newManufacturer, newRRP, businessId);
                //Save new product
                productRepository.save(newProduct);

                // Get inventory items
                List<InventoryItem> inventoryItems = inventoryItemRepository.findAllByBusinessId(businessId);
                //Loop through businesses inventory items and change product of inventory item if it is the one that needs to be changed
                for (InventoryItem item: inventoryItems) {
                    //If item has old product
                    if (item.getProduct().getId().equals(originalProduct.getId())) {
                        item.setProduct(newProduct);
                        inventoryItemRepository.save(item);
                    }
                }
                //Need to remove original product as it has been replaced fully in the database
                productRepository.delete(originalProduct);
                product.setId(newId);
            } else {
                //Save edited product
                productRepository.save(product);
            }

        } catch (BusinessNotFoundException | NoProductExistsException | MissingProductIdException |
                MissingProductNameException | IncorrectRRPFormatException | ForbiddenAdministratorActionException |
                ProductIdAlreadyExistsException | InvalidProductIdCharactersException handledException) {
            throw handledException;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while adding business product: %s",
                    unhandledException.getMessage()));
            throw unhandledException;
        }
    }

}
