package org.seng302.project.serviceLayer.service;

import org.seng302.project.repositoryLayer.model.Business;
import org.seng302.project.repositoryLayer.model.InventoryItem;
import org.seng302.project.repositoryLayer.model.Product;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.BusinessRepository;
import org.seng302.project.repositoryLayer.repository.InventoryItemRepository;
import org.seng302.project.repositoryLayer.repository.ProductRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.dto.AddProductDTO;
import org.seng302.project.serviceLayer.dto.EditProductDTO;
import org.seng302.project.serviceLayer.exceptions.*;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.ForbiddenAdministratorActionException;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductCatalogueService {
    private static final Logger logger = LoggerFactory.getLogger(ProductCatalogueService.class.getName());

    private final BusinessRepository businessRepository;
    private final ProductRepository productRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ProductCatalogueService(UserRepository userRepository,
                               BusinessRepository businessRepository,
                               ProductRepository productRepository,
                                   InventoryItemRepository inventoryItemRepository) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.productRepository = productRepository;
        this.inventoryItemRepository = inventoryItemRepository;
    }

    /**
     * Gets a list of products that belongs to a business.
     *
     * @param businessId ID of the business to get the products of.
     * @param appUser    AppUserDetails from spring security
     * @return List of products that belongs to the business.
     */
    public List<Product> getBusinessesProducts(Integer businessId, AppUserDetails appUser) {
        try {
            // Get the logged in user from the users email
            String userEmail = appUser.getUsername();
            User loggedInUser = userRepository.findByEmail(userEmail).get(0);

            logger.info("User with user id: {} getting products from business with ID: {}", loggedInUser.getId(), businessId );

            // Get the business
            Optional<Business> businessResult = businessRepository.findById(businessId);

            // Check if the business exists
            if (businessResult.isEmpty()) {
                NoBusinessExistsException exception = new NoBusinessExistsException(businessId);
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

        } catch (NoBusinessExistsException | ForbiddenAdministratorActionException handledException) {
            throw handledException;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while getting business products: %s",
                    unhandledException.getMessage()));
            throw unhandledException;
        }
    }

    /**
     * @param requestDTO       The DTO for this request
     */
    public void newProduct(AddProductDTO requestDTO) {
        try {
            // Get the logged in user from the user's email
            String userEmail = requestDTO.getAppUser().getUsername();
            User loggedInUser = userRepository.findByEmail(userEmail).get(0);

            logger.info("User with user id: {} Adding product to business with ID: {}"
                  , loggedInUser.getId(), requestDTO.getBusinessId());

            // Get the business
            Optional<Business> businessResult = businessRepository.findById(requestDTO.getBusinessId());

            // Check if the business exists
            if (businessResult.isEmpty()) {
                NoBusinessExistsException exception = new NoBusinessExistsException(requestDTO.getBusinessId());
                logger.error(exception.getMessage());
                throw exception;
            }
            Business business = businessResult.get();

            // Check if the logged in user is the business owner / administrator or a GAA
            if (!(business.userIsAdmin(loggedInUser.getId()) ||
                    business.getPrimaryAdministratorId().equals(loggedInUser.getId())) && !loggedInUser.isGAA()) {
                ForbiddenAdministratorActionException exception = new ForbiddenAdministratorActionException(
                        requestDTO.getBusinessId());
                logger.error(exception.getMessage());
                throw exception;
            }

            //These have been checked to be not empty by the DTO
            String productId = requestDTO.getId();
            String name = requestDTO.getName();

            //These can be empty
            String description = requestDTO.getDescription();
            String manufacturer = requestDTO.getManufacturer();
            Double recommendedRetailPrice = null;
            if (requestDTO.getRecommendedRetailPrice() != null) {
                recommendedRetailPrice = requestDTO.getRecommendedRetailPrice();
                //If Recommended Retail Price is below 0
                if (recommendedRetailPrice < 0) {
                    InvalidPriceException exception = new InvalidPriceException("recommended retail price");
                    logger.error(exception.getMessage());
                    throw exception;
                }
            }

            //Return 400 if id not unique
            if (productRepository.findByIdAndBusinessId(productId, requestDTO.getBusinessId()).isPresent()) {
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

            Product product = new Product(productId, name, description, manufacturer, recommendedRetailPrice, requestDTO.getBusinessId());
            productRepository.save(product);

        } catch (NoBusinessExistsException | ForbiddenAdministratorActionException | InvalidPriceException
                | ProductIdAlreadyExistsException  | InvalidProductIdCharactersException handledException) {
            throw handledException;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while adding business product: %s",
                    unhandledException.getMessage()));
            throw unhandledException;
        }
    }


    /**
     * Edits product with id productId
     * @param requestDTO The DTO for this request
     */
    public void editProduct(EditProductDTO requestDTO) {
        try {
            // Get the logged in user from the user's email
            String userEmail = requestDTO.getAppUser().getUsername();
            User loggedInUser = userRepository.findByEmail(userEmail).get(0);

            logger.info("User with user id: {} Editing product with id '{}' from business with id {}",
                    loggedInUser.getId(), requestDTO.getProductId(), requestDTO.getBusinessId());

            // Get the business
            Optional<Business> businessResult = businessRepository.findById(requestDTO.getBusinessId());

            // Check if the business exists
            if (businessResult.isEmpty()) {
                NoBusinessExistsException exception = new NoBusinessExistsException(requestDTO.getBusinessId());
                logger.error(exception.getMessage());
                throw exception;
            }
            Business business = businessResult.get();

            // Check if the logged in user is the business owner / administrator or a GAA
            if (!(business.userIsAdmin(loggedInUser.getId()) ||
                    business.getPrimaryAdministratorId().equals(loggedInUser.getId())) && !loggedInUser.isGAA()) {
                ForbiddenAdministratorActionException exception = new ForbiddenAdministratorActionException(requestDTO.getBusinessId());
                logger.error(exception.getMessage());
                throw exception;
            }

            // Get the product
            Optional<Product> productResult = productRepository.findByIdAndBusinessId(requestDTO.getProductId(), requestDTO.getBusinessId());

            // Check if the product exists
            if (productResult.isEmpty()) {
                NoProductExistsException exception = new NoProductExistsException(requestDTO.getProductId(), requestDTO.getBusinessId());
                logger.error(exception.getMessage());
                throw exception;
            }

            Product product = productResult.get();
            Product originalProduct = productResult.get();

            String newName = requestDTO.getName();
            String newDescription = requestDTO.getDescription();
            String newManufacturer = requestDTO.getManufacturer();
            Number newNumberRRP = requestDTO.getRecommendedRetailPrice();
            Double newRRP = originalProduct.getRecommendedRetailPrice();
            String newId = requestDTO.getId();

            //Edit fields if they are sent

            //Name
            if(requestDTO.getName() != null && !originalProduct.getName().equals(newName)) {
                //Has been checked to be non empty by the DTO
                product.setName(newName);
            }

            //Description
            if(requestDTO.getDescription() != null && !originalProduct.getDescription().equals(newDescription)) {
                product.setDescription(newDescription);
            }

            //Manufacturer
            if(requestDTO.getManufacturer() != null && !originalProduct.getManufacturer().equals(newManufacturer)) {
                product.setManufacturer(newManufacturer);
            }

            //Recommended Retail Price
            try {
                if (requestDTO.getRecommendedRetailPrice() != null) {
                    if (newNumberRRP == null) {
                        product.setRecommendedRetailPrice(null);
                        newRRP = null;
                    } else if (originalProduct.getRecommendedRetailPrice() == null ||
                            originalProduct.getRecommendedRetailPrice() != newNumberRRP.doubleValue()) {
                        newRRP = requestDTO.getRecommendedRetailPrice();
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
            if(requestDTO.getId() != null && !originalProduct.getId().equals(newId)) {
                //Id has been checked to be non empty by the DTO

                //Return 400 if id not unique
                if (newId.equals(originalProduct.getId()) || !(originalProduct.getId().equals(newId)) &&
                        productRepository.findByIdAndBusinessId(newId, requestDTO.getBusinessId()).isPresent()) {
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
                Product newProduct = new Product(newId, newName, newDescription, newManufacturer, newRRP, requestDTO.getBusinessId());
                //Save new product
                productRepository.save(newProduct);

                // Get inventory items
                List<InventoryItem> inventoryItems = inventoryItemRepository.findAllByBusinessId(requestDTO.getBusinessId());
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

        } catch (NoBusinessExistsException | IncorrectRRPFormatException | ForbiddenAdministratorActionException |
                ProductIdAlreadyExistsException | InvalidProductIdCharactersException handledException) {
            throw handledException;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while adding business product: %s",
                    unhandledException.getMessage()));
            throw unhandledException;
        }

    }

}
