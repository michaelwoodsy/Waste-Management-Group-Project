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
    private final UserRepository userRepository;

    @Autowired
    public ProductCatalogueController(
            BusinessRepository businessRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {
        this.businessRepository = businessRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    /**
     * Gets a list of products that belongs to a business.
     * @param businessId ID of the business to get the products of.
     * @param appUser AppUserDetails from spring security
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

            // Get the products for the businesses
            return productRepository.findAllByBusinessId(businessId);

        } catch (NoBusinessExistsException | ForbiddenAdministratorActionException handledException) {
            throw handledException;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while getting business products: %s",
                    unhandledException.getMessage()));
            throw unhandledException;
        }
    }


    /**
     *
     * @param businessId ID of the business to add product to.
     * @param appUser AppUserDetails of current user
     * @param json The fields of the new product
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
            Double recommendedRetailPrice = (Double) json.getAsNumber("recommendedRetailPrice");

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

        } catch (NoBusinessExistsException | ForbiddenAdministratorActionException | MissingProductIdException |
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

            // Get the product
            Optional<Product> productResult = productRepository.findById(productId);

            // Check if the product exists
            if (productResult.isEmpty()) {
                NoProductExistsException exception = new NoProductExistsException(productId, businessId);
                logger.error(exception.getMessage());
                throw exception;
            }
            Product product = productResult.get();
            Product originalProduct = productResult.get();

            //Edit fields if they are sent
            //Name
            String newName = json.getAsString("name");
            if(newName != null) {
                product.setName(newName);
            }
            //Description
            String newDescription = json.getAsString("description");
            if(newDescription != null) {
                product.setDescription(newDescription);
            }
            //Manufacturer
            String newManufacturer = json.getAsString("manufacturer");
            if(newManufacturer != null) {
                product.setManufacturer(newManufacturer);
            }
            //Recommended Retail Price
            try {
                Number newRRP = json.getAsNumber("recommendedRetailPrice");
                if(newRRP != null) {
                    product.setRecommendedRetailPrice(newRRP.doubleValue());
                }
            } catch(NumberFormatException e) {
                IncorrectRRPFormatException exception = new IncorrectRRPFormatException();
                logger.error(exception.getMessage());
                throw exception;
            }

            //Id
            String newId = json.getAsString("id");
            if(newId != null) {
                //Return 400 if id not unique
                if (productRepository.findById(newId).isPresent()) {
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
                //Need to remove original product as you cant change the id
                productRepository.delete(originalProduct);
                product.setId(newId);
            }

            //Save edited product
            productRepository.save(product);

        } catch (NoBusinessExistsException | NoProductExistsException |
                IncorrectRRPFormatException | ForbiddenAdministratorActionException |
                ProductIdAlreadyExistsException | InvalidProductIdCharactersException handledException) {
            throw handledException;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while adding business product: %s",
                    unhandledException.getMessage()));
            throw unhandledException;
        }
    }

}
