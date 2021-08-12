package gradle.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.*;
import org.seng302.project.repository_layer.model.enums.Tag;
import org.seng302.project.repository_layer.repository.*;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;
import java.util.Collections;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;


/**
 * Steps for the Cucumber tests relating to U32 Managing my feed
 */
@AutoConfigureTestDatabase
public class ManagingMyFeedSteps extends AbstractInitializer {

    private MockMvc mockMvc;

    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final BusinessRepository businessRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final SaleListingRepository saleListingRepository;
    private final LikedSaleListingRepository likedSaleListingRepository;

    private SaleListing listing;
    private User testUser;

    @Autowired
    public ManagingMyFeedSteps(UserRepository userRepository,
                                     CardRepository cardRepository,
                                     BusinessRepository businessRepository,
                                     AddressRepository addressRepository,
                                     ProductRepository productRepository,
                                     InventoryItemRepository inventoryItemRepository,
                                     SaleListingRepository saleListingRepository,
                                     LikedSaleListingRepository likedSaleListingRepository) {
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
        this.businessRepository = businessRepository;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.saleListingRepository = saleListingRepository;
        this.likedSaleListingRepository = likedSaleListingRepository;
    }

    /**
     * Before each test, setup four sale listings with different parameters
     */
    @BeforeEach
    @Autowired
    void setup(WebApplicationContext context) {
        var users = userRepository.findAll();
        for (var user: users) {
            user.setLikedSaleListings(Collections.emptyList());
            userRepository.save(user);
        }
        likedSaleListingRepository.deleteAll();
        cardRepository.deleteAll();
        userRepository.deleteAll();

        this.initialise();
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        testUser = this.getTestUser();
        addressRepository.save(testUser.getHomeAddress());
        testUser.setId(null);
        userRepository.save(testUser);

        listing = this.getSaleListings().get(0);

        addressRepository.save(listing.getBusiness().getAddress());
        Business business = listing.getBusiness();
        business.setId(null);
        business = businessRepository.save(business);
        Product product = listing.getInventoryItem().getProduct();
        product.setBusinessId(business.getId());
        productRepository.save(product);
        inventoryItemRepository.save(listing.getInventoryItem());
        listing = saleListingRepository.save(listing);
    }

    @AfterEach
    void teardown() {
        likedSaleListingRepository.deleteAll();
        saleListingRepository.deleteAll();
        inventoryItemRepository.deleteAll();
        productRepository.deleteAll();
        businessRepository.deleteAll();
        addressRepository.deleteAll();
        cardRepository.deleteAll();
        userRepository.deleteAll();
    }

    //AC1

//    @Given("I have a message on my home page")
//    public void i_have_a_message_on_my_home_page() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    @When("I attempt to delete the message from my home page")
//    public void i_attempt_to_delete_the_message_from_my_home_page() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    @Then("The message is successfully removed from my home page")
//    public void the_message_is_successfully_removed_from_my_home_page() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    @Given("I have a notification on my home page")
//    public void i_have_a_notification_on_my_home_page() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    @When("I attempt to delete the notification from my home page")
//    public void i_attempt_to_delete_the_notification_from_my_home_page() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//    @Then("The notification is successfully removed from my home page")
//    public void the_notification_is_successfully_removed_from_my_home_page() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }

    //AC2

//    @Given("I have an unread message")
//    public void i_have_an_unread_message() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    @When("I click on \\(read) the message")
//    public void i_click_on_read_the_message() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    @Then("The message is marked as read")
//    public void the_message_is_marked_as_read() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    @Given("I have an unread notification")
//    public void i_have_an_unread_notification() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//    @When("I click on \\(read) the notification")
//    public void i_click_on_read_the_notification() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    @Then("The notification is marked as read")
//    public void the_notification_is_marked_as_read() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    @Given("I have a new message")
//    public void i_have_a_new_message() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    @When("I see the message on my home page")
//    public void i_see_the_message_on_my_home_page() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//    @Then("The message is marked as unread")
//    public void the_message_is_marked_as_unread() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    @Given("I have a new notification")
//    public void i_have_a_new_notification() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    @When("I see the notification on my home page")
//    public void i_see_the_notification_on_my_home_page() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    @Then("The notification is marked as unread")
//    public void the_notification_is_marked_as_unread() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }

    //AC3

    @Given("I have a liked sale listing")
    public void i_have_a_liked_sale_listing() {
        LikedSaleListing likedListing = new LikedSaleListing(testUser, listing);
        likedSaleListingRepository.save(likedListing);
        testUser.addLikedListing(likedListing);
        userRepository.save(testUser);
    }
//
//    @When("I star the liked sale listing")
//    public void i_star_the_liked_sale_listing() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    @Then("The liked sale listing is marked as starred")
//    public void the_liked_sale_listing_is_marked_as_starred() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    @Then("The listing appears at the top of my feed")
//    public void the_listing_appears_at_the_top_of_my_feed() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }

    //AC6

    @When("I tag the listing with the colour {string}")
    public void i_tag_the_listing_with_the_colour(String colour) throws Exception {
        JSONObject body = new JSONObject();
        body.put("tag", colour);

        mockMvc.perform(MockMvcRequestBuilders
                .patch("/listings/{listingId}/tag", listing.getId())
                .content(body.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser))))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Then("The liked sale listing is marked with a {string} tag")
    public void the_liked_sale_listing_is_marked_with_a_tag(String colour) {
        LikedSaleListing updatedLikedListing = likedSaleListingRepository
                .findByListingAndUser(listing, testUser).get(0);

        Assertions.assertTrue(updatedLikedListing.getTag().matchesTag(colour));
    }

    //AC7
    @Given("I have a liked sale listing tagged as {string}")
    @Transactional
    public void i_have_a_liked_sale_listing_tagged_as(String colour) {
        var userOptional = userRepository.findById(testUser.getId());
        Assertions.assertTrue(userOptional.isPresent());
        var user = userOptional.get();
        LikedSaleListing likedListing = new LikedSaleListing(user, listing);
        likedListing.setTag(Tag.getTag(colour));
        likedSaleListingRepository.save(likedListing);
        user.addLikedListing(likedListing);
        testUser = userRepository.save(user);

    }

//    @When("I filter my feed by {string}")
//    public void i_filter_my_feed_by(String string) {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    @Then("Only the listing tagged as {string} is shown on my feed")
//    public void only_the_listing_tagged_as_is_shown_on_my_feed(String string) {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    @When("I clear the tag filter")
//    public void i_clear_the_tag_filter() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }

//    @Then("Both the listings tagged as {string} and {string} are shown on my feed")
//    public void both_the_listings_tagged_as_and_are_shown_on_my_feed(String string, String string2) {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }

}
