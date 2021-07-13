package org.seng302.project.serviceLayer.service;

import org.seng302.project.repositoryLayer.model.Business;
import org.seng302.project.repositoryLayer.model.InventoryItem;
import org.seng302.project.repositoryLayer.model.Product;
import org.seng302.project.repositoryLayer.repository.BusinessRepository;
import org.seng302.project.repositoryLayer.repository.InventoryItemRepository;
import org.seng302.project.repositoryLayer.repository.ProductRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.repositoryLayer.specification.ProductSpecifications;
import org.seng302.project.serviceLayer.dto.product.AddProductDTO;
import org.seng302.project.serviceLayer.dto.product.EditProductDTO;
import org.seng302.project.serviceLayer.dto.product.ProductSearchDTO;
import org.seng302.project.serviceLayer.exceptions.*;
import org.seng302.project.serviceLayer.exceptions.business.BusinessNotFoundException;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.ForbiddenAdministratorActionException;
import org.seng302.project.serviceLayer.exceptions.product.ProductNotFoundException;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
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
            var loggedInUser = userRepository.findByEmail(userEmail).get(0);

            logger.info("User with user id: {} getting products from business with ID: {}", loggedInUser.getId(), businessId );

            // Get the business
            Optional<Business> businessResult = businessRepository.findById(businessId);

            // Check if the business exists
            if (businessResult.isEmpty()) {
                var exception = new BusinessNotFoundException(businessId);
                logger.error(exception.getMessage());
                throw exception;
            }
            var business = businessResult.get();

            // Check if the logged in user is the business owner / administrator or a GAA
            if (!(business.userIsAdmin(loggedInUser.getId()) ||
                    business.getPrimaryAdministratorId().equals(loggedInUser.getId())) && !loggedInUser.isGAA()) {
                var exception = new ForbiddenAdministratorActionException(businessId);
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
     * @param requestDTO       The DTO for this request
     */
    public void newProduct(AddProductDTO requestDTO) {
        try {
            // Get the logged in user from the user's email
            String userEmail = requestDTO.getAppUser().getUsername();
            var loggedInUser = userRepository.findByEmail(userEmail).get(0);

            logger.info("User with user id: {} Adding product to business with ID: {}"
                  , loggedInUser.getId(), requestDTO.getBusinessId());

            // Get the business
            Optional<Business> businessResult = businessRepository.findById(requestDTO.getBusinessId());

            // Check if the business exists
            if (businessResult.isEmpty()) {
                var exception = new BusinessNotFoundException(requestDTO.getBusinessId());
                logger.error(exception.getMessage());
                throw exception;
            }
            var business = businessResult.get();

            // Check if the logged in user is the business owner / administrator or a GAA
            if (!(business.userIsAdmin(loggedInUser.getId()) ||
                    business.getPrimaryAdministratorId().equals(loggedInUser.getId())) && !loggedInUser.isGAA()) {
                var exception = new ForbiddenAdministratorActionException(
                        requestDTO.getBusinessId());
                logger.error(exception.getMessage());
                throw exception;
            }

            //These have been checked to be not empty by the DTO
            //Also productId checked against regex by DTO
            String productId = requestDTO.getId();
            String name = requestDTO.getName();

            //These can be empty
            String description = requestDTO.getDescription();
            String manufacturer = requestDTO.getManufacturer();


            Double recommendedRetailPrice = null;
            //DTO validates that price is either null or >= 0
            if (requestDTO.getRecommendedRetailPrice() != null) {
                recommendedRetailPrice = requestDTO.getRecommendedRetailPrice();
            }

            //Return 400 if id not unique
            if (productRepository.findByIdAndBusinessId(productId, requestDTO.getBusinessId()).isPresent()) {
                var exception = new ProductIdAlreadyExistsException();
                logger.warn(exception.getMessage());
                throw exception;
            }

            var product = new Product(productId, name, description, manufacturer, recommendedRetailPrice, requestDTO.getBusinessId());
            productRepository.save(product);

        } catch (BusinessNotFoundException | ForbiddenAdministratorActionException
                | ProductIdAlreadyExistsException handledException) {
            throw handledException;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while adding business product: %s",
                    unhandledException.getMessage()));
            throw unhandledException;
        }
    }


    /**
     * Edits product with id productId.
     *
     * Note: The frontend prefills the fields with the existing values for the product
     * so we do not need to check whether the request has certain fields or not.
     * This checking has been removed to greatly reduce the Cognitive Complexity of this method.
     * But this method still has a large Cognitive Complexity
     *
     * @param requestDTO The DTO for this request
     */
    public void editProduct(EditProductDTO requestDTO) {
        try {
            // Get the logged in user from the user's email
            String userEmail = requestDTO.getAppUser().getUsername();
            var loggedInUser = userRepository.findByEmail(userEmail).get(0);

            logger.info("User with user id: {} Editing product with id '{}' from business with id {}",
                    loggedInUser.getId(), requestDTO.getProductId(), requestDTO.getBusinessId());

            // Get the business
            Optional<Business> businessResult = businessRepository.findById(requestDTO.getBusinessId());

            // Check if the business exists
            if (businessResult.isEmpty()) {
                var exception = new BusinessNotFoundException(requestDTO.getBusinessId());
                logger.error(exception.getMessage());
                throw exception;
            }
            var business = businessResult.get();

            // Check if the logged in user is the business owner / administrator or a GAA
            if (!(business.userIsAdmin(loggedInUser.getId()) ||
                    business.getPrimaryAdministratorId().equals(loggedInUser.getId())) && !loggedInUser.isGAA()) {
                var exception = new ForbiddenAdministratorActionException(requestDTO.getBusinessId());
                logger.error(exception.getMessage());
                throw exception;
            }

            // Get the product
            Optional<Product> productResult = productRepository.findByIdAndBusinessId(requestDTO.getProductId(), requestDTO.getBusinessId());

            // Check if the product exists
            if (productResult.isEmpty()) {
                var exception = new ProductNotFoundException(requestDTO.getProductId(), requestDTO.getBusinessId());
                logger.error(exception.getMessage());
                throw exception;
            }

            var product = productResult.get();
            var originalProduct = productResult.get();

            //Has been checked to be non empty by the DTO
            String newName = requestDTO.getName();
            product.setName(newName);

            String newDescription = requestDTO.getDescription();
            product.setDescription(newDescription);

            String newManufacturer = requestDTO.getManufacturer();
            product.setManufacturer(newManufacturer);

            //DTO validates that price is either null or >= 0
            Double newRRP = requestDTO.getRecommendedRetailPrice();
            product.setRecommendedRetailPrice(newRRP);

            String newId = requestDTO.getId();

            //Id
            if(!originalProduct.getId().equals(newId)) {
                //Id has been checked to be non empty by the DTO
                //Also productId checked against regex by DTO

                //Return 400 if id not unique
                if (newId.equals(originalProduct.getId()) || !(originalProduct.getId().equals(newId)) &&
                        productRepository.findByIdAndBusinessId(newId, requestDTO.getBusinessId()).isPresent()) {
                    var exception = new ProductIdAlreadyExistsException();
                    logger.warn(exception.getMessage());
                    throw exception;
                }

                //Create new product
                var newProduct = new Product(newId, newName, newDescription, newManufacturer, newRRP, requestDTO.getBusinessId());
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

        } catch (BusinessNotFoundException | ForbiddenAdministratorActionException |
                ProductIdAlreadyExistsException handledException) {
            throw handledException;
        } catch (Exception unhandledException) {
            logger.error(String.format("Unexpected error while editing business product: %s",
                    unhandledException.getMessage()));
            throw unhandledException;
        }

    }


    /**
     * Searches a business's catalogue for products
     * @param businessId id of the business to search products of
     * @param requestDTO request body containing which fields to search by
     * @param appUser the user making the request
     */
    public List<Product> searchProducts(Integer businessId, String searchQuery, ProductSearchDTO requestDTO,
                                        AppUserDetails appUser) {

        var loggedInUser = userRepository.findByEmail(appUser.getUsername()).get(0);

        Optional<Business> businessResult = businessRepository.findById(businessId);

        if (businessResult.isEmpty()) {
            var exception = new NotAcceptableException(String.format("There is no business that exists with the id '%d'",
                    businessId));
            logger.error(exception.getMessage());
            throw exception;
        }
        var business = businessResult.get();

        if (!business.userCanDoAction(loggedInUser)) {
            var exception = new ForbiddenAdministratorActionException(businessId);
            logger.error(exception.getMessage());
            throw exception;
        }

        //Currently works for queries without ANDs, ORs or quotes
        searchQuery = searchQuery.toLowerCase();
        Specification<Product> spec = Specification.where(null);

        if (Boolean.TRUE.equals(requestDTO.getMatchingId())) {
            spec.or(ProductSpecifications.containsId(searchQuery));
        }
        if (Boolean.TRUE.equals(requestDTO.getMatchingName())) {
            spec.or(ProductSpecifications.containsName(searchQuery));
        }
        if (Boolean.TRUE.equals(requestDTO.getMatchingDescription())) {
            spec.or(ProductSpecifications.containsDescription(searchQuery));
        }
        if (Boolean.TRUE.equals(requestDTO.getMatchingManufacturer())) {
            spec.or(ProductSpecifications.containsManufacturer(searchQuery));
        }

        return productRepository.findAll(spec);

        //TODO: return list of GetProductResponseDTOs

    }

}
