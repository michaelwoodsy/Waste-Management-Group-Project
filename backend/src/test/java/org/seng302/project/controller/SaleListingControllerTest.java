package org.seng302.project.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.seng302.project.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ContextConfiguration
public class SaleListingControllerTest {

    // Test users
    private User user;
    private final String userEmail = "basicUser@gmail.com";
    private final String userPassword = "123";
    private User owner;
    private final String ownerEmail = "ownerUser@gmail.com";
    private final String ownerPassword = "123";

    // Test business
    private Business business;

    // Test product
    private Product product;

    // Test inventory
    private InventoryItem inventoryItem;

    private SaleListing listing;

    // Test address
    private Address testAddress1;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BusinessRepository businessRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private InventoryItemRepository inventoryItemRepository;
    @Autowired
    private SaleListingRepository saleListingRepository;
    @Autowired
    private AddressRepository addressRepository;

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
        inventoryItemRepository.deleteAll();

        // Create address
        testAddress1 = new Address();
        addressRepository.save(testAddress1);

        // Create the users
        user = createUser(new User("John", "Smith", "Bob", "Jonny",
                "Likes long walks on the beach", userEmail, "1999-04-27",
                "+64 3 555 0129", testAddress1, userPassword));
        owner = createUser(new User("Jane", "Smith", "Rose", "Jonny",
                "Likes long walks on the beach", ownerEmail, "1999-04-27",
                "+64 3 555 0120", testAddress1, ownerPassword));

        // Create the business
        business = new Business("Business", "A Business", testAddress1, "Retail",
                owner.getId());
        businessRepository.save(business);

        // Create a product
        product = new Product("p1", "Watties Beans", "beans in a can", "Watties", 2.00,
                business.getId());
        productRepository.save(product);

        // Create inventory item
        inventoryItem = new InventoryItem(product, 20,
                10.99, 219.8, "2021-04-25",
                "2021-04-25", "2021-04-25", "2021-04-25");
        inventoryItem = inventoryItemRepository.save(inventoryItem);

    }

    @AfterEach
    public void tearDown() {
        if (listing != null) {
            saleListingRepository.delete(listing);
        }
        inventoryItemRepository.delete(inventoryItem);
        productRepository.delete(product);
        businessRepository.delete(business);
        userRepository.delete(user);
        userRepository.delete(owner);
        addressRepository.delete(testAddress1);
    }

    @Test
    public void checkUnauthenticatedRequest() throws Exception {
        mockMvc.perform(get("/businesses/{id}/listings", business.getId()))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Check a user that isn't an administrator gets a 403.
     */
    @Test
    public void testRandomUserCantAccess() throws Exception {
        mockMvc.perform(get("/businesses/{businessId}/listings", business.getId())
                .with(httpBasic(userEmail, userPassword)))
                .andExpect(status().isForbidden());
    }

    /**
     * Check a user that is an administrator gets a 200.
     */
    @Test
    public void testBusinessAdminCanAccess() throws Exception {
        mockMvc.perform(get("/businesses/{businessId}/listings", business.getId())
                .with(httpBasic(ownerEmail, ownerPassword)))
                .andExpect(status().isOk());
    }

    /**
     * Test a non existent business returns 406 for an authenticated user.
     */
    @Test
    public void testNonExistentBusiness() throws Exception {
        mockMvc.perform(get("/businesses/{businessId}/listings", business.getId() + 9999)
                .with(httpBasic(userEmail, userPassword)))
                .andExpect(status().isNotAcceptable());
    }


    /**
     * Check a user that is an administrator gets a list of sale listings returned.
     */
    @Test
    public void testSaleListingsAreReturned() throws Exception {
        // Create new sale listing
        listing = new SaleListing(business.getId(), inventoryItem, 15.00, null,
                LocalDateTime.now(), 1);
        listing = saleListingRepository.save(listing);

        MvcResult returnedListingResult = mockMvc.perform(get("/businesses/{businessId}/listings", business.getId())
                .with(httpBasic(ownerEmail, ownerPassword)))
                .andExpect(status().isOk())
                .andReturn();

        String returnedListingString = returnedListingResult.getResponse().getContentAsString();
        JSONArray returnedArray = new JSONArray(returnedListingString);

        // Check 1 item is in the array
        assertEquals(1, returnedArray.length());

        // Check the item has the correct values
        JSONObject returnedListing = returnedArray.getJSONObject(0);
        assertEquals(listing.getId(), returnedListing.get("id"));
        assertEquals(listing.getQuantity(), returnedListing.get("quantity"));
        assertEquals(listing.getPrice(), returnedListing.get("price"));
        assertNotNull(returnedListing.get("closes"));
        assertNotNull(returnedListing.get("created"));

        // Check the embedded inventory item
        JSONObject returnedItem = returnedListing.getJSONObject("inventoryItem");
        assertNotNull(returnedItem);
        assertEquals(inventoryItem.getId(), returnedItem.get("id"));
        JSONObject returnedProduct = returnedItem.getJSONObject("product");
        assertNotNull(returnedProduct);

        // Check embedded product
        assertEquals(product.getId(), returnedProduct.get("id"));
        assertEquals(product.getName(), returnedProduct.get("name"));
        assertEquals(product.getDescription(), returnedProduct.get("description"));
        assertEquals(product.getManufacturer(), returnedProduct.get("manufacturer"));
        assertEquals(product.getRecommendedRetailPrice(), returnedProduct.get("recommendedRetailPrice"));
        assertNotNull(product.getCreated());
        assertTrue(returnedProduct.has("images"));
    }

}
