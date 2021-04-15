package org.seng302.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.seng302.project.controller.authentication.AppUserDetails;
import org.seng302.project.controller.authentication.WebSecurityConfig;
import org.seng302.project.exceptions.ForbiddenAdministratorActionException;
import org.seng302.project.exceptions.NoBusinessExistsException;
import org.seng302.project.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for ProductCatalogueController class.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
@ContextConfiguration
public class ProductCatalogueControllerTest {

    private final String ownerEmail = "johnxyz@gmail.com";
    private final String userEmail = "jane111@gmail.com";
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
        user = createUser(new User("John", "Smith", "Hector", "Jonny",
                "Likes long walks on the beach", userEmail, "1999-04-27",
                "+64 3 555 0129", "4 Rountree Street, Upper Riccarton", "1337-H%nt3r2"));

        owner = createUser(new User("Jane", "Smith", "Hector", "Jonny",
                "Likes long walks on the beach", ownerEmail, "1999-04-27",
                "+64 3 555 0120", "4 Rountree Street, Upper Riccarton", "1337-H%nt3r2"));

        // Create the business
        Business testBusiness = new Business("Business", "A Business", "4 Rountree", "Retail",
                owner.getId());
        businessRepository.save(testBusiness);
        businessId = testBusiness.getId();

        // Create a product
        Product product = new Product("p1", "Watties Beans", "beans in a can", 2.00,
                businessId);
        productRepository.save(product);
    }

    /**
     * Tries to get the business products as a logged in normal user.
     * Expects a 403 forbidden response.
     * @throws Exception
     */
    @Test
    void testRandomUserCantAccess() throws Exception {
        assertThrows(ForbiddenAdministratorActionException.class, () -> {
            productCatalogueController.getBusinessesProducts(businessId, new AppUserDetails(user));
        });
    }

    /**
     * Tries to get the business products as a logged in owner.
     * Expects a 200 OK response, and product present.
     * @throws Exception
     */
    @Test
    void testAdministratorCanGetProducts() throws Exception {
        List<Product> products =
                productCatalogueController.getBusinessesProducts(businessId, new AppUserDetails(owner));
        assertEquals(products.size(), 1);
        assertEquals(products.get(0).getName(), "Watties Beans");
    }

    /**
     * Tries to get a non existent business.
     * Expects a 406 response.
     * @throws Exception
     */
    @Test
    void testNonExistentBusiness() throws Exception {
        assertThrows(NoBusinessExistsException.class, () -> {
            productCatalogueController.getBusinessesProducts(1, new AppUserDetails(user));
        });
    }

    /**
     * Tries to get a non existent business.
     * Expects a 406 response.
     * @throws Exception
     */
    @Test
    void testUnauthorised() throws Exception {
        mockMvc.perform(get("/business/{id}/products", businessId))
                .andExpect(status().isUnauthorized());
    }
}
