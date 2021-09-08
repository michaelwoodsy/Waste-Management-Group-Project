package org.seng302.project.web_layer.controller;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.*;
import org.seng302.project.service_layer.dto.sale_listings.PostSaleListingDTO;
import org.seng302.project.service_layer.exceptions.BadRequestException;
import org.seng302.project.service_layer.exceptions.ForbiddenException;
import org.seng302.project.service_layer.exceptions.NotAcceptableException;
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
    private SaleListing listing;
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

        mockMvc.perform(post("/listings/{listingId}/buy", 1))
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
     * Check a random gets a 200 when getting a business' listings.
     */
    @Test
    void getBusinessListings_normalUser_200() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/businesses/{businessId}/listings", business.getId())
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(request).andExpect(status().isOk());
    }

    /**
     * Test a non existent business returns 406 for an authenticated user.
     */
    @Test
    void getBusinessListings_nonExistentBusiness_406() throws Exception {

        Mockito.when(saleListingService.getBusinessListings(any(Integer.class), any(AppUserDetails.class)))
                .thenThrow(new NotAcceptableException(
                        String.format("Business with ID %d does not exist", business.getId())));

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
        Mockito.doThrow(new ForbiddenException(String.format(
                        "User with id %d can not perform this action as they are not an administrator of business with id %d.",
                        testUser.getId(), business.getId()))).when(saleListingService)
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

    /**
     * Tests successful liking of a sale listing (by getting a OK response)
     */
    @Test
    void likeSaleListing_OK_200() throws Exception {
        // Create new sale listing
        listing = new SaleListing(business, inventoryItem, 15.00, null,
                LocalDateTime.now(), 1);
        listing.setId(1);

        mockMvc.perform(MockMvcRequestBuilders
                .patch("/listings/{listingId}/like", listing.getId())
                .with(user(new AppUserDetails(testUser))))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     * Tests that liking of a sale listing that has already been liked by the same user throws
     * an error (by getting a isBadRequest response)
     */
    @Test
    void likeSaleListing_alreadyLikedSaleListing_400() throws Exception {
        // Create new sale listing
        listing = new SaleListing(business, inventoryItem, 15.00, null,
                LocalDateTime.now(), 1);
        listing.setId(1);

        //Create new liked sale listing
        var likedListing = new LikedSaleListing(testUser, listing);
        likedListing.setId(1);

        Mockito.doThrow(BadRequestException.class)
                .when(saleListingService).likeSaleListing(any(Integer.class), any(AppUserDetails.class));

        mockMvc.perform(MockMvcRequestBuilders
                .patch("/listings/{listingId}/like", listing.getId())
                .with(user(new AppUserDetails(testUser))))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    /**
     * Tests that liking of a sale listing without a user throws and error (by getting a isUnauthorized response)
     */
    @Test
    void likeSaleListing_notAuthorizedUser_401() throws Exception {
        // Create new sale listing
        listing = new SaleListing(business, inventoryItem, 15.00, null,
                LocalDateTime.now(), 1);
        listing.setId(1);

        mockMvc.perform(MockMvcRequestBuilders
                .patch("/listings/{listingId}/like", listing.getId()))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    /**
     * Tests that liking of a sale listing with a sale listing ID that does not exist throws
     * an error (by getting a isNotAcceptable response)
     */
    @Test
    void likeSaleListing_nonexistentSaleListingID_406() throws Exception {
        // Create new sale listing
        listing = new SaleListing(business, inventoryItem, 15.00, null,
                LocalDateTime.now(), 1);
        listing.setId(1);

        Mockito.doThrow(NotAcceptableException.class)
                .when(saleListingService).likeSaleListing(any(Integer.class), any(AppUserDetails.class));

        mockMvc.perform(MockMvcRequestBuilders
                .patch("/listings/{listingId}/like", listing.getId() + 9999)
                .with(user(new AppUserDetails(testUser))))
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable());
    }

    /**
     * Tests that unliking a sale listing works with a valid request
     */
    @Test
    void unlikeSaleListing_validRequest_statusOK() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .patch("/listings/{listingId}/unlike", 1)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(request).andExpect(status().isOk());
    }

    /**
     * Test that trying to unlike a listing that does not exist returns a 406 status
     */
    @Test
    void unlikeSaleListing_saleListingNotFound_returns406() throws Exception {
        Mockito.doThrow(NotAcceptableException.class)
                .when(saleListingService).unlikeSaleListing(any(Integer.class), any(AppUserDetails.class));
        RequestBuilder request = MockMvcRequestBuilders
                .patch("/listings/{listingId}/unlike", 1000)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(request).andExpect(status().isNotAcceptable());
    }

    /**
     * Test that trying to unlike a listing that isn't liked returns a 400 status code
     */
    @Test
    void unlikeSaleListing_saleListingNotLiked_returns400() throws Exception {
        Mockito.doThrow(BadRequestException.class)
                .when(saleListingService).unlikeSaleListing(any(Integer.class), any(AppUserDetails.class));

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/listings/{listingId}/unlike", 1)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(request).andExpect(status().isBadRequest());
    }

    /**
     * Test successful purchase of sale listing (by getting a OK response)
     */
    @Test
    void listingPurchase_OK_200() throws Exception {
        // Create new sale listing
        SaleListing listing = new SaleListing(business, inventoryItem, 15.00, null,
                LocalDateTime.now(), 1);
        listing.setId(1000);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/listings/{listingId}/buy", listing.getId())
                .with(user(new AppUserDetails(testUser))))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    /**
     * Tests that trying to tag a sale listing when not logged in gives a 401
     */
    @Test
    void tagSaleListing_notLoggedIn_401() throws Exception {

        JSONObject body = new JSONObject();
        body.put("tag", "red");

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/listings/{listingId}/tag", 1)
                .content(body.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }


    /**
     * Tests that tagging a sale listing
     * returns a 200 response
     */
    @Test
    void tagSaleListing_valid_200() throws Exception {

        JSONObject body = new JSONObject();
        body.put("tag", "red");

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/listings/{listingId}/tag", 1)
                .content(body.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    /**
     * Tests that tagging a sale listing with an invalid tag
     * returns a 400 response
     */
    @Test
    void tagSaleListing_invalidTag_400() throws Exception {

        Mockito.doThrow(new BadRequestException("message"))
                .when(saleListingService)
                .tagSaleListing(any(Integer.class), any(String.class), any(AppUserDetails.class));

        JSONObject body = new JSONObject();
        body.put("tag", "maroon");

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/listings/{listingId}/tag", 1)
                .content(body.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

       mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    /**
     * Tests that tagging a sale listing with no tag
     * returns a 400 response
     */
    @Test
    void tagSaleListing_noTag_400() throws Exception {

        Mockito.doThrow(new BadRequestException("message"))
                .when(saleListingService)
                .tagSaleListing(any(Integer.class), any(String.class), any(AppUserDetails.class));

        JSONObject body = new JSONObject();
        body.put("tag", "tag");

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/listings/{listingId}/tag", 1)
                .content(body.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    /**
     * Tests that tagging a sale listing when not liked
     * returns a 400 response
     */
    @Test
    void tagSaleListing_notLiked_400() throws Exception {

        Mockito.doThrow(new BadRequestException("message"))
                .when(saleListingService)
                .tagSaleListing(any(Integer.class), any(String.class), any(AppUserDetails.class));

        JSONObject body = new JSONObject();
        body.put("tag", "tag");

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/listings/{listingId}/tag", 5)
                .content(body.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }


    /**
     * Tests that a 406 response is given when tagging a nonexistent listing
     */
    @Test
    void tagSaleListing_nonExistentListing_406() throws Exception {

        Mockito.doThrow(new NotAcceptableException("message"))
                .when(saleListingService)
                .tagSaleListing(any(Integer.class), any(String.class), any(AppUserDetails.class));

        JSONObject body = new JSONObject();
        body.put("tag", "tag");

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/listings/{listingId}/tag", 89892)
                .content(body.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable());
    }

    /**
     * Tests that starring a sale listing
     * returns a 200 response
     */
    @Test
    void starSaleListing_validTrue_200() throws Exception {

        JSONObject body = new JSONObject();
        body.put("star", true);

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/listings/{listingId}/star", 1)
                .content(body.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    /**
     * Tests that starring a sale listing
     * returns a 200 response
     */
    @Test
    void starSaleListing_validFalse_200() throws Exception {

        JSONObject body = new JSONObject();
        body.put("star", false);

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/listings/{listingId}/star", 1)
                .content(body.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    /**
     * Tests that starring a sale listing with an invalid star
     * returns a 400 response
     */
    @Test
    void starSaleListing_invalidStarNull_400() throws Exception {
        JSONObject body = new JSONObject();
        body.put("star", null);

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/listings/{listingId}/star", 1)
                .content(body.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    /**
     * Tests that starring a sale listing with an invalid star
     * returns a 400 response
     */
    @Test
    void starSaleListing_invalidStarString_400() throws Exception {
        JSONObject body = new JSONObject();
        body.put("star", "blah");

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/listings/{listingId}/star", 1)
                .content(body.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    /**
     * Tests that starring a sale listing when not liked
     * returns a 400 response
     */
    @Test
    void starSaleListing_notLiked_400() throws Exception {

        Mockito.doThrow(new BadRequestException("message"))
                .when(saleListingService)
                .starSaleListing(any(Integer.class), any(Boolean.class), any(AppUserDetails.class));

        JSONObject body = new JSONObject();
        body.put("star", true);

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/listings/{listingId}/star", 5)
                .content(body.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    /**
     * Tests that a 406 response is given when starring a nonexistent listing
     */
    @Test
    void starSaleListing_nonExistentListing_406() throws Exception {

        Mockito.doThrow(new NotAcceptableException("message"))
                .when(saleListingService)
                .starSaleListing(any(Integer.class), any(Boolean.class), any(AppUserDetails.class));

        JSONObject body = new JSONObject();
        body.put("star", true);

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/listings/{listingId}/star", 89892)
                .content(body.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable());
    }

    /**
     * Tests that featuring a sale listing
     * returns a 200 response OK
     */
    @Test
    void featureSaleListing_validTrue_200() throws Exception {
        JSONObject body = new JSONObject();
        body.put("featured", true);

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/businesses/{businessId}/listings/{listingId}/feature", business.getId(), 1)
                .content(body.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(owner)));

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     * Tests that featuring a sale listing
     * returns a 200 response OK
     */
    @Test
    void featureSaleListing_validFalse_200() throws Exception {
        JSONObject body = new JSONObject();
        body.put("featured", false);

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/businesses/{businessId}/listings/{listingId}/feature", business.getId(), 1)
                .content(body.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(owner)));

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     * Tests that featuring a sale listing with an invalid featured
     * returns a 400 response Bad Request Exception
     */
    @Test
    void featureSaleListing_invalidFeaturedString_400() throws Exception {
        JSONObject body = new JSONObject();
        body.put("featured", "blah");

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/businesses/{businessId}/listings/{listingId}/feature", business.getId(), 1)
                .content(body.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(owner)));

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    /**
     * Tests that when a not logged in user features a sale listing
     * returns a 401 response Unauthorized Exception
     */
    @Test
    void featureSaleListing_notLoggedIn_401() throws Exception {
        JSONObject body = new JSONObject();
        body.put("featured", true);

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/businesses/{businessId}/listings/{listingId}/feature", business.getId(), 1)
                .content(body.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests that when a user who is not the admin of the given business ID features a sale listing
     * returns a 403 response Forbidden Exception
     */
    @Test
    void featureSaleListing_notAdmin_403() throws Exception {
        JSONObject body = new JSONObject();
        body.put("featured", true);

        Mockito.doThrow(new ForbiddenException(String.format(
                "User with id %d can not perform this action as they are not an administrator of business with id %d.",
                testUser.getId(), business.getId()))).when(saleListingService)
                .featureSaleListing(any(Integer.class), any(Integer.class), any(boolean.class), any(AppUserDetails.class));

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/businesses/{businessId}/listings/{listingId}/feature", business.getId(), 1)
                .content(body.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(request)
                .andExpect(status().isForbidden());
    }

    /**
     * Tests that when a user features a sale listing with an invalid business ID
     * returns a 406 response Not Acceptable Exception
     */
    @Test
    void featureSaleListings_nonExistentBusiness_406() throws Exception {
        JSONObject body = new JSONObject();
        body.put("featured", true);

        Mockito.doThrow(new NotAcceptableException(
                String.format("Business with ID %d does not exist", 9999))).when(saleListingService)
                .featureSaleListing(any(Integer.class), any(Integer.class), any(boolean.class), any(AppUserDetails.class));

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/businesses/{businessId}/listings/{listingId}/feature", 9999, 1)
                .content(body.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(request)
                .andExpect(status().isNotAcceptable());
    }

    /**
     * Tests that when a user features a sale listing with an invalid sale listing ID
     * returns a 406 response Not Acceptable Exception
     */
    @Test
    void featureSaleListings_nonExistentListing_406() throws Exception {
        JSONObject body = new JSONObject();
        body.put("featured", true);

        Mockito.doThrow(new NotAcceptableException(
                String.format("No sale Listing with ID %d exists", 9999))).when(saleListingService)
                .featureSaleListing(any(Integer.class), any(Integer.class), any(boolean.class), any(AppUserDetails.class));

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/businesses/{businessId}/listings/{listingId}/feature", business.getId(), 9999)
                .content(body.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(request)
                .andExpect(status().isNotAcceptable());
    }
}
