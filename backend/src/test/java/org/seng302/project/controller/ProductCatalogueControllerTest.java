package org.seng302.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.seng302.project.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for ProductCatalogueController class.
 */
@WebMvcTest(controllers = ProductCatalogueController.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ProductCatalogueControllerTest {

    // User that will own a business for tests
    private final String ownerEmail = "john99@gmail.com";
    // User that won't own the business for testing
    private final String userEmail = "jane@gmail.com";
    private Integer businessId;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BusinessRepository businessRepository;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private ProductCatalogueController productCatalogueController;

    @BeforeEach
    public void initialise() {
        // Create the users
        User testUser = new User("John", "Smith", "Hector", "Jonny",
                "Likes long walks on the beach", userEmail, "1999-04-27",
                "+64 3 555 0129", "4 Rountree Street, Upper Riccarton", "1337-H%nt3r2");
        testUser.setPassword(passwordEncoder.encode(testUser.getPassword()));
        User testUserOwner = new User("John", "Smith", "Hector", "Jonny",
                "Likes long walks on the beach", ownerEmail, "1999-04-27",
                "+64 3 555 0129", "4 Rountree Street, Upper Riccarton", "1337-H%nt3r2");
        testUserOwner.setPassword(passwordEncoder.encode(testUserOwner.getPassword()));

        // Create the business
        Business testBusiness = new Business("Business", "A Business", "4 Rountree", "Retail",
                testUserOwner.getId());
        businessId = testBusiness.getId();

        // Create a product
        Product product = new Product("p1", "Watties Beans", "beans in a can", 2.00,
                testBusiness.getId());

        // Add the business to the user list of business
        testUserOwner.setBusinessesAdministered(List.of(testBusiness));

        // setup mocking for the repositories
        Mockito.when(userRepository.findByEmail(userEmail))
                .thenReturn(List.of(testUser));
        Mockito.when(userRepository.findByEmail(ownerEmail))
                .thenReturn(List.of(testUserOwner));
        Mockito.when(businessRepository.findById(testBusiness.getId()))
                .thenReturn(java.util.Optional.of(testBusiness));
        Mockito.when(productRepository.findAllByBusinessId(testBusiness.getId()))
                .thenReturn(List.of(product));

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    /**
     * Tries to get the business products as a logged in normal user.
     * Expects a 403 forbidden response.
     * @throws Exception
     */
    @Test
    @WithUserDetails(userEmail)
    void testNonAdministratorGetProductsReturns403() throws Exception {
        mockMvc.perform(get("/business/{id}/products", businessId))
                .andExpect(status().isForbidden());
    }

    /**
     * Tries to get the business products as a logged in owner.
     * Expects a 200 OK response, and product present.
     * @throws Exception
     */
    @Test
    @WithUserDetails(ownerEmail)
    void testAdministratorGetProductsReturns200() throws Exception {
        mockMvc.perform(get("/business/{id}/products", businessId))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length").value(1));
    }

    /**
     * Tries to get a non existent business.
     * Expects a 406 response.
     * @throws Exception
     */
    @Test
    @WithUserDetails(userEmail)
    void testNonExistentBusiness() throws Exception {
        mockMvc.perform(get("/business/{id}/products", businessId))
                .andExpect(status().isNotAcceptable());
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

//    @Test
//    @WithUserDetails(userEmail)
//    void backup() throws Exception {
//
//        mockMvc.perform(get("/business/{id}/products", businessId))
//                .andExpect(status().isForbidden());
//
//        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
//        Assertions.assertThat(userCaptor.getValue().getFirstName()).isEqualTo("Zaphod");
//        Assertions.assertThat(userCaptor.getValue().getEmail()).isEqualTo("zaphod@galaxy.net");
//    }

}
