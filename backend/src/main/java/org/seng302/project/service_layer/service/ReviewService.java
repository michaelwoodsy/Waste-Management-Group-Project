package org.seng302.project.service_layer.service;

import org.seng302.project.repository_layer.model.*;
import org.seng302.project.repository_layer.model.Review;
import org.seng302.project.repository_layer.model.Sale;
import org.seng302.project.repository_layer.model.User;
import org.seng302.project.repository_layer.repository.ReviewRepository;
import org.seng302.project.repository_layer.repository.SaleHistoryRepository;
import org.seng302.project.repository_layer.repository.UserNotificationRepository;
import org.seng302.project.service_layer.dto.review.PostReviewDTO;
import org.seng302.project.service_layer.exceptions.NotAcceptableException;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class.getName());

    private final BusinessService businessService;
    private final UserService userService;
    private final ReviewRepository reviewRepository;
    private final SaleHistoryRepository saleHistoryRepository;
    private final UserNotificationRepository userNotificationRepository;

    @Autowired
    public ReviewService(BusinessService businessService,
                         UserService userService,
                         ReviewRepository reviewRepository,
                         SaleHistoryRepository saleHistoryRepository,
                         UserNotificationRepository userNotificationRepository){
        this.businessService = businessService;
        this.userService = userService;
        this.reviewRepository = reviewRepository;
        this.saleHistoryRepository = saleHistoryRepository;
        this.userNotificationRepository = userNotificationRepository;
    }

    /**
     * Method that gets all reviews left by Users for a particular Business
     * @param businessId The ID of the Business you wish to get reviews of
     * @param user The User that is trying to access the reviews for this Business
     * @return a List of all the reviews for this Business
     */
    public List<Review> getBusinessReviews(Integer businessId,
                                            AppUserDetails user){
        logger.info("Request to get all sale reviews of a Business with ID: {}", businessId);
        // Get the business of the request
        businessService.checkBusiness(businessId);

        // Return a list of all the reviews belonging to the business (if there are none an empty list)
        return reviewRepository.findAllByBusinessId(businessId);
    }


    /**
     * Creates a review on a sale (purchased sale listing)
     *
     * @param userId id of the user to make the review as
     * @param purchaseId id of the sale the user is leaving the review about
     * @param requestDTO the dto containing the rating and message of the review
     * @param appUser the user making the request
     */
    public void newReview(Integer userId, Integer purchaseId, PostReviewDTO requestDTO,
                          AppUserDetails appUser) {

        logger.info("Request by user with id {} to make a review for sale with id {}", userId, purchaseId);

        userService.checkForbidden(userId, appUser);
        User user = userService.getUserByEmail(appUser.getUsername());

        if (user == null) {
            String errorMessage = "There is no user with id " + userId;
            logger.warn(errorMessage);
            throw new NotAcceptableException(errorMessage);
        }

        Optional<Sale> purchaseOptional = saleHistoryRepository.findById(purchaseId);
        if (purchaseOptional.isEmpty()) {
            String errorMessage = purchaseId + " is not a valid purchase id.";
            logger.warn(errorMessage);
            throw new NotAcceptableException(errorMessage);
        }

        Sale purchase = purchaseOptional.get();
        if (!purchase.getBuyerId().equals(userId)) {
            String errorMessage = purchaseId + " is not a valid purchase id for user with id " + userId;
            logger.warn(errorMessage);
            throw new NotAcceptableException(errorMessage);
        }

        Review review = new Review(purchase, user, requestDTO.getRating(), requestDTO.getReviewMessage());
        reviewRepository.save(review);
    }

    /**
     * Creates a review on a sale (purchased sale listing)
     *
     * @param businessId id of business the review is from
     * @param reviewId id of the review the response is being left on
     * @param response the review response message
     * @param appUser the user making the request
     */
    public void respondToReview(Integer businessId, Integer reviewId, String response,
                          AppUserDetails appUser) {
        logger.info("Request by business with ID {} to respond to a review with ID {}", businessId, reviewId);

        Business business = businessService.checkBusiness(businessId);

        businessService.checkUserCanDoBusinessAction(appUser, business);

        Optional<Review> reviewOptional = reviewRepository.findById(reviewId);
        if (reviewOptional.isEmpty()) {
            String errorMessage = reviewId + " is not a valid review id.";
            logger.warn(errorMessage);
            throw new NotAcceptableException(errorMessage);
        }

        Review review = reviewOptional.get();
        review.setReviewResponse(response);
        reviewRepository.save(review);

        ReviewReplyNotification reviewReplyNotification = new ReviewReplyNotification(review);
        userNotificationRepository.save(reviewReplyNotification);
    }
}
