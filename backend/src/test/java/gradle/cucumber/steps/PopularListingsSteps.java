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
import org.seng302.project.repository_layer.model.Business;
import org.seng302.project.repository_layer.model.Product;
import org.seng302.project.repository_layer.model.SaleListing;
import org.seng302.project.repository_layer.model.User;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
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
    private final CardRepository cardRepository;

    User testUser;

    Business business1;
    Business business2;

    private ResultActions result;



    @Autowired
    public PopularListingsSteps(UserRepository userRepository,
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
            inventoryItemRepository.save(listing.getInventoryItem());
            saleListingRepository.save(listing);
        }
    }

    //AC1: up to 10 of the most liked sale listings are shown
    @Given("More than {int} sale listings exist")
    public void moreThanSaleListingsExist(int numSaleListings) {
    }

    @And("Other users have liked the sales listings")
    public void otherUsersHaveLikedTheSalesListings() {

    }

    @When("I retrieve the popular sale listings with no country selected")
    public void iRetrieveThePopularSaleListingsWithNoCountrySelected() {

    }

    @Then("the {int} most popular sale listings are retrieved")
    public void theMostPopularSaleListingsAreRetrieved(int numSaleListings) {
    }

    //AC1: up to 10 of the most liked sale listings are shown when filtering by a country
    @When("I retrieve the popular sale listings with the country {string} selected")
    public void iRetrieveThePopularSaleListingsWithTheCountrySelected(String arg0) {
    }

    @Then("the most popular sale listings for New Zealand are retrieved")
    public void theMostPopularSaleListingsForNewZealandAreRetrieved() {
    }

    //AC1: Sale listings that have not been sold and have reached their closing date are removed
    @Given("A sale listing exists")
    public void aSaleListingExists() {
    }

    @When("The sale listing has a closing date before the current date")
    public void theSaleListingHasAClosingDateBeforeTheCurrentDate() {
    }

    @Then("The sale listing is removed")
    public void theSaleListingIsRemoved() {
    }
}