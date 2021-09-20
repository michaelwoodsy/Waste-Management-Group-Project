package org.seng302.project.service_layer.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.*;
import org.seng302.project.repository_layer.repository.AddressRepository;
import org.seng302.project.repository_layer.repository.BusinessRepository;
import org.seng302.project.repository_layer.repository.ReviewRepository;
import org.seng302.project.repository_layer.repository.UserRepository;
import org.seng302.project.service_layer.exceptions.ForbiddenException;
import org.seng302.project.service_layer.exceptions.NotAcceptableException;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@DataJpaTest
class ReviewServiceTest extends AbstractInitializer {
    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final BusinessService businessService;
    private final AddressRepository addressRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewService reviewService;

    private User testUser;
    private User testAdmin;
    private Business testBusiness;

    @Autowired
    public ReviewServiceTest(UserRepository userRepository,
                             BusinessRepository businessRepository,
                             AddressRepository addressRepository,
                             ReviewRepository reviewRepository){
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.addressRepository = addressRepository;
        this.reviewRepository = reviewRepository;
        UserService userService = Mockito.mock(UserService.class);
        this.businessService = Mockito.mock(BusinessService.class);
        this.reviewService = new ReviewService(businessService, userService, this.reviewRepository);
    }

    @BeforeEach
    void setup() {
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
        businessRepository.save(testBusiness);

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
     * Tests that when getting a business' reviews as an admin of a business
     * all expected reviews are returned
     */
    @Test
    void getBusinessReviews_hasReviews_Success(){
        Assertions.assertEquals(this.testBusiness.getPrimaryAdministratorId(), this.testAdmin.getId());
        Integer businessId = this.testBusiness.getId();
        AppUserDetails appUser = new AppUserDetails(this.testAdmin);
        List<Review> reviews = reviewService.getBusinessReviews(businessId, appUser);
        Assertions.assertEquals(6, reviews.size());
    }

    /**
     * Tests that when getting a business' reviews as an admin of a business
     * all expected reviews are returned
     */
    @Test
    void getBusinessReviews_hasNoReviews_Success(){
        Assertions.assertEquals(this.testBusiness.getPrimaryAdministratorId(), this.testAdmin.getId());
        reviewRepository.deleteAll();
        Integer businessId = this.testBusiness.getId();
        AppUserDetails appUser = new AppUserDetails(this.testAdmin);
        List<Review> reviews = reviewService.getBusinessReviews(businessId, appUser);
        Assertions.assertEquals(0, reviews.size());
    }

    /**
     * Tests that when getting a business' reviews as a user who is not an admin of a business
     * all expected reviews are returned
     */
    @Test
    void getBusinessReviews_notAdmin_Success(){
        Assertions.assertNotEquals(this.testBusiness.getPrimaryAdministratorId(), this.testUser.getId());
        Integer businessId = this.testBusiness.getId();
        AppUserDetails appUser = new AppUserDetails(this.testUser);
        List<Review> reviews = reviewService.getBusinessReviews(businessId, appUser);
        Assertions.assertEquals(6, reviews.size());
    }

    /**
     * Tests that a NotAcceptableException is thrown when
     * getting a business' reviews and
     * a nonexistent business is provided
     */
    @Test
    void getBusinessReviews_invalidBusiness_NotAcceptableException(){
        Mockito.doThrow(new NotAcceptableException(""))
                .when(businessService).checkBusiness(any(Integer.class));

        Integer businessId = 99999;
        AppUserDetails appUser = new AppUserDetails(this.testAdmin);
        Assertions.assertThrows(NotAcceptableException.class,
                () -> reviewService.getBusinessReviews(businessId, appUser));
    }
}
