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
import org.seng302.project.repository_layer.repository.AddressRepository;
import org.seng302.project.repository_layer.repository.BusinessRepository;
import org.seng302.project.repository_layer.repository.SaleListingRepository;
import org.seng302.project.repository_layer.repository.UserRepository;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Steps for the Cucumber tests relating to UD3 - Featured Business Listings
 */
@Transactional
@AutoConfigureTestDatabase
public class FeaturedBusinessListingsSteps extends AbstractInitializer {

    private MockMvc mockMvc;
    private RequestBuilder request;
    private final SaleListingRepository saleListingRepository;
    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    private User testBusinessAdmin;
    private Business testBusiness;
    private Integer numListings;

    @Autowired
    public FeaturedBusinessListingsSteps(SaleListingRepository saleListingRepository,
                                         BusinessRepository businessRepository,
                                         UserRepository userRepository,
                                         AddressRepository addressRepository) {
        this.saleListingRepository = saleListingRepository;
        this.businessRepository = businessRepository;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;

    }

    @Autowired
    @BeforeEach
    public void setup(WebApplicationContext context) {
        saleListingRepository.deleteAll();
        businessRepository.deleteAll();
        userRepository.deleteAll();
        addressRepository.deleteAll();

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        this.testBusinessAdmin = this.getTestUserBusinessAdmin();
        this.testBusinessAdmin.setId(null);
        addressRepository.save(testBusinessAdmin.getHomeAddress());

        this.testBusiness = this.getTestBusiness();
        this.testBusiness.setId(null);
        addressRepository.save(testBusiness.getAddress());

        businessRepository.save(testBusiness);
        List<SaleListing> listings = new ArrayList<>();
        //Make 6 sale listings for the business
        for (int i = 1; i < 7; i++) {
            SaleListing listing = new SaleListing();
            listing.setBusiness(testBusiness);
            listings.add(listing);
        }
        saleListingRepository.saveAll(listings);
    }

    @AfterEach
    public void tearDown() {
        saleListingRepository.deleteAll();
        businessRepository.deleteAll();
        userRepository.deleteAll();
        addressRepository.deleteAll();
    }

//    AC2
    @Given("the user is an admin of a business")
    public void theUserIsAnAdminOfABusiness() {
        Assertions.assertTrue(testBusiness.userIsAdmin(testBusinessAdmin.getId()));
    }

    @When("the user tries to feature a listing")
    public void theUserTriesToFeatureAListing() throws Exception {
        List<SaleListing> businessListings = saleListingRepository.findAllByBusinessId(testBusiness.getId());
        SaleListing listing = businessListings.get(0);
        Integer listingId = listing.getId();
        Integer businessId = testBusiness.getId();

        JSONObject body = new JSONObject();
        body.put("featured", true);
        request = MockMvcRequestBuilders
                .patch("/businesses/{businessId}/listings/{listingId}/feature", businessId, listingId)
                .content(body.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testBusinessAdmin)));
    }

    @Then("the listing is now featured")
    public void theListingIsNowFeatured() throws Exception {
        mockMvc.perform(request)
                .andExpect(status().isOk());

        List<SaleListing> businessListings = saleListingRepository.findAllByBusinessId(testBusiness.getId());
        SaleListing listing = businessListings.get(0);
        Assertions.assertTrue(listing.isFeatured());
    }

//    AC3
    @Given("the user is an admin of a business and {int} listings are featured")
    public void theUserIsAnAdminOfABusinessAndListingsAreFeatured(int numListings) {
        Assertions.assertTrue(testBusiness.userIsAdmin(testBusinessAdmin.getId()));
        List<SaleListing> listings = saleListingRepository.findAllByBusinessId(testBusiness.getId());
        this.numListings = numListings;
        //There are 6 sale listings for this business
        //Set numListings amount of sale listings to be featured (0 - numListings-1)
        //This will leave the sale listing at index numListings not featured
        for (int i = 0; i < numListings; i++) {
            listings.get(i).setFeatured(true);
        }
        saleListingRepository.saveAll(listings);
        //Check that sale listings with index (0 - numListings-1) are now featured
        listings = saleListingRepository.findAllByBusinessId(testBusiness.getId());
        for (int i = 0; i < numListings; i++) {
            Assertions.assertTrue(listings.get(i).isFeatured());
        }

    }

    @Then("an error occurs and the listing is not featured")
    public void anErrorOccursAndTheListingIsNotFeatured() throws Exception {
        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

//    AC6
    @When("the user tries to remove a featured listing")
    public void theUserTriesToRemoveAFeaturedListing() throws Exception {
        List<SaleListing> listings = saleListingRepository.findAllByBusinessId(testBusiness.getId());
        SaleListing listing = listings.get(numListings-1);
        Assertions.assertTrue(listing.isFeatured());

        JSONObject body = new JSONObject();
        body.put("featured", false);

        Integer businessId = testBusiness.getId();
        Integer listingId = listing.getId();
        request = MockMvcRequestBuilders
                .patch("/businesses/{businessId}/listings/{listingId}/feature", businessId, listingId)
                .content(body.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testBusinessAdmin)));

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Then("the listing is no longer featured")
    public void theListingIsNoLongerFeatured() {
        List<SaleListing> listings = saleListingRepository.findAllByBusinessId(testBusiness.getId());
        SaleListing listing = listings.get(numListings-1);
        Assertions.assertFalse(listing.isFeatured());
    }
}
