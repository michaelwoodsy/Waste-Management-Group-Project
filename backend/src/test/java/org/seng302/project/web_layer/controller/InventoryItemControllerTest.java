package org.seng302.project.web_layer.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.project.repository_layer.model.*;
import org.seng302.project.repository_layer.repository.*;
import org.seng302.project.service_layer.exceptions.*;
import org.seng302.project.service_layer.exceptions.businessAdministrator.ForbiddenAdministratorActionException;
import org.seng302.project.service_layer.exceptions.product.NoProductExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

/**
 * Unit tests for ProductCatalogueController class.
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
class InventoryItemControllerTest {

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

    private void createInventoryItem(Product product) {
        InventoryItem item = new InventoryItem(product, 2, 2.00, 1.80,
                "2021-04-20", null, null, "2023-05-20");
        inventoryItemRepository.save(item);
    }

    @BeforeEach
    public void initialise() {
        saleListingRepository.deleteAll();
        inventoryItemRepository.deleteAll();

        // Create user addresses
        Address testAddress1 = new Address();
        Address testAddress2 = new Address();
        Address testAddress3 = new Address();
        addressRepository.save(testAddress1);
        addressRepository.save(testAddress2);
        addressRepository.save(testAddress3);

        // Create the users
        User user = createUser(new User("John", "Smith", "Bob", "Jonny",
                "Likes long walks on the beach", "jane111@gmail.com", "1999-04-27",
                "+64 3 555 0129", testAddress1, "1337-H%nt3r2"));

        User owner = createUser(new User("Jane", "Smith", "Rose", "Jonny",
                "Likes long walks on the beach", "johnxyz@gmail.com", "1999-04-27",
                "+64 3 555 0120", testAddress2, "1337-H%nt3r2"));

        // Create the business
        Business testBusiness = new Business("Business", "A Business", testAddress3, "Retail",
                owner.getId());

        businessRepository.save(testBusiness);
        testBusiness.addAdministrator(owner);
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
        Assertions.assertEquals(new InvalidQuantityException().getMessage(), returnedExceptionString);
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
        assertEquals(1, retrievedInventoryItems.size());

        InventoryItem retrievedItem = retrievedInventoryItems.get(0);

        Assertions.assertEquals("p1", retrievedItem.getProduct().getId());
        Assertions.assertEquals(6, retrievedItem.getQuantity());
        Assertions.assertEquals(expiryString, retrievedItem.getExpires());
        Assertions.assertEquals(bestBeforeString, retrievedItem.getBestBefore());
        Assertions.assertEquals(manufacturedString, retrievedItem.getManufactured());
    }

    /**
     * Tests that a 401 is returned if trying to get when not logged in.
     */
    @Test
    void tryGetInventoryNotLoggedIn() throws Exception {
        Product product = productRepository.findById(new ProductId("p1", businessId)).orElseThrow();
        createInventoryItem(product);

        RequestBuilder getInventoryRequest = MockMvcRequestBuilders
                .get("/businesses/{businessId}/inventory", businessId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(getInventoryRequest)
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andReturn();
    }

    /**
     * Tests getting the inventory for a business that does not exist.
     */
    @Test
    void tryGetInventoryNoBusiness() throws Exception {
        if (businessRepository.findById(237).isPresent()) {
            businessRepository.deleteById(237);
        }

        RequestBuilder getInventoryRequest = MockMvcRequestBuilders
                .get("/businesses/{businessId}/inventory", 237)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("johnxyz@gmail.com", "1337-H%nt3r2"));

        this.mockMvc.perform(getInventoryRequest)
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andReturn();
    }

    /**
     * Tests that a 403 is returned when trying to get inventory when not admin.
     */
    @Test
    void tryGettingInventoryNotAdmin() throws Exception {
        Product product = productRepository.findById(new ProductId("p1", businessId)).orElseThrow();
        createInventoryItem(product);

        RequestBuilder getInventoryRequest = MockMvcRequestBuilders
                .get("/businesses/{businessId}/inventory", businessId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("jane111@gmail.com", "1337-H%nt3r2"));

        this.mockMvc.perform(getInventoryRequest)
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andReturn();
    }

    /**
     * Tests that the inventory is successfully returned when admin sends get request.
     */
    @Test
    void tryGettingInventory() throws Exception {
        Product product = productRepository.findById(new ProductId("p1", businessId)).orElseThrow();
        createInventoryItem(product);

        RequestBuilder getInventoryRequest = MockMvcRequestBuilders
                .get("/businesses/{businessId}/inventory", businessId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("johnxyz@gmail.com", "1337-H%nt3r2"));

        MvcResult mvcResult = this.mockMvc.perform(getInventoryRequest)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        JSONArray result = new JSONArray(mvcResult.getResponse().getContentAsString());
        JSONObject inventoryItem = result.getJSONObject(0);

        Assertions.assertNotNull(inventoryItem.get("id"));
        Assertions.assertEquals("p1", inventoryItem.getJSONObject("product").getString("id"));
        Assertions.assertEquals(2, inventoryItem.getInt("quantity"));
        Assertions.assertEquals(2.00, inventoryItem.getDouble("pricePerItem"));
        Assertions.assertEquals(1.80, inventoryItem.getDouble("totalPrice"));
        Assertions.assertEquals("2021-04-20", inventoryItem.getString("manufactured"));
        Assertions.assertEquals("null", String.valueOf(inventoryItem.get("sellBy")));
        Assertions.assertEquals("null", String.valueOf(inventoryItem.get("bestBefore")));
        Assertions.assertEquals("2023-05-20", inventoryItem.getString("expires"));
    }

    /**
     * Tests editing a inventory item and giving it a productId that doesn't exist
     */
    @Test
    void editItemInvalidProductId() throws Exception {
        Product product = productRepository.findById(new ProductId("p1", businessId)).orElseThrow();
        InventoryItem item = new InventoryItem(product, 2, 2.00, 1.80,
                "2021-04-20", null, null, "2023-05-20");
        inventoryItemRepository.save(item);



        JSONObject testItem = new JSONObject();
        testItem.put("productId", "NotInProducts");

        RequestBuilder postInventoryRequest = MockMvcRequestBuilders
                .put("/businesses/{businessId}/inventory/{inventoryItemId}", businessId, item.getId())
                .content(testItem.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("johnxyz@gmail.com", "1337-H%nt3r2"));

        MvcResult postInventoryResponse = this.mockMvc.perform(postInventoryRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postInventoryResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new NoProductExistsException("NotInProducts", businessId).getMessage(), returnedExceptionString);
    }

    /**
     * Tests editing a inventory item and giving it a productId that is ""
     */
    @Test
    void editItemMissingProductId() throws Exception {

        Product product = productRepository.findById(new ProductId("p1", businessId)).orElseThrow();
        InventoryItem item = new InventoryItem(product, 2, 2.00, 1.80,
                "2021-04-20", null, null, "2023-05-20");
        inventoryItemRepository.save(item);

        JSONObject testItem = new JSONObject();
        testItem.put("productId", "");

        RequestBuilder postInventoryRequest = MockMvcRequestBuilders
                .put("/businesses/{businessId}/inventory/{inventoryItemId}", businessId, item.getId())
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
     * Tests editing a inventory item and giving it a quantity that is invalid
     */
    @Test
    void editItemInvalidQuantity() throws Exception {
        Product product = productRepository.findById(new ProductId("p1", businessId)).orElseThrow();
        InventoryItem item = new InventoryItem(product, 2, 2.00, 1.80,
                "2021-04-20", null, null, "2023-05-20");
        inventoryItemRepository.save(item);


        JSONObject testItem = new JSONObject();
        testItem.put("quantity", 0);

        RequestBuilder postInventoryRequest = MockMvcRequestBuilders
                .put("/businesses/{businessId}/inventory/{inventoryItemId}", businessId, item.getId())
                .content(testItem.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("johnxyz@gmail.com", "1337-H%nt3r2"));

        MvcResult postInventoryResponse = this.mockMvc.perform(postInventoryRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postInventoryResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new InvalidQuantityException().getMessage(), returnedExceptionString);
    }

    /**
     * Tests editing a inventory item and giving it a price per item that is invalid
     */
    @Test
    void editItemInvalidPrice() throws Exception {
        Product product = productRepository.findById(new ProductId("p1", businessId)).orElseThrow();
        InventoryItem item = new InventoryItem(product, 2, 2.00, 1.80,
                "2021-04-20", null, null, "2023-05-20");
        inventoryItemRepository.save(item);


        JSONObject testItem = new JSONObject();
        testItem.put("pricePerItem", -1);

        RequestBuilder postInventoryRequest = MockMvcRequestBuilders
                .put("/businesses/{businessId}/inventory/{inventoryItemId}", businessId, item.getId())
                .content(testItem.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("johnxyz@gmail.com", "1337-H%nt3r2"));

        MvcResult postInventoryResponse = this.mockMvc.perform(postInventoryRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postInventoryResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new InvalidPriceException("price per item").getMessage(), returnedExceptionString);
    }

    /**
     * Tests editing a inventory item and giving it a manufacture date that is in the future
     */
    @Test
    void editItemFutureManufacture() throws Exception {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();

        Product product = productRepository.findById(new ProductId("p1", businessId)).orElseThrow();
        InventoryItem item = new InventoryItem(product, 2, 2.00, 1.80,
                "2021-04-20", null, null, "2023-05-20");
        inventoryItemRepository.save(item);


        JSONObject testItem = new JSONObject();
        c.setTime(new Date());
        c.add(Calendar.DATE, 1); //Adds one day to the current date
        String dateString = dateFormatter.format(c.getTime());
        testItem.put("manufactured", dateString);

        RequestBuilder postInventoryRequest = MockMvcRequestBuilders
                .put("/businesses/{businessId}/inventory/{inventoryItemId}", businessId, item.getId())
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
     * Tests editing a inventory item and giving it a invalid date
     */
    @Test
    void editItemInvalidDate() throws Exception {
        Product product = productRepository.findById(new ProductId("p1", businessId)).orElseThrow();
        InventoryItem item = new InventoryItem(product, 2, 2.00, 1.80,
                "2021-04-20", null, null, "2023-05-20");
        inventoryItemRepository.save(item);


        JSONObject testItem = new JSONObject();
        testItem.put("manufactured", "A Date");
        testItem.put("sellBy", "Another Date");
        testItem.put("bestBefore", "Yet Another Date");
        testItem.put("expires", "Why not Another Date");

        RequestBuilder postInventoryRequest = MockMvcRequestBuilders
                .put("/businesses/{businessId}/inventory/{inventoryItemId}", businessId, item.getId())
                .content(testItem.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("johnxyz@gmail.com", "1337-H%nt3r2"));

        MvcResult postInventoryResponse = this.mockMvc.perform(postInventoryRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postInventoryResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new InvalidDateException().getMessage(), returnedExceptionString);
    }

    /**
     * Tests editing an inventory item and then retrieving that inventory item
     */
    @Test
    void editAndRetrieveItem() throws Exception {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();

        Product product = productRepository.findById(new ProductId("p1", businessId)).orElseThrow();
        InventoryItem item = new InventoryItem(product, 2, 2.00, 1.80,
                "2021-04-20", null, null, "2023-05-20");
        inventoryItemRepository.save(item);

        JSONObject testItem = new JSONObject();
        testItem.put("quantity", 10);
        testItem.put("pricePerItem", 3.00);
        testItem.put("totalPrice", 25.00);

        c.setTime(new Date());
        c.add(Calendar.DATE, -10);
        String manufactureString = dateFormatter.format(c.getTime());
        testItem.put("manufactured", manufactureString);

        c.setTime(new Date());
        c.add(Calendar.DATE, 11);
        String sellByString = dateFormatter.format(c.getTime());
        testItem.put("sellBy", sellByString);

        c.setTime(new Date());
        c.add(Calendar.DATE, 12);
        String bestBeforeString = dateFormatter.format(c.getTime());
        testItem.put("bestBefore", bestBeforeString);

        c.setTime(new Date());
        c.add(Calendar.DATE, 10);
        String expireString = dateFormatter.format(c.getTime());
        testItem.put("expires", expireString);


        RequestBuilder postInventoryRequest = MockMvcRequestBuilders
                .put("/businesses/{businessId}/inventory/{inventoryItemId}", businessId, item.getId())
                .content(testItem.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("johnxyz@gmail.com", "1337-H%nt3r2"));

        this.mockMvc.perform(postInventoryRequest)
                .andExpect(MockMvcResultMatchers.status().isOk()) // We expect a 200 response
                .andReturn();

        //Retrieve Inventory Item
        List<InventoryItem> items = inventoryItemRepository.findAllByBusinessId(businessId);
        assertEquals(1, items.size());
        InventoryItem retrievedItem = items.get(0);

        Assertions.assertNotNull(item.getId());
        Assertions.assertEquals("p1", retrievedItem.getProduct().getId());
        Assertions.assertEquals(10, retrievedItem.getQuantity());
        Assertions.assertEquals(3.00, retrievedItem.getPricePerItem());
        Assertions.assertEquals(25.00, retrievedItem.getTotalPrice());
        Assertions.assertEquals(manufactureString, retrievedItem.getManufactured());
        Assertions.assertEquals(sellByString, retrievedItem.getSellBy());
        Assertions.assertEquals(bestBeforeString, retrievedItem.getBestBefore());
        Assertions.assertEquals(expireString, retrievedItem.getExpires());
    }
}


