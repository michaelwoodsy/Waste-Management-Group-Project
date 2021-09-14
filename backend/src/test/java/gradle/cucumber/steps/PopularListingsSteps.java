package gradle.cucumber.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.*;
import org.seng302.project.repository_layer.repository.*;
import org.seng302.project.service_layer.service.SaleListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;


/**
 * Steps for the Cucumber tests relating to U29 Browse/Search Sales Listings
 */
@AutoConfigureTestDatabase
public class PopularListingsSteps extends AbstractInitializer {

    private MockMvc mockMvc;

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final SaleListingRepository saleListingRepository;
    private final LikedSaleListingRepository likedSaleListingRepository;
    private final CardRepository cardRepository;

    private final SaleListingService saleListingService;

    User testUser1;
    User testUser2;
    User testUser3;
    User testUser4;

    SaleListing expiredListing;
    List<SaleListing> listings;

    private ResultActions result;



    @Autowired
    public PopularListingsSteps(UserRepository userRepository,
                                BusinessRepository businessRepository,
                                AddressRepository addressRepository,
                                ProductRepository productRepository,
                                InventoryItemRepository inventoryItemRepository,
                                SaleListingRepository saleListingRepository,
                                LikedSaleListingRepository likedSaleListingRepository,
                                CardRepository cardRepository,
                                SaleListingService saleListingService) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.saleListingRepository = saleListingRepository;
        this.likedSaleListingRepository = likedSaleListingRepository;
        this.cardRepository = cardRepository;
        this.saleListingService = saleListingService;
    }

    @AfterEach
    void teardown() {
        saleListingRepository.deleteAll();
        inventoryItemRepository.deleteAll();
        productRepository.deleteAll();
        businessRepository.deleteAll();
        addressRepository.deleteAll();
        cardRepository.deleteAll();
        userRepository.deleteAll();
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

        testUser1 = this.getTestUser();
        testUser2 = this.getTestOtherUser();
        testUser3 = this.getTestOtherUser();
        testUser4 = this.getTestSystemAdmin();
        addressRepository.save(testUser1.getHomeAddress());
        addressRepository.save(testUser2.getHomeAddress());
        addressRepository.save(testUser3.getHomeAddress());
        addressRepository.save(testUser4.getHomeAddress());
        testUser1 = userRepository.save(testUser1);
        testUser2 = userRepository.save(testUser2);
        testUser3 = userRepository.save(testUser3);
        testUser4 = userRepository.save(testUser4);
    }

    //AC1: up to 10 of the most liked sale listings are shown
    @Given("Sale listings exist")
    public void saleListingsExist() {
        listings = this.getSaleListings();

        for (SaleListing listing: listings) {
            addressRepository.save(listing.getBusiness().getAddress());
            Business business = listing.getBusiness();
            businessRepository.save(business);
            Product product = listing.getInventoryItem().getProduct();
            product.setBusinessId(business.getId());
            productRepository.save(product);
            inventoryItemRepository.save(listing.getInventoryItem());
            saleListingRepository.save(listing);
        }

        Assertions.assertTrue(saleListingRepository.findAll().size() > 1);
    }

    @And("Other users have liked the sales listings")
    public void otherUsersHaveLikedTheSalesListings() {
        //Listing 1 has 2 likes
        //Listing 2 has 4 likes
        //Listing 3 has 3 likes
        //Listing 4 has 1 like
        LikedSaleListing like = new LikedSaleListing(testUser1, listings.get(0));
        likedSaleListingRepository.save(like);
        testUser1.addLikedListing(like);
        like = new LikedSaleListing(testUser1, listings.get(1));
        likedSaleListingRepository.save(like);
        testUser1.addLikedListing(like);
        userRepository.save(testUser1);
        like = new LikedSaleListing(testUser1, listings.get(2));
        likedSaleListingRepository.save(like);
        testUser1.addLikedListing(like);
        userRepository.save(testUser1);
        like = new LikedSaleListing(testUser1, listings.get(3));
        likedSaleListingRepository.save(like);
        testUser1.addLikedListing(like);
        userRepository.save(testUser1);

        like = new LikedSaleListing(testUser2, listings.get(1));
        likedSaleListingRepository.save(like);
        testUser2.addLikedListing(like);
        userRepository.save(testUser2);
        like = new LikedSaleListing(testUser2, listings.get(2));
        likedSaleListingRepository.save(like);
        testUser2.addLikedListing(like);
        userRepository.save(testUser2);

        like = new LikedSaleListing(testUser3, listings.get(1));
        likedSaleListingRepository.save(like);
        testUser3.addLikedListing(like);
        userRepository.save(testUser3);
        like = new LikedSaleListing(testUser3, listings.get(2));
        likedSaleListingRepository.save(like);
        testUser3.addLikedListing(like);
        userRepository.save(testUser3);

        like = new LikedSaleListing(testUser4, listings.get(1));
        likedSaleListingRepository.save(like);
        testUser4.addLikedListing(like);
        userRepository.save(testUser4);
        like = new LikedSaleListing(testUser4, listings.get(2));
        likedSaleListingRepository.save(like);
        testUser4.addLikedListing(like);
        userRepository.save(testUser4);
    }

    @When("I retrieve the popular sale listings with no country selected")
    public void iRetrieveThePopularSaleListingsWithNoCountrySelected() throws Exception {
        result = mockMvc.perform(MockMvcRequestBuilders
                .get("/popularlistings"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Then("The most popular sale listings are retrieved, in order of most liked to least liked")
    public void theMostPopularSaleListingsAreRetrievedInOrderOfMostLikedToLeastLiked() throws Exception {
        String contentAsString = result.andReturn().getResponse().getContentAsString();
        JSONArray listings = new JSONArray(contentAsString);
        Assertions.assertEquals(4, listings.length());

        JSONObject prevListing = (JSONObject) listings.get(0);
        for(int i = 1; i < listings.length(); i++) {
            JSONObject listing = (JSONObject) listings.get(i);

            Assertions.assertTrue(prevListing.getInt("likes") >= listing.getInt("likes"));
            prevListing = listing;
        }
    }

    //AC1: up to 10 of the most liked sale listings are shown when filtering by a country
    @When("I retrieve the popular sale listings with the country {string} selected")
    public void iRetrieveThePopularSaleListingsWithTheCountrySelected(String country) throws Exception {
        result = mockMvc.perform(MockMvcRequestBuilders
                .get("/popularlistings")
                .param("country", country))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Then("the most popular sale listings for {string} are retrieved")
    public void theMostPopularSaleListingsForAreRetrieved(String country) throws Exception {
        String contentAsString = result.andReturn().getResponse().getContentAsString();
        JSONArray listings = new JSONArray(contentAsString);
        Assertions.assertEquals(2, listings.length());

        JSONObject firstListing = (JSONObject) listings.get(0);
        JSONObject secondListing = (JSONObject) listings.get(1);

        Assertions.assertEquals(country, firstListing.getJSONObject("business").getJSONObject("address").getString("country"));
        Assertions.assertEquals(country, secondListing.getJSONObject("business").getJSONObject("address").getString("country"));

        Assertions.assertTrue(firstListing.getInt("likes") >= secondListing.getInt("likes"));
    }

    //AC1: Sale listings that have not been sold and have reached their closing date are removed
    @Given("A sale listing exists")
    public void aSaleListingExists() {
        expiredListing = getSaleListings().get(0);

        addressRepository.save(expiredListing.getBusiness().getAddress());
        Business business = expiredListing.getBusiness();
        businessRepository.save(business);
        Product product = expiredListing.getInventoryItem().getProduct();
        product.setBusinessId(business.getId());
        productRepository.save(product);
        inventoryItemRepository.save(expiredListing.getInventoryItem());
        expiredListing = saleListingRepository.save(expiredListing);

        Assertions.assertNotNull(expiredListing);
        Assertions.assertEquals(1, saleListingRepository.findAll().size());
    }

    @When("The sale listing has a closing date before the current date")
    public void theSaleListingHasAClosingDateBeforeTheCurrentDate() {
        expiredListing.setCloses(LocalDateTime.now().minusSeconds(1));
        expiredListing = saleListingRepository.save(expiredListing);
    }

    @Then("The sale listing is removed")
    public void theSaleListingIsRemoved() {
        //Simulate automatic deletion
        saleListingService.deleteExpiredSaleListings();
        Assertions.assertEquals(0, saleListingRepository.findAll().size());
    }
}