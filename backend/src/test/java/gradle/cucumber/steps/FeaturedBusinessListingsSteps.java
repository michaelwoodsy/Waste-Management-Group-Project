package gradle.cucumber.steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.*;
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

@Transactional
@AutoConfigureTestDatabase
public class FeaturedBusinessListingsSteps extends AbstractInitializer {

    private MockMvc mockMvc;
    private RequestBuilder request;
    private SaleListingRepository saleListingRepository;
    private BusinessRepository businessRepository;
    private UserRepository userRepository;

    private User testBusinessAdmin;
    private Business testBusiness;
    private Integer numListings;

    @Autowired
    public FeaturedBusinessListingsSteps(SaleListingRepository saleListingRepository,
                                         BusinessRepository businessRepository,
                                         UserRepository userRepository,
                                         WebApplicationContext context) {
        this.saleListingRepository = saleListingRepository;
        this.businessRepository = businessRepository;
        this.userRepository = userRepository;
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Before
    public void setup() {
        this.testBusinessAdmin = this.getTestUserBusinessAdmin();
        this.testBusinessAdmin.setId(null);
        this.testBusiness = this.getTestBusiness();
        this.testBusiness.setId(null);
        businessRepository.save(testBusiness);
        List<SaleListing> listings = new ArrayList<>();
        for (int i = 1; i < 7; i++) {
            SaleListing listing = new SaleListing();
            listing.setBusiness(testBusiness);
            listings.add(listing);
        }
        saleListingRepository.saveAll(listings);
    }

    @After
    public void tearDown() {
        saleListingRepository.deleteAll();
        businessRepository.deleteAll();
        userRepository.deleteAll();
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
        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Then("the listing is now featured")
    public void theListingIsNowFeatured() {
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
//        Set all but 1 sale listing to true
        for (int i = 0; i < numListings; i++) {
            listings.get(i).setFeatured(true);
        }
//        Check that all but 1 have been set to be featured
        saleListingRepository.saveAll(listings);
        listings = saleListingRepository.findAllByBusinessId(testBusiness.getId());
        for (int i = 0; i < numListings; i++) {
            Assertions.assertTrue(listings.get(i).isFeatured());
        }
    }

    @When("the user tires to feature a listing")
    public void theUserTiresToFeatureAListing() throws Exception {
        List<SaleListing> listings = saleListingRepository.findAllByBusinessId(testBusiness.getId());
        SaleListing listing = listings.get(numListings);
        Assertions.assertFalse(listings.get(numListings).isFeatured());

        JSONObject body = new JSONObject();
        body.put("featured", true);

        Integer businessId = testBusiness.getId();
        Integer listingId = listing.getId();
        request = MockMvcRequestBuilders
                .patch("/businesses/{businessId}/listings/{listingId}/feature", businessId, listingId)
                .content(body.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testBusinessAdmin)));
    }

    @Then("an error occurs and the listing is not featured")
    public void anErrorOccursAndTheListingIsNotFeatured() throws Exception {
        mockMvc.perform(request)
                .andExpect(status().isNotAcceptable());
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
