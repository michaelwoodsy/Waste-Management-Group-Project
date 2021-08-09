package gradle.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.*;
import org.seng302.project.repository_layer.repository.*;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Objects;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

/**
 * Steps for the Cucumber tests relating to U30 - Individual Full Sale Listing
 */
@Transactional
public class IndividualFullSaleListingSteps extends AbstractInitializer {
    private MockMvc mockMvc;

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final SaleListingRepository saleListingRepository;
    private final CardRepository cardRepository;
    private final LikedSaleListingRepository likedSaleListingRepository;

    User testUser;

    Business business1;
    Business business2;

    private ResultActions result;



    @Autowired
    public IndividualFullSaleListingSteps(UserRepository userRepository,
                                   BusinessRepository businessRepository,
                                   AddressRepository addressRepository,
                                   ProductRepository productRepository,
                                   InventoryItemRepository inventoryItemRepository,
                                   SaleListingRepository saleListingRepository,
                                   CardRepository cardRepository,
                                   LikedSaleListingRepository likedSaleListingRepository) {
        this.likedSaleListingRepository = likedSaleListingRepository;
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.saleListingRepository = saleListingRepository;
        this.cardRepository = cardRepository;
    }

    /**
     * Before each test, setup four sale listings with different parameters and 1 liked sale listing
     *
     */
    @BeforeEach
    @Autowired
    void setup(WebApplicationContext context) {
        //need to clear the local repositories (these are repositories only used in this test class as I used the @AutoConfigureTestDatabase annotation)
        cardRepository.deleteAll();
        likedSaleListingRepository.deleteAll();
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

        for (SaleListing listing : listings) {
            addressRepository.save(listing.getBusiness().getAddress());
            Business business = listing.getBusiness();
            businessRepository.save(business);
            Product product = listing.getInventoryItem().getProduct();
            product.setBusinessId(business.getId());
            productRepository.save(product);
            saleListingRepository.save(listing);
        }
    }

    //AC3 - can like a sale listing
    @Given("I am logged in and there is a sale listing")
    public void i_am_logged_in_and_there_is_a_sale_listing() {
        Assertions.assertFalse(saleListingRepository.findAll().isEmpty());
        Assertions.assertEquals(1, userRepository.findByEmail(testUser.getEmail()).size());
    }


    @When("I try to like the sale listing")
    public void i_try_to_like_the_sale_listing() throws Exception {
        var listingId = saleListingRepository.findAll().get(0).getId();

        mockMvc.perform(MockMvcRequestBuilders
                .put("/listings/{listingId}/like", listingId)
                .with(user(new AppUserDetails(testUser))))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Then("The sale listing is added to my list of liked sale listings")
    public void the_sale_listing_is_added_to_my_list_of_liked_sale_listings() {
        var saleListing = saleListingRepository.findAll().get(0);
        var user = userRepository.findByEmail(testUser.getEmail()).get(0);
        var likedListing = likedSaleListingRepository.findByListingAndUser(saleListing, user).get(0);
        var usersLikedListing = user.getLikedSaleListings().get(0);

        Assertions.assertEquals(saleListing, likedListing.getListing());
        Assertions.assertEquals(likedListing, usersLikedListing);
    }

    //AC5 - Any user (including myself) can “like” the listing at most once.
    @Given("I am logged in and I have already liked a sale listing")
    public void iAmLoggedInAndIHaveAlreadyLikedASaleListing() {
        testUser = userRepository.findByEmail(testUser.getEmail()).get(0);
        var saleListing = saleListingRepository.findAll().get(1);
        LikedSaleListing likedSaleListing = new LikedSaleListing(testUser, saleListing);
        likedSaleListingRepository.save(likedSaleListing);

        var usersLikedListings = testUser.getLikedSaleListings();
        usersLikedListings.add(likedSaleListing);
        testUser.setLikedSaleListings(usersLikedListings);
        userRepository.save(testUser);

        Assertions.assertEquals(1, userRepository.findByEmail(testUser.getEmail()).size());
        Assertions.assertEquals(likedSaleListingRepository.findByListingAndUser(saleListing, testUser).get(0), testUser.getLikedSaleListings().get(0));
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @When("I try to like the sale listing again")
    public void iTryToLikeTheSaleListingAgain() throws Exception {
        var listingId = saleListingRepository.findAll().get(1).getId();

        result = mockMvc.perform(MockMvcRequestBuilders
                .put("/listings/{listingId}/like", listingId)
                .with(user(new AppUserDetails(testUser))))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Then("An error is thrown")
    public void anErrorIsThrown() {
        String ErrorMessage = Objects.requireNonNull(result.andReturn().getResolvedException()).getMessage().toString();
        Assertions.assertEquals("BadRequestException: This user has already liked this sale listing", ErrorMessage);
    }
}
