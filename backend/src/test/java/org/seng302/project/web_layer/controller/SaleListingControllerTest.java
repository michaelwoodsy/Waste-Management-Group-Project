package org.seng302.project.web_layer.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.seng302.project.repository_layer.model.*;
import org.seng302.project.repository_layer.repository.*;
import org.seng302.project.service_layer.exceptions.*;
import org.seng302.project.service_layer.exceptions.businessAdministrator.ForbiddenAdministratorActionException;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class SaleListingControllerTest {

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

    @Test
    void checkUnauthenticatedRequest() throws Exception {
        mockMvc.perform(get("/businesses/{id}/listings", business.getId()))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/businesses/{id}/listings", business.getId()))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Check a user that is an administrator gets a 200.
     */
    @Test
    void testBusinessAdminCanAccess() throws Exception {
        mockMvc.perform(get("/businesses/{businessId}/listings", business.getId())
                .with(httpBasic(ownerEmail, ownerPassword)))
                .andExpect(status().isOk());
    }

    /**
     * Test a non existent business returns 406 for an authenticated user.
     */
    @Test
    void testNonExistentBusiness() throws Exception {
        mockMvc.perform(get("/businesses/{businessId}/listings", business.getId() + 9999)
                .with(httpBasic(userEmail, userPassword)))
                .andExpect(status().isNotAcceptable());
    }


    /**
     * Check a user that is an administrator gets a list of sale listings returned.
     */
    @Test
    @Transactional
    void testSaleListingsAreReturned() throws Exception {
        // Create new sale listing
        listing = new SaleListing(business, inventoryItem, 15.00, null,
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

    /**
     * Test creating a sales listing with a not authorized user.
     */
    @Test
    void testCreateSalesListingNotAuthorized() throws Exception {
        LocalDateTime closesDate = LocalDateTime.now();
        closesDate = closesDate.plusDays(10);

        JSONObject testItem = new JSONObject();
        testItem.put("inventoryItemId", inventoryItem.getId());
        testItem.put("quantity", 6);
        testItem.put("price", 59.99);
        testItem.put("moreInfo", "Some more info about this listing");
        testItem.put("closes", closesDate.toString());

        RequestBuilder postListingRequest = MockMvcRequestBuilders
                .post("/businesses/{businessId}/listings", business.getId())
                .content(testItem.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic(userEmail, userPassword));

        MvcResult postInventoryResponse = this.mockMvc.perform(postListingRequest)
                .andExpect(MockMvcResultMatchers.status().isForbidden()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postInventoryResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new ForbiddenAdministratorActionException(business.getId()).getMessage(), returnedExceptionString);
    }

    /**
     * Test creating a sales listing with an invalid inventoryItemId.
     */
    @Test
    void testCreateSalesListingInvalidInventoryItemId() throws Exception {
        LocalDateTime closesDate = LocalDateTime.now();
        closesDate = closesDate.plusDays(10);

        JSONObject testItem = new JSONObject();
        testItem.put("inventoryItemId", 254645756);
        testItem.put("quantity", 6);
        testItem.put("price", 59.99);
        testItem.put("moreInfo", "Some more info about this listing");
        testItem.put("closes", closesDate.toString());

        RequestBuilder postListingRequest = MockMvcRequestBuilders
                .post("/businesses/{businessId}/listings", business.getId())
                .content(testItem.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic(ownerEmail, ownerPassword));

        MvcResult postInventoryResponse = this.mockMvc.perform(postListingRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postInventoryResponse.getResponse().getContentAsString();
        Assertions.assertEquals(String.format(
                "BadRequestException: No inventory item with id 254645756 exists in business with id %d.", business.getId()),
               returnedExceptionString);
    }

    /**
     * Test creating a sales listing with no quantity.
     */
    @Test
    void testCreateSalesListingMissingQuantity() throws Exception {
        LocalDateTime closesDate = LocalDateTime.now();
        closesDate = closesDate.plusDays(10);

        JSONObject testItem = new JSONObject();
        testItem.put("inventoryItemId", inventoryItem.getId());
        testItem.put("quantity", null);
        testItem.put("price", 59.99);
        testItem.put("moreInfo", "Some more info about this listing");
        testItem.put("closes", closesDate.toString());

        RequestBuilder postListingRequest = MockMvcRequestBuilders
                .post("/businesses/{businessId}/listings", business.getId())
                .content(testItem.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic(ownerEmail, ownerPassword));

        MvcResult postInventoryResponse = this.mockMvc.perform(postListingRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postInventoryResponse.getResponse().getContentAsString();
        Assertions.assertEquals("MethodArgumentNotValidException: Quantity is a mandatory field.", returnedExceptionString);
    }

    /**
     * Test creating a sales listing with no price.
     */
    @Test
    void testCreateSalesListingMissingPrice() throws Exception {
        LocalDateTime closesDate = LocalDateTime.now();
        closesDate = closesDate.plusDays(10);

        JSONObject testItem = new JSONObject();
        testItem.put("inventoryItemId", inventoryItem.getId());
        testItem.put("quantity", inventoryItem.getQuantity());
        testItem.put("price", null);
        testItem.put("moreInfo", "Some more info about this listing");
        testItem.put("closes", closesDate.toString());

        RequestBuilder postListingRequest = MockMvcRequestBuilders
                .post("/businesses/{businessId}/listings", business.getId())
                .content(testItem.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic(ownerEmail, ownerPassword));

        MvcResult postInventoryResponse = this.mockMvc.perform(postListingRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postInventoryResponse.getResponse().getContentAsString();
        Assertions.assertEquals("MethodArgumentNotValidException: Price is a mandatory field.", returnedExceptionString);
    }

    /**
     * Test creating a sales listing with to many items (not enough quantity of inventory item).
     */
    @Test
    void testCreateSalesListingToManyItems() throws Exception {
        LocalDateTime closesDate = LocalDateTime.now();
        closesDate = closesDate.plusDays(10);

        JSONObject testItem = new JSONObject();
        testItem.put("inventoryItemId", inventoryItem.getId());
        testItem.put("quantity", inventoryItem.getQuantity() + 1);
        testItem.put("price", 59.99);
        testItem.put("moreInfo", "Some more info about this listing");
        testItem.put("closes", closesDate.toString());

        RequestBuilder postListingRequest = MockMvcRequestBuilders
                .post("/businesses/{businessId}/listings", business.getId())
                .content(testItem.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic(ownerEmail, ownerPassword));

        MvcResult postInventoryResponse = this.mockMvc.perform(postListingRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postInventoryResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new NotEnoughOfInventoryItemException(inventoryItem.getId(), inventoryItem.getQuantity(), 0).getMessage(), returnedExceptionString);
    }

    /**
     * Test creating a sales listing.
     */
    @Test
    void testCreateSalesListing() throws Exception {
        LocalDateTime closesDate = LocalDateTime.now();
        closesDate = closesDate.plusDays(10);

        JSONObject testItem = new JSONObject();
        testItem.put("inventoryItemId", inventoryItem.getId());
        testItem.put("quantity", inventoryItem.getQuantity() - 1);
        testItem.put("price", 59.99);
        testItem.put("moreInfo", "Some more info about this listing");
        testItem.put("closes", closesDate.toString());

        RequestBuilder postListingRequest = MockMvcRequestBuilders
                .post("/businesses/{businessId}/listings", business.getId())
                .content(testItem.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic(ownerEmail, ownerPassword));

        this.mockMvc.perform(postListingRequest)
                .andExpect(MockMvcResultMatchers.status().isCreated()) // We expect a 400 response
                .andReturn();

        List<SaleListing> retrievedSaleListings = saleListingRepository.findAllByBusinessIdAndInventoryItemId(business.getId(), inventoryItem.getId());
        assertEquals(1, retrievedSaleListings.size());
        SaleListing retrievedListing = retrievedSaleListings.get(0);

        Assertions.assertEquals(inventoryItem.getId(), retrievedListing.getInventoryItem().getId());
        Assertions.assertEquals(inventoryItem.getQuantity() - 1, retrievedListing.getQuantity());
        Assertions.assertEquals(59.99, retrievedListing.getPrice());
        Assertions.assertEquals("Some more info about this listing", retrievedListing.getMoreInfo());


        //Try creating a new sales listing with a quantity of 2 (should throw NotEnoughOfInventoryItemException)

        testItem.put("quantity", 2);

        postListingRequest = MockMvcRequestBuilders
                .post("/businesses/{businessId}/listings", business.getId())
                .content(testItem.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic(ownerEmail, ownerPassword));

        MvcResult postInventoryResponse = this.mockMvc.perform(postListingRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postInventoryResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new NotEnoughOfInventoryItemException(inventoryItem.getId(), 1, inventoryItem.getQuantity() - 1).getMessage(), returnedExceptionString);
    }

    /**
     * Test the user must be authorised to view sale listings
     */
    @Test
    void listingSearch_notLoggedIn_401() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/listings")
                .param("searchQuery", "")
                .param("matchingProductName", String.valueOf(false))
                .param("matchingBusinessName", String.valueOf(false))
                .param("matchingBusinessLocation", String.valueOf(false))
                .param("matchingBusinessType", String.valueOf(false))
                .param("pageNumber", String.valueOf(1))
                .param("sortBy", ""))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    /**
     * Test successful retrieval of sales listings (by getting a OK response)
     */
    @Test
    void listingSearch_OK_200() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/listings")
                .param("searchQuery", "")
                .param("matchingProductName", String.valueOf(false))
                .param("matchingBusinessName", String.valueOf(false))
                .param("matchingBusinessLocation", String.valueOf(false))
                .param("matchingBusinessType", String.valueOf(false))
                .param("pageNumber", String.valueOf(1))
                .param("sortBy", "").with(user(new AppUserDetails(user))))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
