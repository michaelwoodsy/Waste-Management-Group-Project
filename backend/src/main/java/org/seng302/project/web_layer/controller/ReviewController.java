package org.seng302.project.web_layer.controller;

import org.seng302.project.service_layer.dto.review.PostReviewDTO;
import org.seng302.project.service_layer.service.BusinessService;
import org.seng302.project.service_layer.service.ReviewService;
import org.seng302.project.service_layer.service.SaleListingService;
import org.seng302.project.service_layer.service.UserService;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Rest controller for reviews
 */
@RestController
public class ReviewController {

    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class.getName());

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * Creates a review
     *
     * @param userId the user to make the review as
     * @param purchaseId the sale the user is leaving the review about
     * @param requestDTO the dto containing the rating and message of the review
     * @param appUser the user making the request
     */
    @PostMapping("/users/{userId}/purchases/{purchaseId}/review")
    @ResponseStatus(HttpStatus.CREATED)
    public void postReview(@PathVariable int userId,
            @PathVariable int purchaseId, @Valid @RequestBody PostReviewDTO requestDTO,
            @AuthenticationPrincipal AppUserDetails appUser) {
        reviewService.newReview(userId, purchaseId, requestDTO, appUser);
    }
}
