package org.seng302.project.controller;

import org.seng302.project.controller.authentication.AppUserDetails;
import org.seng302.project.exceptions.ForbiddenAdministratorActionException;
import org.seng302.project.exceptions.NoBusinessExistsException;
import org.seng302.project.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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
        // Get the business
        Optional<Business> businessResult = businessRepository.findById(businessId);

        // Check if the business exists
        if (businessResult.isEmpty()) {
            NoBusinessExistsException exception = new NoBusinessExistsException(businessId);
            logger.error(exception.getMessage());
            throw exception;
        }
        Business business = businessResult.get();

        // Get the logged in user from the users email
        String userEmail = appUser.getUsername();
        User loggedInUser = userRepository.findByEmail(userEmail).get(0);

        // Check if the logged in user is the business owner / administrator
        if (!business.userIsAdmin(loggedInUser.getId())) {
            ForbiddenAdministratorActionException exception = new ForbiddenAdministratorActionException(businessId);
            logger.error(exception.getMessage());
            throw exception;
        }

        // Get the products for the businesses
        return productRepository.findAllByBusinessId(businessId);
    }

}
