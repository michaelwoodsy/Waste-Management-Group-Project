package gradle.cucumber.steps;

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
import org.seng302.project.repository_layer.model.Review;
import org.seng302.project.repository_layer.model.Sale;
import org.seng302.project.repository_layer.model.User;
import org.seng302.project.repository_layer.repository.*;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/**
 * Cucumber steps for UD6 Sale Reviews
 */

@Transactional
@AutoConfigureTestDatabase
public class SaleReviewsSteps extends AbstractInitializer {

    private MockMvc mockMvc;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final ReviewRepository reviewRepository;
    private final SaleHistoryRepository saleHistoryRepository;

    private User testUser;
    private Business testBusiness;
    private MvcResult result;

    @Autowired
    public SaleReviewsSteps(
            AddressRepository addressRepository,
            UserRepository userRepository,
            BusinessRepository businessRepository,
            ReviewRepository reviewRepository,
            SaleHistoryRepository saleHistoryRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.saleHistoryRepository = saleHistoryRepository;
        this.reviewRepository = reviewRepository;
    }

    @Autowired
    @BeforeEach
    public void setup(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();


        reviewRepository.deleteAll();
        saleHistoryRepository.deleteAll();
        userRepository.deleteAll();

        this.testUser = this.getTestUser();
        testUser.setId(null);
        addressRepository.save(testUser.getHomeAddress());
        this.testUser = userRepository.save(testUser);

        User testAdmin = this.getTestUserBusinessAdmin();
        testAdmin.setId(null);
        addressRepository.save(testAdmin.getHomeAddress());

        this.testBusiness = this.getTestBusiness();
        this.testBusiness.setId(null);
        addressRepository.save(testBusiness.getAddress());
        this.testBusiness = businessRepository.save(testBusiness);

        testAdmin = userRepository.findByEmail(testAdmin.getEmail()).get(0);
        this.testBusiness.setPrimaryAdministratorId(testAdmin.getId());
        this.testBusiness = businessRepository.save(testBusiness);

        List<Review> reviews = new ArrayList<>();
        //Make 6 reviews for the business
        for (int i = 0; i < 6; i++) {
            Review review = new Review();
            review.setBusiness(testBusiness);
            review.setUser(testUser);
            reviews.add(review);
        }
        reviewRepository.saveAll(reviews);
    }

    @AfterEach
    public void teardown() {
        reviewRepository.deleteAll();
        saleHistoryRepository.deleteAll();
        businessRepository.deleteAll();
        userRepository.deleteAll();
        addressRepository.deleteAll();
    }

    //AC1
    @Given("I have purchased a sale listing from a given business.")
    public void iHavePurchasedASaleListingFromAGivenBusiness() {
    }

    @When("I leave a {int} star review with the comment {string}")
    public void iLeaveAStarReviewWithTheComment(int rating, String comment) throws Exception {
    }

    @Then("A review is successfully left on the sale.")
    public void aReviewIsSuccessfullyLeftOnTheSale() {
    }

    //AC2
    @When("I leave a review with comment {string} but no star rating.")
    public void iLeaveAReviewWithCommentButNoStarRating(String comment) throws Exception {
    }

    @Then("An error message is returned to say that no star rating has been provided for the review.")
    public void anErrorMessageIsReturnedToSayThatNoStarRatingHasBeenProvidedForTheReview() throws Exception {
    }
    //AC3
    @Given("I am logged in as a user.")
    public void iAmLoggedInAsAUser() {
        Assertions.assertEquals(1, userRepository.findByEmail(this.testUser.getEmail()).size());
    }

    @When("I view my purchase history.")
    public void iViewMyPurchaseHistory() {
        
    }

    @Then("I can view the reviews left on my purchase history.")
    public void iCanViewTheReviewsLeftOnMyPurchaseHistory() {
        
    }
    //AC4
    @When("I view a business' profile page.")
    public void iViewABusinessProfilePage() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/businesses/{businessId}/reviews", this.testBusiness.getId())
                .with(user(new AppUserDetails(this.testUser)));
        result = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
    }

    @Then("I can view the reviews left on their business.")
    public void iCanViewTheReviewsLeftOnTheirBusiness() throws Exception{
        String response = result.getResponse().getContentAsString();
        JSONArray reviews = new JSONArray(response);
        JSONObject review = reviews.getJSONObject(0);
        Assertions.assertEquals(6, reviews.length());
    }
    //AC5
    @Then("I can see the average star rating based on the average score of the business' reviews.")
    public void iCanSeeTheAverageStarRatingBasedOnTheAverageScoreOfTheBusinessReviews() {
    }
}
