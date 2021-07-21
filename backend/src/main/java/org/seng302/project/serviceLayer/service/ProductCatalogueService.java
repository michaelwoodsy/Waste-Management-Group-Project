package org.seng302.project.serviceLayer.service;

import org.seng302.project.repositoryLayer.model.*;
import org.seng302.project.repositoryLayer.repository.BusinessRepository;
import org.seng302.project.repositoryLayer.repository.InventoryItemRepository;
import org.seng302.project.repositoryLayer.repository.ProductRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.repositoryLayer.specification.ProductSpecifications;
import org.seng302.project.serviceLayer.dto.product.AddProductDTO;
import org.seng302.project.serviceLayer.dto.product.EditProductDTO;
import org.seng302.project.serviceLayer.dto.product.GetProductDTO;
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


import javax.transaction.Transactional;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ProductCatalogueService {
    private static final Logger logger = LoggerFactory.getLogger(ProductCatalogueService.class.getName());

    private final BusinessRepository businessRepository;
    private final ProductRepository productRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final UserRepository userRepository;

    private static final String AND_SPACE_REGEX = "( and |\\s)(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
    private static final String QUOTE_REGEX = "^\".*\"$";

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
     * Helper function for this class that gets the business
     * Throws a NotAcceptableException if the business is not found
     * @return the business with the given id
     */
    private Business getBusiness(Integer businessId) {
        Optional<Business> businessResult = businessRepository.findById(businessId);

        if (businessResult.isEmpty()) {
            var exception = new NotAcceptableException(String.format("There is no business that exists with the id '%d'",
                    businessId));
            logger.error(exception.getMessage());
            throw exception;
        }

        return businessResult.get();
    }

    /**
     * Helper function for this class that checks a user is either
     * and admin of the business or a DGAA/GAA
     * Throws a ForbiddenAdministratorActionException if the user is not an admin
     */
    private void checkUserAdmin(Business business, User user) {
        if (!business.userCanDoAction(user)) {
            var exception = new ForbiddenAdministratorActionException(business.getId());
            logger.error(exception.getMessage());
            throw exception;
        }
    }

    /**
     * Gets a list of products that belongs to a business.
     *
     * @param businessId ID of the business to get the products of.
     * @param appUser    AppUserDetails from spring security
     * @return List of products that belongs to the business.
     */
    public List<GetProductDTO> getBusinessesProducts(Integer businessId, AppUserDetails appUser) {
        try {
            // Get the logged in user from the users email
            String userEmail = appUser.getUsername();
            var loggedInUser = userRepository.findByEmail(userEmail).get(0);

            logger.info("User with user id: {} getting products from business with ID: {}", loggedInUser.getId(), businessId );

            var business = getBusiness(businessId);
            checkUserAdmin(business, loggedInUser);

            // Get the products for the businesses
            List<Product> products = productRepository.findAllByBusinessId(businessId);
            List<GetProductDTO> response = new ArrayList<>();
            for (Product product : products) {
                response.add(new GetProductDTO(product));
            }
            return response;

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

            var business = getBusiness(requestDTO.getBusinessId());
            checkUserAdmin(business, loggedInUser);

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

            var business = getBusiness(requestDTO.getBusinessId());
            checkUserAdmin(business, loggedInUser);


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
     * Searches the product id field, handling ORs, ANDs, spaces and quotes
     * Updates the set of products for searchProducts()
     * @param currentResult The products that have already been retrieved searchProducts()
     * @param conjunctions The list of strings that have been split by OR
     * @param businessId The id of the business we are searching the catalogue of
     */
    private void searchIdField(Set<Product> currentResult, String[] conjunctions, Integer businessId) {
        for (String conjunction : conjunctions) {
            String[] terms = conjunction.split(AND_SPACE_REGEX); // Split by AND and spaces
            Specification<Product> spec = Specification.where(ProductSpecifications.hasBusinessId(businessId));
            for (String term : terms) {
                //Remove quotes from quoted string, then search by full contents inside the quotes
                if (Pattern.matches(QUOTE_REGEX, term)) {
                    term = term.replace("\"", "");
                }
                spec = spec.and(ProductSpecifications.containsId(term));
            }
            currentResult.addAll(productRepository.findAll(spec));
        }
    }


    /**
     * Searches the product name field, handling ORs, ANDs, spaces and quotes
     * Updates the set of products for searchProducts()
     * @param currentResult The products that have already been retrieved searchProducts()
     * @param conjunctions The list of strings that have been split by OR
     * @param businessId The id of the business we are searching the catalogue of
     */
    private void searchNameField(Set<Product> currentResult, String[] conjunctions, Integer businessId) {
        for (String conjunction : conjunctions) {
            String[] terms = conjunction.split(AND_SPACE_REGEX); // Split by AND and spaces
            Specification<Product> spec = Specification.where(ProductSpecifications.hasBusinessId(businessId));
            for (String term : terms) {
                //Remove quotes from quoted string, then search by full contents inside the quotes
                if (Pattern.matches(QUOTE_REGEX, term)) {
                    term = term.replace("\"", "");
                }
                spec = spec.and(ProductSpecifications.containsName(term));
            }
            currentResult.addAll(productRepository.findAll(spec));
        }
    }


    /**
     * Searches the product description field, handling ORs, ANDs, spaces and quotes
     * Updates the set of products for searchProducts()
     * @param currentResult The products that have already been retrieved searchProducts()
     * @param conjunctions The list of strings that have been split by OR
     * @param businessId The id of the business we are searching the catalogue of
     */
    private void searchDescriptionField(Set<Product> currentResult, String[] conjunctions, Integer businessId) {
        for (String conjunction : conjunctions) {
            String[] terms = conjunction.split(AND_SPACE_REGEX); // Split by AND and spaces
            Specification<Product> spec = Specification.where(ProductSpecifications.hasBusinessId(businessId));
            for (String term : terms) {
                //Remove quotes from quoted string, then search by full contents inside the quotes
                if (Pattern.matches(QUOTE_REGEX, term)) {
                    term = term.replace("\"", "");
                }
                spec = spec.and(ProductSpecifications.containsDescription(term));
            }
            currentResult.addAll(productRepository.findAll(spec));
        }
    }

    /**
     * Searches the product manufacturer field, handling ORs, ANDs, spaces and quotes
     * Updates the set of products for searchProducts()
     * @param currentResult The products that have already been retrieved searchProducts()
     * @param conjunctions The list of strings that have been split by OR
     * @param businessId The id of the business we are searching the catalogue of
     */
    private void searchManufacturerField(Set<Product> currentResult, String[] conjunctions, Integer businessId) {
        for (String conjunction : conjunctions) {
            String[] terms = conjunction.split(AND_SPACE_REGEX); // Split by AND and spaces
            Specification<Product> spec = Specification.where(ProductSpecifications.hasBusinessId(businessId));
            for (String term : terms) {
                //Remove quotes from quoted string, then search by full contents inside the quotes
                if (Pattern.matches(QUOTE_REGEX, term)) {
                    term = term.replace("\"", "");
                }
                spec = spec.and(ProductSpecifications.containsManufacturer(term));
            }
            currentResult.addAll(productRepository.findAll(spec));
        }
    }

    /**
     * Searches a business's catalogue for products
     * @param businessId id of the business to search products of
     * @param requestDTO a DTO containing which fields to search by
     * @param appUser the user making the request
     */
    public List<GetProductDTO> searchProducts(Integer businessId, String searchQuery, ProductSearchDTO requestDTO,
                                              AppUserDetails appUser) {

        logger.info("Request to search products of business with id: {}, and searchQuery: {}", businessId, searchQuery);

        var loggedInUser = userRepository.findByEmail(appUser.getUsername()).get(0);

        var business = getBusiness(businessId);
        checkUserAdmin(business, loggedInUser);

        searchQuery = searchQuery.toLowerCase();
        Set<Product> result = new LinkedHashSet<>();
        String[] conjunctions = searchQuery.split(" or (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // Split by OR

        //Searching id field
        if (Boolean.TRUE.equals(requestDTO.getMatchingId())) {
            searchIdField(result, conjunctions, businessId);
        }

        //Searching name field
        if (Boolean.TRUE.equals(requestDTO.getMatchingName())) {
            searchNameField(result, conjunctions, businessId);
        }

        //Searching description field
        if (Boolean.TRUE.equals(requestDTO.getMatchingDescription())) {
            searchDescriptionField(result, conjunctions, businessId);
        }

        //Searching manufacturer field
        if (Boolean.TRUE.equals(requestDTO.getMatchingManufacturer())) {
            searchManufacturerField(result, conjunctions, businessId);
        }

        List<Product> products = new ArrayList<>(result);
        return products.stream().map(GetProductDTO::new).collect(Collectors.toList());
    }

    /**
     * Updates the currencyCountry field of all products in a business.
     * Expects the business to exist.
     *
     * @param businessId Id of bussiness to update products for.
     * @param currencyCountry New country to update all products with.
     */
    @Transactional
    public void updateProductCurrency(Integer businessId, String currencyCountry) {
        List<Product> products = productRepository.findAllByBusinessId(businessId);
        for (Product product : products) {
            product.setCurrencyCountry(currencyCountry);
            productRepository.save(product);
        }
    }
}
