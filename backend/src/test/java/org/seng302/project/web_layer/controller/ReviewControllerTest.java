package org.seng302.project.web_layer.controller;


import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.Business;
import org.seng302.project.repository_layer.model.Review;
import org.seng302.project.repository_layer.model.Sale;
import org.seng302.project.repository_layer.model.User;
import org.seng302.project.repository_layer.repository.*;
import org.seng302.project.service_layer.dto.review.PostReviewDTO;
import org.seng302.project.service_layer.exceptions.ForbiddenException;
import org.seng302.project.service_layer.exceptions.NotAcceptableException;
import org.seng302.project.service_layer.service.ReviewService;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ReviewControllerTest extends AbstractInitializer {

    @Autowired
    private MockMvc mockMvc;

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
    private Review testReview;

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
            if (i == 0) {
                review.setSale(sale);
            }
            reviews.add(review);
        }
        reviewRepository.saveAll(reviews);
        this.testReview = reviewRepository.findAllByBusinessId(testBusiness.getId(), Pageable.unpaged()).getContent().get(0);
    }

    /**
     * Tests that retrieving reviews for a business
     * gives a 200 response
     */
    @Test
    void getBusinessReviews_success_200() throws Exception {
        Integer businessId = this.testBusiness.getId();
        AppUserDetails appUser = new AppUserDetails(this.testAdmin);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/businesses/{businessId}/reviews", businessId)
                .with(user(appUser));

        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     * Tests that leaving a valid review
     * when not logged in gives a
     * 401 response
     */
    @Test
    void postReview_notLoggedIn_401() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .post("/users/{userId}/purchases/{purchaseId}/review",
                        testUser.getId(), sale.getSaleId());

        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    /**
     * Tests that leaving a valid review with
     * a message gives a 201 response
     */
    @Test
    void postReview_withMessage_201() throws Exception {
        JSONObject requestBody = new JSONObject();
        requestBody.put("rating", 4);
        requestBody.put("reviewMessage", "Very tasty");

        RequestBuilder createReviewRequest = MockMvcRequestBuilders
                .post("/users/{userId}/purchases/{purchaseId}/review",
                        testUser.getId(), sale.getSaleId())
                .content(requestBody.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(createReviewRequest).andExpect(status().isCreated());
    }

    /**
     * Tests that leaving a valid review without
     * a message gives a 201 response
     */
    @Test
    void postReview_withoutMessage_201() throws Exception {
        JSONObject requestBody = new JSONObject();
        requestBody.put("rating", 4);

        RequestBuilder createReviewRequest = MockMvcRequestBuilders
                .post("/users/{userId}/purchases/{purchaseId}/review",
                        testUser.getId(), sale.getSaleId())
                .content(requestBody.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(createReviewRequest).andExpect(status().isCreated());

    }

    /**
     * Tests that trying to leave a review with
     * a 6 star rating gives a 400 response
     * because the acceptable range is 1-5
     */
    @Test
    void postReview_6StarRating_400() throws Exception {
        JSONObject requestBody = new JSONObject();
        requestBody.put("rating", 6);
        requestBody.put("reviewMessage", "Super tasty");

        RequestBuilder createReviewRequest = MockMvcRequestBuilders
                .post("/users/{userId}/purchases/{purchaseId}/review",
                        testUser.getId(), sale.getSaleId())
                .content(requestBody.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(createReviewRequest).andExpect(status().isBadRequest());
    }

    /**
     * Tests that trying to leave a review with
     * no star rating gives a 400 response
     */
    @Test
    void postReview_noRating_400() throws Exception {
        JSONObject requestBody = new JSONObject();
        requestBody.put("reviewMessage", "Super tasty");

        RequestBuilder createReviewRequest = MockMvcRequestBuilders
                .post("/users/{userId}/purchases/{purchaseId}/review",
                        testUser.getId(), sale.getSaleId())
                .content(requestBody.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(createReviewRequest).andExpect(status().isBadRequest());

    }

    /**
     * Tests that a 403 response is given when
     * trying to leave a review and
     * the appUser doesn't match the userId, or is not an admin
     */
    @Test
    void postReview_differentUser_403() throws Exception {
        JSONObject requestBody = new JSONObject();
        requestBody.put("rating", 4);
        requestBody.put("reviewMessage", "Very tasty");

        doThrow(ForbiddenException.class)
                .when(reviewService)
                .newReview(Mockito.any(Integer.class), Mockito.any(Integer.class),
                        Mockito.any(PostReviewDTO.class), Mockito.any(AppUserDetails.class));

        RequestBuilder createReviewRequest = MockMvcRequestBuilders
                .post("/users/{userId}/purchases/{purchaseId}/review",
                        testUser.getId(), sale.getSaleId())
                .content(requestBody.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testAdmin)));

        mockMvc.perform(createReviewRequest).andExpect(status().isForbidden());
    }

    /**
     * Tests that a 406 response is given when
     * trying to leave a review and
     * the sale doesn't exist
     */
    @Test
    void postReview_nonExistentSale_406() throws Exception {
        JSONObject requestBody = new JSONObject();
        requestBody.put("rating", 4);
        requestBody.put("reviewMessage", "Very tasty");

        doThrow(NotAcceptableException.class)
                .when(reviewService)
                .newReview(Mockito.any(Integer.class), Mockito.any(Integer.class),
                        Mockito.any(PostReviewDTO.class), Mockito.any(AppUserDetails.class));

        RequestBuilder createReviewRequest = MockMvcRequestBuilders
                .post("/users/{userId}/purchases/{purchaseId}/review",
                        testUser.getId(), sale.getSaleId() + 50)
                .content(requestBody.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(createReviewRequest).andExpect(status().isNotAcceptable());
    }

    /**
     * Tests that leaving a valid response to a review
     * gives a 200 response
     */
    @Test
    void respondToReview_success_200() throws Exception {
        JSONObject requestBody = new JSONObject();
        requestBody.put("reviewResponse", "Thank you for the feedback");

        RequestBuilder patchReviewRequest = MockMvcRequestBuilders
                .patch("/businesses/{businessId}/reviews/{reviewId}/respond", testBusiness.getId(), testReview.getReviewId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody.toString())
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testAdmin)));

        mockMvc.perform(patchReviewRequest).andExpect(status().isOk());
    }

    /**
     * Tests that leaving a response to a review
     * with no response message
     * gives a 400 response
     */
    @Test
    void respondToReview_noMessage_400() throws Exception {
        JSONObject requestBody = new JSONObject();

        RequestBuilder patchReviewRequest = MockMvcRequestBuilders
                .patch("/businesses/{businessId}/reviews/{reviewId}/respond", testBusiness.getId(), testReview.getReviewId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody.toString())
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testAdmin)));

        mockMvc.perform(patchReviewRequest).andExpect(status().isBadRequest());
    }

    /**
     * Tests that responding to a review
     * when not logged in gives a
     * 401 response
     */
    @Test
    void respondToReview_notLoggedIn_401() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .post("/businesses/{businessId}/reviews/{reviewId}/respond",
                        testBusiness.getId(), testReview.getReviewId());

        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    /**
     * Tests that a 403 response is given when
     * trying to respond to a review
     * and the user is not an admin of the business
     */
    @Test
    void respondToReview_notBusinessAdmin_403() throws Exception {
        JSONObject requestBody = new JSONObject();
        requestBody.put("reviewResponse", "Thank you for the feedback");

        doThrow(ForbiddenException.class)
                .when(reviewService)
                .respondToReview(Mockito.any(Integer.class), Mockito.any(Integer.class),
                        Mockito.any(String.class), Mockito.any(AppUserDetails.class));

        RequestBuilder patchReviewRequest = MockMvcRequestBuilders
                .patch("/businesses/{businessId}/reviews/{reviewId}/respond",
                        testBusiness.getId(), testReview.getReviewId())
                .content(requestBody.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        mockMvc.perform(patchReviewRequest).andExpect(status().isForbidden());
    }

    /**
     * Tests that a 406 response is given when
     * trying to respond to a review and
     * the review doesn't exist
     */
    @Test
    void respondToReview_nonExistentReview_406() throws Exception {
        JSONObject requestBody = new JSONObject();
        requestBody.put("reviewResponse", "Thank you for the feedback");

        doThrow(NotAcceptableException.class)
                .when(reviewService)
                .respondToReview(Mockito.any(Integer.class), Mockito.any(Integer.class),
                        Mockito.any(String.class), Mockito.any(AppUserDetails.class));

        RequestBuilder patchReviewRequest = MockMvcRequestBuilders
                .patch("/businesses/{businessId}/reviews/{reviewId}/respond",
                        testBusiness.getId(), testReview.getReviewId())
                .content(requestBody.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testAdmin)));

        mockMvc.perform(patchReviewRequest).andExpect(status().isNotAcceptable());
    }

}
