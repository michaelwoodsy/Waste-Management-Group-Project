package org.seng302.project.web_layer.controller;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.*;
import org.seng302.project.service_layer.dto.sale_listings.PostSaleListingDTO;
import org.seng302.project.service_layer.exceptions.*;
import org.seng302.project.service_layer.exceptions.business.BusinessNotFoundException;
import org.seng302.project.service_layer.exceptions.businessAdministrator.ForbiddenAdministratorActionException;
import org.seng302.project.service_layer.service.SaleListingService;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SaleListingControllerTest extends AbstractInitializer {

    private User testUser;
    private User owner;
    private User systemAdmin;

    private Business business;

    private InventoryItem inventoryItem;


    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private SaleListingService saleListingService;

    @BeforeEach
    public void setup() {
        testUser = this.getTestUser();
        owner = this.getTestUserBusinessAdmin();
        systemAdmin = this.getTestSystemAdmin();

        business = this.getTestBusiness();

        Product product = this.getTestProduct();

        inventoryItem = new InventoryItem(product, 20,
                10.99, 219.8, "2021-04-25",
                "2021-04-25", "2021-04-25", "2021-04-25");
        inventoryItem.setId(1);

    }

    /**
     * Tests that getting listings from a business
     * when not logged in gives a 401 response
     */
    @Test
    void getBusinessListings_notLoggedIn_401() throws Exception {
        mockMvc.perform(get("/businesses/{id}/listings", business.getId()))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests that creating a listing
     * when not logged in gives a 401 response
     */
    @Test
    void postBusinessListings_notLoggedIn_401() throws Exception {
        mockMvc.perform(post("/businesses/{id}/listings", business.getId()))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Check a user that is a business admin gets a 200 when getting their listings.
     */
    @Test
    void getBusinessListings_asBusinessAdmin_200() throws Exception {

        RequestBuilder request = MockMvcRequestBuilders
                .get("/businesses/{businessId}/listings", business.getId())
                .with(user(new AppUserDetails(owner)));

        mockMvc.perform(request).andExpect(status().isOk());
    }

    /**
     * Check a system admin gets a 200 when getting a business' listings.
     */
    @Test
    void getBusinessListings_asSystemAdmin_200() throws Exception {

        RequestBuilder request = MockMvcRequestBuilders
                .get("/businesses/{businessId}/listings", business.getId())
                .with(user(new AppUserDetails(systemAdmin)));

        mockMvc.perform(request).andExpect(status().isOk());
    }

    /**
     * Test a non existent business returns 406 for an authenticated user.
     */
    @Test
    void getBusinessListings_nonExistentBusiness_406() throws Exception {

        Mockito.when(saleListingService.getBusinessListings(any(Integer.class), any(AppUserDetails.class)))
                .thenThrow(new BusinessNotFoundException(business.getId()));

        RequestBuilder request = MockMvcRequestBuilders
                .get("/businesses/{businessId}/listings", business.getId() + 9999)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(request).andExpect(status().isNotAcceptable());
    }

    /**
     * Test creating a sales listing with a random user (not admin)
     */
    @Test
    void createSaleListing_notAdmin_403() throws Exception {
        LocalDateTime closesDate = LocalDateTime.now();
        closesDate = closesDate.plusDays(10);

        JSONObject testItem = new JSONObject();
        testItem.put("inventoryItemId", inventoryItem.getId());
        testItem.put("quantity", inventoryItem.getQuantity() - 1);
        testItem.put("price", 59.99);
        testItem.put("moreInfo", "Some more info about this listing");
        testItem.put("closes", closesDate.toString());
        Mockito.doThrow(new ForbiddenAdministratorActionException(business.getId())).when(saleListingService)
                .newBusinessListing(any(PostSaleListingDTO.class), any(Integer.class), any(AppUserDetails.class));

        RequestBuilder postListingRequest = MockMvcRequestBuilders
                .post("/businesses/{businessId}/listings", business.getId())
                .content(testItem.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(postListingRequest)
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    /**
     * Test creating a sales listing with an invalid inventoryItemId.
     */
    @Test
    void createSaleListing_invalidInventoryItemId_400() throws Exception {
        LocalDateTime closesDate = LocalDateTime.now();
        closesDate = closesDate.plusDays(10);

        JSONObject testItem = new JSONObject();
        testItem.put("inventoryItemId", 254645756);
        testItem.put("quantity", 6);
        testItem.put("price", 59.99);
        testItem.put("moreInfo", "Some more info about this listing");
        testItem.put("closes", closesDate.toString());

        Mockito.doThrow(new BadRequestException(String.format(
                "BadRequestException: No inventory item with id 254645756 exists in business with id %d.",
                business.getId())))
                .when(saleListingService)
                .newBusinessListing(any(PostSaleListingDTO.class), any(Integer.class), any(AppUserDetails.class));

        RequestBuilder postListingRequest = MockMvcRequestBuilders
                .post("/businesses/{businessId}/listings", business.getId())
                .content(testItem.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(owner)));

       mockMvc.perform(postListingRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    /**
     * Test creating a sales listing with no quantity.
     * Expects a 400 with a message from the DTO.
     */
    @Test
    void createSaleListing_missingQuantity_400() throws Exception {
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
                .with(user(new AppUserDetails(owner)));

        MvcResult postInventoryResponse = this.mockMvc.perform(postListingRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postInventoryResponse.getResponse().getContentAsString();
        Assertions.assertEquals("MethodArgumentNotValidException: Quantity is a mandatory field.", returnedExceptionString);
    }

    /**
     * Test creating a sales listing with no price.
     * Expects a 400 with a message from the DTO.
     */
    @Test
    void createSaleListing_missingPrice_400() throws Exception {
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
                .with(user(new AppUserDetails(owner)));

        MvcResult postInventoryResponse = this.mockMvc.perform(postListingRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postInventoryResponse.getResponse().getContentAsString();
        Assertions.assertEquals("MethodArgumentNotValidException: Price is a mandatory field.", returnedExceptionString);
    }

    /**
     * Test creating a sales listing with too many items (not enough quantity of inventory item).
     */
    @Test
    void createSaleListing_tooManyItems_400() throws Exception {
        LocalDateTime closesDate = LocalDateTime.now();
        closesDate = closesDate.plusDays(10);

        JSONObject testItem = new JSONObject();
        testItem.put("inventoryItemId", inventoryItem.getId());
        testItem.put("quantity", inventoryItem.getQuantity() + 1);
        testItem.put("price", 59.99);
        testItem.put("moreInfo", "Some more info about this listing");
        testItem.put("closes", closesDate.toString());

        Mockito.doThrow(new BadRequestException(String.format(
                "You do not have enough of item with id %d for this listing (you have %d, with %d used in other sale listings).",
                inventoryItem.getId(), 5, 6))).when(saleListingService)
                .newBusinessListing(any(PostSaleListingDTO.class), any(Integer.class), any(AppUserDetails.class));

        RequestBuilder postListingRequest = MockMvcRequestBuilders
                .post("/businesses/{businessId}/listings", business.getId())
                .content(testItem.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(owner)));

        mockMvc.perform(postListingRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()); // We expect a 400 response
    }

    /**
     * Test creating a sale listing gives a 201 response.
     */
    @Test
    void createSalesListing_201() throws Exception {
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
                .with(user(new AppUserDetails(owner)));

        mockMvc.perform(postListingRequest)
                .andExpect(MockMvcResultMatchers.status().isCreated());
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
                .param("sortBy", "").with(user(new AppUserDetails(testUser))))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
