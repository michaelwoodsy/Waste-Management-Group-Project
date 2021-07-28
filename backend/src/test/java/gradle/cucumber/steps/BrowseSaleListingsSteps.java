package gradle.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.*;
import org.seng302.project.repository_layer.repository.*;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


/**
 * Steps for the Cucumber tests relating to U29 Browse/Search Sales Listings
 */
@AutoConfigureTestDatabase
public class BrowseSaleListingsSteps extends AbstractInitializer {

    private MockMvc mockMvc;

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final SaleListingRepository saleListingRepository;
    private final CardRepository cardRepository;

    User testUser;

    Business business1;
    Business business2;

    private ResultActions result;



    @Autowired
    public BrowseSaleListingsSteps(UserRepository userRepository,
                                   BusinessRepository businessRepository,
                                   AddressRepository addressRepository,
                                   ProductRepository productRepository,
                                   InventoryItemRepository inventoryItemRepository,
                                   SaleListingRepository saleListingRepository,
                                   CardRepository cardRepository) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.saleListingRepository = saleListingRepository;
        this.cardRepository = cardRepository;
    }

    /**
     * Before each test, setup four sale listings with different parameters
     */
    @BeforeEach
    @Autowired
    void setup(WebApplicationContext context) {
        //need to clear the local repositories (these are repositories only used in this test class as I used the @AutoConfigureTestDatabase annotation)
        cardRepository.deleteAll();
        userRepository.deleteAll();
        saleListingRepository.deleteAll();
        inventoryItemRepository.deleteAll();
        productRepository.deleteAll();
        businessRepository.deleteAll();
        addressRepository.deleteAll();

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        this.initialise();
        testUser = this.getTestUser();
        addressRepository.save(testUser.getHomeAddress());
        userRepository.save(testUser);

        List<SaleListing> listings = this.getSaleListings();

        for (SaleListing listing: listings) {
            addressRepository.save(listing.getBusiness().getAddress());
            Business business = listing.getBusiness();
            businessRepository.save(business);
            Product product = listing.getInventoryItem().getProduct();
            product.setBusinessId(business.getId());
            productRepository.save(product);
            saleListingRepository.save(listing);
        }
    }

    //AC2
    @Given("I am logged in")
    public void i_am_logged_in() {
        Assertions.assertEquals(1, userRepository.findByEmail(testUser.getEmail()).size());
    }

    @When("I search sales listings with no filters")
    public void i_search_sales_listings_with_no_filters() throws Exception{
        result = mockMvc.perform(MockMvcRequestBuilders
                .get("/listings")
                .param("searchQuery", "")
                .param("matchingProductName", String.valueOf(false))
                .param("matchingBusinessName", String.valueOf(false))
                .param("matchingBusinessLocation", String.valueOf(false))
                .param("matchingBusinessType", String.valueOf(false))
                .param("pageNumber", String.valueOf(0))
                .param("sortBy", "").with(user(new AppUserDetails(testUser))))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Then("All sales listings are returned")
    public void all_sales_listings_are_returned() throws Exception {
        String contentAsString = result.andReturn().getResponse().getContentAsString();
        JSONArray results = new JSONArray(contentAsString);

        JSONArray listings = (JSONArray) results.get(0);

        Assertions.assertEquals(4, results.get(1));
    }

    //AC4
    @When("I search sales listings, sorting by price ascending")
    public void i_search_sales_listings_sorting_by_price_ascending() throws Exception {
        result = mockMvc.perform(MockMvcRequestBuilders
                .get("/listings")
                .param("searchQuery", "")
                .param("matchingProductName", String.valueOf(false))
                .param("matchingBusinessName", String.valueOf(false))
                .param("matchingBusinessLocation", String.valueOf(false))
                .param("matchingBusinessType", String.valueOf(false))
                .param("pageNumber", String.valueOf(0))
                .param("sortBy", "priceAsc").with(user(new AppUserDetails(testUser))))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Then("All sales listings are returned in price ascending order")
    public void all_sales_listings_are_returned_in_price_ascending_order() throws Exception {
        String contentAsString = result.andReturn().getResponse().getContentAsString();
        JSONArray results = new JSONArray(contentAsString);
        Assertions.assertEquals(4, results.get(1));

        JSONArray listings = (JSONArray) results.get(0);
        double prevPrice = 0.00;
        for(int i = 0; i < listings.length(); i++) {
            JSONObject listing = (JSONObject) listings.get(i);
            double price = Double.parseDouble(listing.get("price").toString());
            Assertions.assertTrue(prevPrice <= price);
            prevPrice = price;
        }
    }

    //AC4
    @When("I search sales listings, sorting by price descending")
    public void i_search_sales_listings_sorting_by_price_descending() throws Exception {
        result = mockMvc.perform(MockMvcRequestBuilders
                .get("/listings")
                .param("searchQuery", "")
                .param("matchingProductName", String.valueOf(false))
                .param("matchingBusinessName", String.valueOf(false))
                .param("matchingBusinessLocation", String.valueOf(false))
                .param("matchingBusinessType", String.valueOf(false))
                .param("pageNumber", String.valueOf(0))
                .param("sortBy", "priceDesc").with(user(new AppUserDetails(testUser))))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Then("All sales listings are returned in price descending order")
    public void all_sales_listings_are_returned_in_price_descending_order() throws Exception {
        String contentAsString = result.andReturn().getResponse().getContentAsString();
        JSONArray results = new JSONArray(contentAsString);
        Assertions.assertEquals(4, results.get(1));

        JSONArray listings = (JSONArray) results.get(0);
        double prevPrice = Double.POSITIVE_INFINITY;
        for(int i = 0; i < listings.length(); i++) {
            JSONObject listing = (JSONObject) listings.get(i);
            double price = Double.parseDouble(listing.get("price").toString());
            Assertions.assertTrue(prevPrice >= price);
            prevPrice = price;
        }
    }

    //AC4
    @When("I search sales listings, sorting by product name")
    public void i_search_sales_listings_sorting_by_product_name() throws Exception {
        result = mockMvc.perform(MockMvcRequestBuilders
                .get("/listings")
                .param("searchQuery", "")
                .param("matchingProductName", String.valueOf(false))
                .param("matchingBusinessName", String.valueOf(false))
                .param("matchingBusinessLocation", String.valueOf(false))
                .param("matchingBusinessType", String.valueOf(false))
                .param("pageNumber", String.valueOf(0))
                .param("sortBy", "productName").with(user(new AppUserDetails(testUser))))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Then("All sales listings are returned, ordered by product name")
    public void all_sales_listings_are_returned_ordered_by_product_name() throws Exception{
        String contentAsString = result.andReturn().getResponse().getContentAsString();
        JSONArray results = new JSONArray(contentAsString);
        Assertions.assertEquals(4, results.get(1));

        JSONArray listings = (JSONArray) results.get(0);
        String prevName = "";
        for(int i = 0; i < listings.length(); i++) {
            JSONObject listing = (JSONObject) listings.get(i);
            String name = listing.getJSONObject("inventoryItem").getJSONObject("product").get("name").toString();
            if (i == 0) {
                prevName = name;
            } else {
                Assertions.assertTrue(prevName.compareTo(name) <= 0);
                prevName = name;
            }
        }
    }

    //AC5
    @When("I search sales listings, filtering by business type {string}")
    public void i_search_sales_listings_filtering_by_business_type(String string) throws Exception {
        result = mockMvc.perform(MockMvcRequestBuilders
                .get("/listings")
                .param("searchQuery", string)
                .param("matchingProductName", String.valueOf(false))
                .param("matchingBusinessName", String.valueOf(false))
                .param("matchingBusinessLocation", String.valueOf(false))
                .param("matchingBusinessType", String.valueOf(true))
                .param("pageNumber", String.valueOf(0))
                .param("sortBy", "").with(user(new AppUserDetails(testUser))))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Then("Sale listings are returned that correspond to a business with type Retail Trade")
    public void sale_listings_are_returned_that_correspond_to_a_business_with_type_retail_trade() throws Exception {
        String contentAsString = result.andReturn().getResponse().getContentAsString();
        JSONArray results = new JSONArray(contentAsString);
        Assertions.assertEquals(2, results.get(1));

        JSONArray listings = (JSONArray) results.get(0);
        for(int i = 0; i < listings.length(); i++) {
            JSONObject listing = (JSONObject) listings.get(i);

            Assertions.assertEquals("Retail Trade", listing.getJSONObject("business").get("businessType").toString());
        }
    }

    //AC6
    @When("I search sales listings, filtering by part of product name {string}")
    public void i_search_sales_listings_filtering_by_part_of_product_name(String string) throws Exception{
        result = mockMvc.perform(MockMvcRequestBuilders
                .get("/listings")
                .param("searchQuery", string)
                .param("matchingProductName", String.valueOf(true))
                .param("matchingBusinessName", String.valueOf(false))
                .param("matchingBusinessLocation", String.valueOf(false))
                .param("matchingBusinessType", String.valueOf(false))
                .param("pageNumber", String.valueOf(0))
                .param("sortBy", "").with(user(new AppUserDetails(testUser))))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Then("One sales listing is returned that has {string} in its product name")
    public void one_sales_listing_is_returned_that_has_in_its_product_name(String string) throws Exception{
        String contentAsString = result.andReturn().getResponse().getContentAsString();
        JSONArray results = new JSONArray(contentAsString);
        Assertions.assertEquals(1, results.get(1));

        JSONArray listings = (JSONArray) results.get(0);
        JSONObject listing = (JSONObject) listings.get(0);
        String name = listing.getJSONObject("inventoryItem").getJSONObject("product").get("name").toString().toLowerCase();

        Assertions.assertTrue(name.contains(string));
    }


    //AC7
    @When("I search sales listings, filtering by price in between {double} and {double}")
    public void i_search_sales_listings_filtering_by_price_in_between_and(Double lowerPrice, Double upperPrice) throws Exception{
        result = mockMvc.perform(MockMvcRequestBuilders
                .get("/listings")
                .param("searchQuery", "")
                .param("matchingProductName", String.valueOf(false))
                .param("matchingBusinessName", String.valueOf(false))
                .param("matchingBusinessLocation", String.valueOf(false))
                .param("matchingBusinessType", String.valueOf(false))
                .param("priceRangeLower", String.valueOf(lowerPrice))
                .param("priceRangeUpper", String.valueOf(upperPrice))
                .param("pageNumber", String.valueOf(0))
                .param("sortBy", "").with(user(new AppUserDetails(testUser))))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Then("Three sale listings are returned that have a price in between {double} and {double} inclusive")
    public void three_sale_listings_are_returned_that_have_a_price_in_between_and_inclusive(Double lowerPrice, Double upperPrice) throws Exception{
        String contentAsString = result.andReturn().getResponse().getContentAsString();
        JSONArray results = new JSONArray(contentAsString);
        Assertions.assertEquals(3, results.get(1));

        JSONArray listings = (JSONArray) results.get(0);
        for(int i = 0; i < listings.length(); i++) {
            JSONObject listing = (JSONObject) listings.get(i);
            Double price = Double.parseDouble(listing.get("price").toString());

            Assertions.assertTrue(lowerPrice <= price && price <= upperPrice);
        }
    }

    //AC8
    @When("I search sales listings, filtering by part of seller name {string}")
    public void i_search_sales_listings_filtering_by_part_of_seller_name(String string) throws Exception{
        result = mockMvc.perform(MockMvcRequestBuilders
                .get("/listings")
                .param("searchQuery", string)
                .param("matchingProductName", String.valueOf(false))
                .param("matchingBusinessName", String.valueOf(true))
                .param("matchingBusinessLocation", String.valueOf(false))
                .param("matchingBusinessType", String.valueOf(false))
                .param("pageNumber", String.valueOf(0))
                .param("sortBy", "").with(user(new AppUserDetails(testUser))))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Then("Two sales listing is returned that belong to the business named {string}")
    public void two_sales_listing_is_returned_that_belong_to_the_business_named(String businessName) throws Exception{
        String contentAsString = result.andReturn().getResponse().getContentAsString();
        JSONArray results = new JSONArray(contentAsString);
        Assertions.assertEquals(2, results.get(1));

        JSONArray listings = (JSONArray) results.get(0);
        for(int i = 0; i < listings.length(); i++) {
            JSONObject listing = (JSONObject) listings.get(i);
            String name = listing.getJSONObject("business").get("name").toString();

            Assertions.assertEquals(businessName, name);
        }
    }

    //AC9
    @When("I search sales listings, filtering by part of seller location {string}")
    public void i_search_sales_listings_filtering_by_part_of_seller_location(String string) throws Exception{
        result = mockMvc.perform(MockMvcRequestBuilders
                .get("/listings")
                .param("searchQuery", string)
                .param("matchingProductName", String.valueOf(false))
                .param("matchingBusinessName", String.valueOf(false))
                .param("matchingBusinessLocation", String.valueOf(true))
                .param("matchingBusinessType", String.valueOf(false))
                .param("pageNumber", String.valueOf(0))
                .param("sortBy", "").with(user(new AppUserDetails(testUser))))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    //AC10
    @When("I search sales listings, filtering by closing date in between {string} and {string}")
    public void i_search_sales_listings_filtering_by_closing_date_in_between_and(String dateLower, String dateUpper) throws Exception{
        result = mockMvc.perform(MockMvcRequestBuilders
                .get("/listings")
                .param("searchQuery", "")
                .param("matchingProductName", String.valueOf(false))
                .param("matchingBusinessName", String.valueOf(false))
                .param("matchingBusinessLocation", String.valueOf(false))
                .param("matchingBusinessType", String.valueOf(false))
                .param("closingDateLower", String.valueOf(dateLower))
                .param("closingDateUpper", String.valueOf(dateUpper))
                .param("pageNumber", String.valueOf(0))
                .param("sortBy", "").with(user(new AppUserDetails(testUser))))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Then("Two sale listings are returned that have a closing date in between {string} and {string}")
    public void two_sale_listings_are_returned_that_have_a_closing_date_in_between_and(String dateLower, String dateUpper) throws Exception{
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        LocalDateTime closingDateLower = formatter.parse(dateLower).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime closingDateUpper = formatter.parse(dateUpper).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        String contentAsString = result.andReturn().getResponse().getContentAsString();
        JSONArray results = new JSONArray(contentAsString);
        Assertions.assertEquals(2, results.get(1));

        JSONArray listings = (JSONArray) results.get(0);
        for(int i = 0; i < listings.length(); i++) {
            JSONObject listing = (JSONObject) listings.get(i);
            String dateStr = listing.get("closes").toString();
            LocalDateTime closesDate = formatter.parse(dateStr).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

            Assertions.assertTrue(closingDateLower.isBefore(closesDate) && closingDateUpper.isAfter(closesDate));
        }
    }

}