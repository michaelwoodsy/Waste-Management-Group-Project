package org.seng302.project.web_layer.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.Business;
import org.seng302.project.repository_layer.model.Review;
import org.seng302.project.repository_layer.model.Sale;
import org.seng302.project.repository_layer.model.User;
import org.seng302.project.repository_layer.repository.*;
import org.seng302.project.service_layer.service.ReviewService;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ReviewControllerTest extends AbstractInitializer{

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewService reviewService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BusinessRepository businessRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private SaleHistoryRepository saleHistoryRepository;

    private User testUser;
    private User testAdmin;
    private Business testBusiness;
    private Sale sale;

    @BeforeEach
    public void setup() {
        this.initialise();
        this.testUser = this.getTestUser();
        addressRepository.save(testUser.getHomeAddress());
        testUser.setId(null);
        this.testUser = userRepository.save(testUser);

        this.testAdmin = this.getTestUserBusinessAdmin();
        addressRepository.save(testAdmin.getHomeAddress());
        testAdmin.setId(null);
        this.testAdmin = userRepository.save(testAdmin);

        this.testBusiness = this.getTestBusiness();
        this.testBusiness.setId(null);
        addressRepository.save(testBusiness.getAddress());
        this.testBusiness = businessRepository.save(testBusiness);

        this.sale = saleHistoryRepository.save(new Sale());

        List<Review> reviews = new ArrayList<>();
        //Make 6 reviews for the business
        for (int i = 0; i < 6; i++) {
            Review review = new Review();
            review.setBusiness(testBusiness);
            review.setUser(testUser);
            if ( i == 0) {
                review.setSale(sale);
            }
            reviews.add(review);
        }
        reviewRepository.saveAll(reviews);
    }

    @Test
    void getBusinessReviews_success_200() throws Exception {
        Integer businessId = this.testBusiness.getId();
        System.out.println(reviewRepository.findAllByBusinessId(businessId));
        AppUserDetails appUser = new AppUserDetails(this.testAdmin);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/businesses/{businessId}/reviews", businessId)
                .with(user(new AppUserDetails(testUser)));

        MvcResult response = mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String responseData = response.getResponse().getContentAsString();
        List<Review> result = objectMapper.readValue(responseData, new TypeReference<>() {
        });
        Assertions.assertEquals(6, result.size());
    }

    /**
     * Tests that leaving a valid review
     * when not logged in gives a
     * 401 response
     */
    @Test
    void postReview_notLoggedIn_401() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/users/{userId}/purchases/{purchaseId}/review",
                        testUser.getId(), sale.getSaleId()))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    /**
     * Tests that leaving a valid review with
     * a message gives a 201 response
     */
    @Test
    void postReview_withMessage_201() {

    }

    /**
     * Tests that leaving a valid review without
     * a message gives a 201 response
     */
    @Test
    void postReview_withoutMessage_201() {

    }

    /**
     * Tests that trying to leave a review with
     * a 6 star rating gives a 400 response
     * because the acceptable range is 0-5
     */
    @Test
    void postReview_6StarRating_400() {

    }

    /**
     * Tests that trying to leave a review with
     * no star rating gives a 400 response
     */
    @Test
    void postReview_noRating_400() {

    }

    //TODO: tests for posting a review
    //403
    //406
}
