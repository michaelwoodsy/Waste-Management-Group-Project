package gradle.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
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
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureTestDatabase
public class RespondToReviewsSteps extends AbstractInitializer {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final BusinessRepository businessRepository;
    private final SaleHistoryRepository saleHistoryRepository;
    private final ReviewRepository reviewRepository;
    private final UserNotificationRepository userNotificationRepository;


    private MockMvc mockMvc;
    private User testUser;
    private User testAdmin;
    private Business testBusiness;
    private Sale testSale;
    private Review testReview;


    private RequestBuilder patchReviewRequest;

    @Autowired
    public RespondToReviewsSteps(AddressRepository addressRepository,
                            UserRepository userRepository,
                            BusinessRepository businessRepository,
                            SaleHistoryRepository saleHistoryRepository,
                            ReviewRepository reviewRepository,
                                 UserNotificationRepository userNotificationRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.saleHistoryRepository = saleHistoryRepository;
        this.reviewRepository = reviewRepository;
        this.userNotificationRepository = userNotificationRepository;
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
        userNotificationRepository.deleteAll();

        this.testUser = this.getTestUser();
        addressRepository.save(testUser.getHomeAddress());
        testUser.setId(null);
        this.testUser = userRepository.save(testUser);

        this.testAdmin = this.getTestUserBusinessAdmin();
        addressRepository.save(testAdmin.getHomeAddress());
        testAdmin.setId(null);

        this.testBusiness = this.getTestBusiness();
        this.testBusiness.setId(null);
        addressRepository.save(testBusiness.getAddress());
        this.testBusiness = businessRepository.save(testBusiness);

        this.testSale = new Sale(this.getSaleListings().get(0));
        this.testSale.setSaleId(null);
        this.testSale.setBusiness(testBusiness);
        this.testSale.setBuyerId(this.testUser.getId());
        this.testSale = saleHistoryRepository.save(this.testSale);

        this.testReview = new Review(this.testSale, this.testUser, 3, "Very good");
        this.testReview.setReviewId(null);
        this.testReview = reviewRepository.save(this.testReview);
    }

    @AfterEach
    public void teardown() {
        userNotificationRepository.deleteAll();
        reviewRepository.deleteAll();
        saleHistoryRepository.deleteAll();
        businessRepository.deleteAll();
        userRepository.deleteAll();
        addressRepository.deleteAll();
    }

    // AC 2
    @Given("I am an administrator for a business account")
    public void i_am_the_administrator_of_a_business_account() {
        testBusiness.addAdministrator(testAdmin);
        testBusiness = businessRepository.save(testBusiness);
        Assertions.assertTrue(testBusiness.userIsAdmin(testAdmin.getId()));
    }

    @When("I respond to a review with the message {string}.")
    public void iRespondToAReviewWithTheMessage(String response) throws Exception {
        JSONObject requestBody = new JSONObject();
        requestBody.put("reviewResponse", response);
        Assertions.assertEquals("Thank you very much for the feedback", response);

        patchReviewRequest = MockMvcRequestBuilders
                .patch("/businesses/{businessId}/reviews/{reviewId}/respond", testBusiness.getId(), testReview.getReviewId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody.toString())
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testAdmin)));

        mockMvc.perform(patchReviewRequest).andExpect(status().isOk());
    }

    @Then("A response is successfully left on the review.")
    public void aResponseIsSuccessfullyLeftOnTheReview() {
        Assertions.assertEquals(1, userNotificationRepository.findAll().size());
        Assertions.assertEquals(1, reviewRepository.findAll().size());
        Assertions.assertEquals("Thank you very much for the feedback", reviewRepository.findAll().get(0).getReviewResponse());
    }
}
