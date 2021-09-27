package org.seng302.project.service_layer.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.*;
import org.seng302.project.repository_layer.repository.*;
import org.seng302.project.service_layer.dto.review.GetReviewDTO;
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
    private final BusinessNotificationRepository businessNotificationRepository;

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
        this.businessNotificationRepository = Mockito.mock(
                BusinessNotificationRepository.class);
        this.reviewService = new ReviewService(businessService, userService,
                this.reviewRepository, this.saleHistoryRepository, this.businessNotificationRepository);
    }

    @BeforeEach
    void setup() {
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

        List<SaleListing> listings = this.getSaleListings();

        List<Review> reviews = new ArrayList<>();
        //Make 4 reviews for the business
        for (SaleListing listing : listings) {
            Review review = new Review();
            review.setBusiness(testBusiness);
            review.setUser(testUser);
            Sale sale = new Sale(listing);
            sale.setBuyerId(testUser.getId());
            sale.setBusiness(testBusiness);
            sale = saleHistoryRepository.save(sale);
            review.setSale(sale);
            reviews.add(review);
        }
        sale = reviews.get(0).getSale();
        reviewRepository.saveAll(reviews);
    }

    /**
     * Tests that when getting a business' reviews as an admin of a business
     * all expected reviews are returned
     */
    @Test
    void getBusinessReviews_hasReviews_Success() {
        Assertions.assertEquals(this.testBusiness.getPrimaryAdministratorId(), this.testAdmin.getId());
        Integer businessId = this.testBusiness.getId();
        JSONObject response = reviewService.getBusinessReviews(businessId);
        List<GetReviewDTO> reviews = (List<GetReviewDTO>) response.get("reviews");
        Assertions.assertEquals(4, reviews.size());
    }

    /**
     * Tests that when getting a business' reviews as an admin of a business
     * all expected reviews are returned
     */
    @Test
    void getBusinessReviews_hasNoReviews_Success() {
        Assertions.assertEquals(this.testBusiness.getPrimaryAdministratorId(), this.testAdmin.getId());
        reviewRepository.deleteAll();
        Integer businessId = this.testBusiness.getId();
        JSONObject response = reviewService.getBusinessReviews(businessId);
        List<GetReviewDTO> reviews = (List<GetReviewDTO>) response.get("reviews");
        Assertions.assertEquals(0, reviews.size());
    }

    /**
     * Tests that when getting a business' reviews as a user who is not an admin of a business
     * all expected reviews are returned
     */
    @Test
    void getBusinessReviews_notAdmin_Success() {
        Assertions.assertNotEquals(this.testBusiness.getPrimaryAdministratorId(), this.testUser.getId());
        Integer businessId = this.testBusiness.getId();
        JSONObject response = reviewService.getBusinessReviews(businessId);
        List<GetReviewDTO> reviews = (List<GetReviewDTO>) response.get("reviews");
        Assertions.assertEquals(4, reviews.size());
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
        Assertions.assertThrows(NotAcceptableException.class,
                () -> reviewService.getBusinessReviews(businessId));
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

        //Expect 4 reviews from setup() plus the 1 from this test
        Assertions.assertEquals(5, reviews.size());
    }

    /**
     * Tests that leaving a review creates a notification
     * for the business
     */
    @Test
    void newReview_createsNotification() {

        Mockito.when(userService.getUserByEmail(any(String.class))).thenReturn(testUser);

        Integer userId = testUser.getId();
        Integer purchaseId = sale.getSaleId();
        PostReviewDTO requestDTO = new PostReviewDTO(5, "Loved it!");
        AppUserDetails appUser = new AppUserDetails(this.testUser);

        ArgumentCaptor<ReviewNotification> notificationArgumentCaptor = ArgumentCaptor.forClass(
                ReviewNotification.class);

        reviewService.newReview(userId, purchaseId, requestDTO, appUser);
        Mockito.verify(businessNotificationRepository).save(notificationArgumentCaptor.capture());

        Review reviewOnNotification = notificationArgumentCaptor.getValue().getReview();

        Assertions.assertEquals(purchaseId, reviewOnNotification.getSale().getSaleId());

    }

}
