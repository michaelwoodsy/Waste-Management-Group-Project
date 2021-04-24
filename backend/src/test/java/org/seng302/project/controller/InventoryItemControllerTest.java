package org.seng302.project.controller;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.seng302.project.controller.authentication.AppUserDetails;
import org.seng302.project.exceptions.*;
import org.seng302.project.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
public class InventoryItemControllerTest {

    private User user;
    private User owner;
    private Integer businessId;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private InventoryItemController inventoryItemController;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

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
     * Tries creating a inventory item when not an admin.
     * Expect a 400 response.
     */
    @Test
    void tryCreatingItemWithoutAdmin() throws Exception {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, 7);//Date always in the future

        JSONObject testItem = new JSONObject();
        testItem.put("productId", "p1");
        testItem.put("quantity", 6);
        testItem.put("expires", dateFormatter.format(c.getTime()));

        RequestBuilder postInventoryRequest = MockMvcRequestBuilders
                .post("/businesses/{businessId}/inventory", businessId)
                .content(testItem.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("jane111@gmail.com", "1337-H%nt3r2"));

        MvcResult postInventoryResponse = this.mockMvc.perform(postInventoryRequest)
                .andExpect(MockMvcResultMatchers.status().isForbidden()) // We expect a 403 response
                .andReturn();

        String returnedExceptionString = postInventoryResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new ForbiddenAdministratorActionException(businessId).getMessage(), returnedExceptionString);
    }



    /**
     * Tries creating a inventory item without a product id.
     * Expect a 400 response.
     */
    @Test
    void tryCreatingItemWithoutProductId() throws Exception {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();

        JSONObject testItem = new JSONObject();
        testItem.put("quantity", 6);

        c.setTime(new Date());
        c.add(Calendar.DATE, 7);
        testItem.put("expires", dateFormatter.format(c.getTime()));

        RequestBuilder postInventoryRequest = MockMvcRequestBuilders
                .post("/businesses/{businessId}/inventory", businessId)
                .content(testItem.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("johnxyz@gmail.com", "1337-H%nt3r2"));

        MvcResult postInventoryResponse = this.mockMvc.perform(postInventoryRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postInventoryResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new MissingProductIdException().getMessage(), returnedExceptionString);
    }

    /**
     * Tries creating a inventory item without a quantity.
     * Expect a 400 response.
     */
    @Test
    void tryCreatingItemWithoutQuantity() throws Exception {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();

        JSONObject testItem = new JSONObject();
        testItem.put("productId", "p1");

        c.setTime(new Date());
        c.add(Calendar.DATE, 7);
        testItem.put("expires", dateFormatter.format(c.getTime()));

        RequestBuilder postInventoryRequest = MockMvcRequestBuilders
                .post("/businesses/{businessId}/inventory", businessId)
                .content(testItem.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("johnxyz@gmail.com", "1337-H%nt3r2"));

        MvcResult postInventoryResponse = this.mockMvc.perform(postInventoryRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postInventoryResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new MissingInventoryItemQuantityException().getMessage(), returnedExceptionString);
    }

    /**
     * Tries creating a inventory item without a expiry date.
     * Expect a 400 response.
     */
    @Test
    void tryCreatingItemWithoutExpiry() throws Exception {

        JSONObject testItem = new JSONObject();
        testItem.put("productId", "p1");
        testItem.put("quantity", 6);

        RequestBuilder postInventoryRequest = MockMvcRequestBuilders
                .post("/businesses/{businessId}/inventory", businessId)
                .content(testItem.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("johnxyz@gmail.com", "1337-H%nt3r2"));

        MvcResult postInventoryResponse = this.mockMvc.perform(postInventoryRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postInventoryResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new MissingInventoryItemExpiryException().getMessage(), returnedExceptionString);
    }

    /**
     * Tries creating a inventory item with a manufacture date in the future.
     * Expect a 400 response.
     */
    @Test
    void tryCreatingItemWithFutureManufacture() throws Exception {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();

        JSONObject testItem = new JSONObject();
        testItem.put("productId", "p1");
        testItem.put("quantity", 6);

        c.setTime(new Date());
        c.add(Calendar.DATE, 7);
        testItem.put("expires", dateFormatter.format(c.getTime()));

        c.setTime(new Date());
        c.add(Calendar.DATE, 1);
        testItem.put("manufactured", dateFormatter.format(c.getTime()));

        RequestBuilder postInventoryRequest = MockMvcRequestBuilders
                .post("/businesses/{businessId}/inventory", businessId)
                .content(testItem.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("johnxyz@gmail.com", "1337-H%nt3r2"));

        MvcResult postInventoryResponse = this.mockMvc.perform(postInventoryRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postInventoryResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new InvalidManufactureDateException().getMessage(), returnedExceptionString);
    }

    /**
     * Tries creating a inventory item with a sellBy date in the past.
     * Expect a 400 response.
     */
    @Test
    void tryCreatingItemWithPastSellBy() throws Exception {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();

        JSONObject testItem = new JSONObject();
        testItem.put("productId", "p1");
        testItem.put("quantity", 6);

        c.setTime(new Date());
        c.add(Calendar.DATE, 7);
        testItem.put("expires", dateFormatter.format(c.getTime()));

        c.setTime(new Date());
        c.add(Calendar.DATE, -1);
        testItem.put("sellBy", dateFormatter.format(c.getTime()));

        RequestBuilder postInventoryRequest = MockMvcRequestBuilders
                .post("/businesses/{businessId}/inventory", businessId)
                .content(testItem.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("johnxyz@gmail.com", "1337-H%nt3r2"));

        MvcResult postInventoryResponse = this.mockMvc.perform(postInventoryRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postInventoryResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new InvalidSellByDateException().getMessage(), returnedExceptionString);
    }

    /**
     * Tries creating a inventory item with a bestBefore date in the past.
     * Expect a 400 response.
     */
    @Test
    void tryCreatingItemWithPastBestBefore() throws Exception {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();

        JSONObject testItem = new JSONObject();
        testItem.put("productId", "p1");
        testItem.put("quantity", 6);

        c.setTime(new Date());
        c.add(Calendar.DATE, 7);
        testItem.put("expires", dateFormatter.format(c.getTime()));

        c.setTime(new Date());
        c.add(Calendar.DATE, -1);
        testItem.put("bestBefore", dateFormatter.format(c.getTime()));

        RequestBuilder postInventoryRequest = MockMvcRequestBuilders
                .post("/businesses/{businessId}/inventory", businessId)
                .content(testItem.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("johnxyz@gmail.com", "1337-H%nt3r2"));

        MvcResult postInventoryResponse = this.mockMvc.perform(postInventoryRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postInventoryResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new InvalidBestBeforeDateException().getMessage(), returnedExceptionString);
    }

    /**
     * Tries creating a inventory item with a expiry date in the past.
     * Expect a 400 response.
     */
    @Test
    void tryCreatingItemWithPastExpiry() throws Exception {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();

        JSONObject testItem = new JSONObject();
        testItem.put("productId", "p1");
        testItem.put("quantity", 6);

        c.setTime(new Date());
        c.add(Calendar.DATE, -1);
        testItem.put("expires", dateFormatter.format(c.getTime()));

        RequestBuilder postInventoryRequest = MockMvcRequestBuilders
                .post("/businesses/{businessId}/inventory", businessId)
                .content(testItem.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("johnxyz@gmail.com", "1337-H%nt3r2"));

        MvcResult postInventoryResponse = this.mockMvc.perform(postInventoryRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postInventoryResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new ItemExpiredException().getMessage(), returnedExceptionString);
    }

    /**
     * Tests successful creation of a inventory item through the POST method
     */
    @Test
    void createAndRetrieveInventoryItem() throws Exception {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        Calendar c = Calendar.getInstance();


        JSONObject testItem = new JSONObject();
        testItem.put("productId", "p1");
        testItem.put("quantity", 6);

        c.setTime(new Date());
        c.add(Calendar.DATE, 1);
        String expiryString = dateFormatter.format(c.getTime());
        testItem.put("expires", expiryString);

        c.setTime(new Date());
        c.add(Calendar.DATE, 1);
        String bestBeforeString = dateFormatter.format(c.getTime());
        testItem.put("bestBefore", bestBeforeString);

        c.setTime(new Date());
        c.add(Calendar.DATE, -1);
        String manufacturedString = dateFormatter.format(c.getTime());
        testItem.put("manufactured", manufacturedString);

        RequestBuilder postItemRequest = MockMvcRequestBuilders
                .post("/businesses/{businessId}/inventory", businessId)
                .content(testItem.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("johnxyz@gmail.com", "1337-H%nt3r2"));

        this.mockMvc.perform(postItemRequest)
                .andExpect(MockMvcResultMatchers.status().isCreated()) // We expect a 201 response
                .andReturn();

        List<InventoryItem> retrievedInventoryItems = inventoryItemRepository.findAllByBusinessId(businessId);
        assertEquals(retrievedInventoryItems.size(), 1);

        InventoryItem retrievedItem = retrievedInventoryItems.get(0);

        Assertions.assertEquals("p1", retrievedItem.getProduct().getId());
        Assertions.assertEquals(6, retrievedItem.getQuantity());
        Assertions.assertEquals(expiryString, retrievedItem.getExpires());
        Assertions.assertEquals(bestBeforeString, retrievedItem.getBestBefore());
        Assertions.assertEquals(manufacturedString, retrievedItem.getManufactured());
    }
}


