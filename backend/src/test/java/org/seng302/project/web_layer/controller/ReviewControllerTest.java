package org.seng302.project.web_layer.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.Business;
import org.seng302.project.repository_layer.model.Review;
import org.seng302.project.repository_layer.model.User;
import org.seng302.project.repository_layer.repository.AddressRepository;
import org.seng302.project.repository_layer.repository.BusinessRepository;
import org.seng302.project.repository_layer.repository.ReviewRepository;
import org.seng302.project.repository_layer.repository.UserRepository;
import org.seng302.project.service_layer.exceptions.ForbiddenException;
import org.seng302.project.service_layer.exceptions.NotAcceptableException;
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

import static org.mockito.ArgumentMatchers.any;
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

    private User testUser;
    private User testAdmin;
    private Business testBusiness;

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
        this.testBusiness.setPrimaryAdministratorId(this.testAdmin.getId());
        addressRepository.save(testBusiness.getAddress());
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

    /**
     * Tests that getting a business' reviews when the user is an admin of the business
     *  returns a 200 OK and all the business' reviews
     */
    @Test
    void getBusinessReviews_isAdminSuccess_200() throws Exception {
        Integer businessId = this.testBusiness.getId();
        Assertions.assertEquals(this.testBusiness.getPrimaryAdministratorId(), this.testAdmin.getId());

        Mockito.when(reviewService.getBusinessReviews(any(Integer.class), any(AppUserDetails.class)))
                .thenReturn(reviewRepository.findAllByBusinessId(businessId));
        AppUserDetails appUser = new AppUserDetails(this.testAdmin);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/businesses/{businessId}/reviews", businessId)
                .with(user(appUser));

        MvcResult response = mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String responseData = response.getResponse().getContentAsString();
        List<Review> result = objectMapper.readValue(responseData, new TypeReference<>() {
        });
        Assertions.assertEquals(6, result.size());
    }

    /**
     * Tests that getting a business' reviews when the user is not an admin of the business
     * returns a 200 OK and all the business' reviews
     */
    @Test
    void getBusinessReviews_notAdminSuccess_200() throws Exception {
        Assertions.assertNotEquals(this.testBusiness.getPrimaryAdministratorId(), this.testUser.getId());
        Integer businessId = this.testBusiness.getId();
        Mockito.when(reviewService.getBusinessReviews(any(Integer.class), any(AppUserDetails.class)))
                .thenReturn(reviewRepository.findAllByBusinessId(businessId));
        AppUserDetails appUser = new AppUserDetails(this.testAdmin);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/businesses/{businessId}/reviews", businessId)
                .with(user(appUser));

        MvcResult response = mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String responseData = response.getResponse().getContentAsString();
        List<Review> result = objectMapper.readValue(responseData, new TypeReference<>() {
        });
        Assertions.assertEquals(6, result.size());
    }

    /**
     * Tests that getting a business' reviews with an invalid business ID
     * returns a 400 Bad Request
     */
    @Test
    void getBusinessReviews_invalidBusinessIdType_400() throws Exception {
        String businessId = "two";
        Assertions.assertEquals(this.testBusiness.getPrimaryAdministratorId(), this.testAdmin.getId());
        AppUserDetails appUser = new AppUserDetails(this.testAdmin);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/businesses/{businessId}/reviews", businessId)
                .with(user(appUser));

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    /**
     * Tests that getting a business' reviews with an invalid business ID
     * returns a 401 Unauthorized
     */
    @Test
    void getBusinessReviews_notLoggedIn_401() throws Exception {
        Integer businessId = this.testBusiness.getId();
        Assertions.assertEquals(this.testBusiness.getPrimaryAdministratorId(), this.testAdmin.getId());
        AppUserDetails appUser = new AppUserDetails(this.testAdmin);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/businesses/{businessId}/reviews", businessId);

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    /**
     * Tests that getting a business' reviews with an nonexistent business ID
     * returns a 406 Not Acceptable
     */
    @Test
    void getBusinessReviews_nonexistentBusinessID_406() throws Exception {
        Integer businessId = 9999;
        Assertions.assertEquals(this.testBusiness.getPrimaryAdministratorId(), this.testAdmin.getId());
        AppUserDetails appUser = new AppUserDetails(this.testAdmin);

        Mockito.doThrow(new NotAcceptableException(
                String.format("No Business with ID %d exists", businessId))).when(reviewService)
                .getBusinessReviews(any(Integer.class), any(AppUserDetails.class));

        RequestBuilder request = MockMvcRequestBuilders
                .get("/businesses/{businessId}/reviews", businessId)
                .with(user(appUser));

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable());
    }
}
