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
import org.seng302.project.repository_layer.model.*;
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
import java.util.List;
import java.util.ArrayList;


import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Cucumber steps for UD6 Sale Reviews
 */
@Transactional
@AutoConfigureTestDatabase
public class SaleReviewsSteps extends AbstractInitializer {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final BusinessRepository businessRepository;
    private final SaleHistoryRepository saleHistoryRepository;
    private final ReviewRepository reviewRepository;


    private MockMvc mockMvc;
    private User testUser;
    private Business testBusiness;
    private Sale sale;
    private MvcResult result;

    private RequestBuilder createReviewRequest;

    @Autowired
    public SaleReviewsSteps(AddressRepository addressRepository,
                              UserRepository userRepository,
                              BusinessRepository businessRepository,
                            SaleHistoryRepository saleHistoryRepository,
                            ReviewRepository reviewRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.saleHistoryRepository = saleHistoryRepository;
        this.reviewRepository = reviewRepository;
    }

    @BeforeEach
    @Autowired
    public void setup(WebApplicationContext context) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        reviewRepository.deleteAll();
        saleHistoryRepository.deleteAll();
        userRepository.deleteAll();

        this.testUser = this.getTestUser();
        addressRepository.save(testUser.getHomeAddress());
        testUser.setId(null);
        this.testUser = userRepository.save(testUser);

        User testAdmin = this.getTestUserBusinessAdmin();
        addressRepository.save(testAdmin.getHomeAddress());
        testAdmin.setId(null);

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
        Sale newSale = new Sale();
        newSale.setBuyerId(testUser.getId());
        newSale.setMoreInfo("Purchased sale listing");

        this.sale = saleHistoryRepository.save(newSale);
        
    }

    @When("I leave a {int} star review with the comment {string}")
    public void iLeaveAStarReviewWithTheComment(int rating, String comment) throws Exception {
        JSONObject requestBody = new JSONObject();
        requestBody.put("rating", rating);
        requestBody.put("reviewMessage", comment);

        RequestBuilder createReviewRequest = MockMvcRequestBuilders
                .post("/users/{userId}/purchases/{purchaseId}/review",
                        testUser.getId(), sale.getSaleId())
                .content(requestBody.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(createReviewRequest).andExpect(status().isCreated());
    }

    @Then("A review is successfully left on the sale.")
    public void aReviewIsSuccessfullyLeftOnTheSale() {
        List<Review> retrievedReviews = reviewRepository.findAllByUserId(testUser.getId());
        //6 from the constructor and 1 new one
        Assertions.assertEquals(7, retrievedReviews.size());
        Assertions.assertEquals("Purchased sale listing", retrievedReviews.get(6).getSale().getMoreInfo());
    }


    //AC2
    @When("I leave a review with comment {string} but no star rating.")
    public void iLeaveAReviewWithCommentButNoStarRating(String comment) throws Exception {
        JSONObject requestBody = new JSONObject();
        requestBody.put("reviewMessage", comment);

        createReviewRequest = MockMvcRequestBuilders
                .post("/users/{userId}/purchases/{purchaseId}/review",
                        testUser.getId(), sale.getSaleId())
                .content(requestBody.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));
        
    }

    @Then("An error message is returned to say that no star rating has been provided for the review.")
    public void anErrorMessageIsReturnedToSayThatNoStarRatingHasBeenProvidedForTheReview() throws Exception {
        MvcResult postReviewResponse = this.mockMvc.perform(createReviewRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postReviewResponse.getResponse().getContentAsString();
        Assertions.assertEquals("MethodArgumentNotValidException: Rating is a mandatory field.", returnedExceptionString);
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
        Assertions.assertEquals(6, reviews.length());
    }

    //AC5
    @Then("I can see the average star rating based on the average score of the business' reviews.")
    public void iCanSeeTheAverageStarRatingBasedOnTheAverageScoreOfTheBusinessReviews() {
    }
}
