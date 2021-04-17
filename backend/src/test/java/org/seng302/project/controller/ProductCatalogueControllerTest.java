package org.seng302.project.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.project.controller.authentication.AppUserDetails;
import org.seng302.project.exceptions.ForbiddenAdministratorActionException;
import org.seng302.project.exceptions.NoBusinessExistsException;
import org.seng302.project.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;



import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for ProductCatalogueController class.
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
public class ProductCatalogueControllerTest {

    private User user;
    private User owner;
    private Integer businessId;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private ProductCatalogueController productCatalogueController;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Creates the user if it's not already created.
     * If it is already created, the user is returned.
     * @return User
     */
    private User createUser(User wantedUser) {
        if (userRepository.findByEmail(wantedUser.getEmail()).size() > 0) {
            // Already exists, return it
            return(userRepository.findByEmail(wantedUser.getEmail()).get(0));
        } else {
            // User doesn't exist, save it to repository
            wantedUser.setPassword(passwordEncoder.encode(wantedUser.getPassword()));
            userRepository.save(wantedUser);
            return wantedUser;
        }
    }

    @BeforeEach
    public void initialise() {
        // Create the users

        user = createUser(new User("John", "Smith", "Bob", "Jonny",
                "Likes long walks on the beach", "jane111@gmail.com", "1999-04-27",
                "+64 3 555 0129", "4 Rountree Street, Upper Riccarton", "1337-H%nt3r2"));

        owner = createUser(new User("Jane", "Smith", "Rose", "Jonny",
                "Likes long walks on the beach", "johnxyz@gmail.com", "1999-04-27",
                "+64 3 555 0120", "4 Rountree Street, Upper Riccarton", "1337-H%nt3r2"));

        // Create the business
        Business testBusiness = new Business("Business", "A Business", "4 Rountree", "Retail",
                owner.getId());
        businessRepository.save(testBusiness);
        businessId = testBusiness.getId();

        // Create a product
        Product product = new Product("p1", "Watties Beans", "beans in a can", "Watties", 2.00,
                businessId);
        productRepository.save(product);
    }

    /**
     * Tries to get the business products as a logged in normal user.
     * Expects a 403 forbidden response.
     */
    @Test
    void testRandomUserCantAccess() {
        assertThrows(ForbiddenAdministratorActionException.class, () -> {
            productCatalogueController.getBusinessesProducts(businessId, new AppUserDetails(user));
        });
    }

    /**
     * Tries to get the business products as a logged in owner.
     * Expects a 200 OK response, and product present.
     */
    @Test
    void testAdministratorCanGetProducts() {
        List<Product> products =
                productCatalogueController.getBusinessesProducts(businessId, new AppUserDetails(owner));
        assertEquals(products.size(), 1);
        assertEquals(products.get(0).getName(), "Watties Beans");
    }

    /**
     * Tries to get a non existent business.
     * Expects a 406 response.
     */
    @Test
    void testNonExistentBusiness() {
        assertThrows(NoBusinessExistsException.class, () -> {
            productCatalogueController.getBusinessesProducts(businessId + 999999, new AppUserDetails(user));
        });
    }

    /**
     * Tries to get a non existent business.
     * Expects a 406 response.
     * @throws Exception possible exception from using MockMvc
     */
    @Test
    void testUnauthorised() throws Exception {
        mockMvc.perform(get("/business/{id}/products", businessId))
                .andExpect(status().isUnauthorized());
    }
}
