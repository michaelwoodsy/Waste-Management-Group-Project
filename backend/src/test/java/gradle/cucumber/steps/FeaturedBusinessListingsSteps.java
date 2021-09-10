package gradle.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@ContextConfiguration
public class FeaturedBusinessListingsSteps {

    private MockMvc mockMvc;
    private RequestBuilder request;

    @BeforeEach
    @Autowired
    public void setup(WebApplicationContext context) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @After
    public void tearDown() {
        saleListingRepository.deleteAll();
        businessRepository.deleteAll();
        userRepository.deleteAll();
    }

//    AC2
    @Given("the user is acting as a business")
    public void the_user_is_acting_as_a_business() {
    }


    @When("the user tries to feature a listing")
    public void theUserTriesToFeatureAListing() {

    }

    @Then("the listing is now featured")
    public void theListingIsNowFeatured() {

    }

//    AC3
    @Given("the user is acting as a business and {int} listings are featured")
    public void theUserIsActingAsABusinessAndListingsAreFeatured(int arg0) {

    }

    @When("the user tires to feature a listing")
    public void theUserTiresToFeatureAListing() {

    }

    @Then("an error occurs and the listing is not featured")
    public void anErrorOccursAndTheListingIsNotFeatured() {

    }

//    AC6
    @When("the user tries to remove a featured listing")
    public void theUserTriesToRemoveAFeaturedListing() {

    }

    @Then("the listing is no longer featured")
    public void theListingIsNoLongerFeatured() {
    }
}
