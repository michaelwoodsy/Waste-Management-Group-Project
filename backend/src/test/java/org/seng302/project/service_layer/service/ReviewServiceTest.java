package org.seng302.project.service_layer.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.*;
import org.seng302.project.repository_layer.repository.*;
import org.seng302.project.service_layer.dto.review.PostReviewDTO;
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
    private final UserService userService;
    private final BusinessRepository businessRepository;
    private final BusinessService businessService;
    private final AddressRepository addressRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewService reviewService;
    private final SaleHistoryRepository saleHistoryRepository;

    private User testUser;
    private User testAdmin;
    private Business testBusiness;
    private Sale sale;

    @Autowired
    public ReviewServiceTest(UserRepository userRepository,
                             BusinessRepository businessRepository,
                             AddressRepository addressRepository,
                             ReviewRepository reviewRepository,
                             SaleHistoryRepository saleHistoryRepository){
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.addressRepository = addressRepository;
        this.reviewRepository = reviewRepository;
        this.saleHistoryRepository = saleHistoryRepository;
        this.userService = Mockito.mock(UserService.class);
        this.businessService = Mockito.mock(BusinessService.class);
        this.reviewService = new ReviewService(businessService, userService,
                this.reviewRepository, this.saleHistoryRepository);
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
        addressRepository.save(testBusiness.getAddress());
        businessRepository.save(testBusiness);

        this.sale = new Sale();
        sale.setBuyerId(testUser.getId());
        saleHistoryRepository.save(sale);

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

    /**
     * Tests that when getting a business' reviews as an admin of a business
     * all expected reviews are returned
     */
    @Test
    void getBusinessReviews_hasReviews_Success(){
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
        reviewRepository.deleteAll();
        Integer businessId = this.testBusiness.getId();
        AppUserDetails appUser = new AppUserDetails(this.testAdmin);
        List<Review> reviews = reviewService.getBusinessReviews(businessId, appUser);
        Assertions.assertEquals(0, reviews.size());
    }

    /**
     * Tests that a ForbiddenException is thrown when
     * getting a business' reviews and
     * a the user is not an admin of the given business
     */
    @Test
    void getBusinessReviews_notAdmin_ForbiddenException(){
        Integer businessId = this.testBusiness.getId();

        Mockito.when(businessService.checkBusiness(businessId))
                .thenReturn(this.testBusiness);

        Mockito.doThrow(new ForbiddenException(""))
                .when(businessService).checkUserCanDoBusinessAction(any(AppUserDetails.class), any(Business.class));

        AppUserDetails appUser = new AppUserDetails(this.testUser);
        Assertions.assertThrows(ForbiddenException.class,
                () -> reviewService.getBusinessReviews(businessId, appUser));
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

    /**
     * Tests that posting a review as a different user
     * results in a ForbiddenException
     */
    @Test
    void newReview_notUser_forbiddenException() {
        Mockito.doThrow(new ForbiddenException(""))
                .when(userService).checkForbidden(any(Integer.class),
                any(AppUserDetails.class));

        Integer userId = testUser.getId();
        Integer purchaseId = sale.getSaleId();
        PostReviewDTO requestDTO = new PostReviewDTO(5, "Loved it!");
        AppUserDetails appUser = new AppUserDetails(this.testAdmin);
        Assertions.assertThrows(ForbiddenException.class,
                () -> reviewService.newReview(userId, purchaseId, requestDTO, appUser));

    }

    /**
     * Tests that posting a review as a nonexistent user
     * results in a NotAcceptableException
     */
    @Test
    void newReview_nonExistentUser_notAcceptableException() {

        Mockito.when(userService.getUserByEmail(any(String.class))).thenReturn(null);

        Integer purchaseId = sale.getSaleId();
        PostReviewDTO requestDTO = new PostReviewDTO(5, "Loved it!");
        AppUserDetails appUser = new AppUserDetails(this.testUser);
        Assertions.assertThrows(NotAcceptableException.class,
                () -> reviewService.newReview(50, purchaseId, requestDTO, appUser));

    }

    /**
     * Tests that posting a review for a nonexistent purchase
     * for the user results in a NotAcceptableException
     */
    @Test
    void newReview_nonExistentPurchaseForUser_notAcceptableException() {
        Mockito.when(userService.getUserByEmail(any(String.class))).thenReturn(testUser);

        Integer userId = testUser.getId();
        Integer purchaseId = sale.getSaleId();
        PostReviewDTO requestDTO = new PostReviewDTO(5, "Loved it!");
        AppUserDetails appUser = new AppUserDetails(this.testUser);
        Assertions.assertThrows(NotAcceptableException.class,
                () -> reviewService.newReview(userId, purchaseId + 70, requestDTO, appUser));

    }

    /**
     * Tests that posting a review adds the review to the repository
     */
    @Test
    void newReview_success() {

        Mockito.when(userService.getUserByEmail(any(String.class))).thenReturn(testUser);

        Integer userId = testUser.getId();
        Integer purchaseId = sale.getSaleId();
        PostReviewDTO requestDTO = new PostReviewDTO(5, "Loved it!");
        AppUserDetails appUser = new AppUserDetails(this.testUser);

        reviewService.newReview(userId, purchaseId, requestDTO, appUser);

        List<Review> reviews = reviewRepository.findAllByUserId(testUser.getId());

        //Expect 6 reviews from setup() plus the 1 from this test
        Assertions.assertEquals(7, reviews.size());

    }

}