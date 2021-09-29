package org.seng302.project.web_layer.controller;

import net.minidev.json.JSONObject;
import org.seng302.project.service_layer.dto.review.PostReviewDTO;
import org.seng302.project.service_layer.exceptions.BadRequestException;
import org.seng302.project.service_layer.service.ReviewService;
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
     * Request to get all of a business' reviews
     *
     * @param businessId The ID of the Business you want to get the reviews of
     * @return a list with all the reviews of that Business, an empty list if there are none
     */
    @GetMapping("/businesses/{businessId}/reviews")
    @ResponseStatus(HttpStatus.OK)
    public JSONObject getBusinessReviews(@PathVariable Integer businessId,
                                         @RequestParam(defaultValue = "0") Integer page) {
        return reviewService.getBusinessReviews(businessId, page);
    }

    /**
     * Creates a review on a sale (purchased sale listing)
     *
     * @param userId     id of the user to make the review as
     * @param purchaseId id of the sale the user is leaving the review about
     * @param requestDTO the dto containing the rating and message of the review
     * @param appUser    the user making the request
     */
    @PostMapping("/users/{userId}/purchases/{purchaseId}/review")
    @ResponseStatus(HttpStatus.CREATED)
    public void postReview(@PathVariable int userId,
                           @PathVariable int purchaseId, @Valid @RequestBody PostReviewDTO requestDTO,
                           @AuthenticationPrincipal AppUserDetails appUser) {
        reviewService.newReview(userId, purchaseId, requestDTO, appUser);
    }

    /**
     * Responds to a review left on a business
     *
     * @param businessId  id of business the review is from
     * @param reviewId    id of the review the response is being left on
     * @param requestBody the JSONObject containing the review response message
     * @param appUser     the user making the request
     */
    @PatchMapping("/businesses/{businessId}/reviews/{reviewId}/respond")
    @ResponseStatus(HttpStatus.OK)
    public void respondToReview(@PathVariable int businessId,
                                @PathVariable int reviewId, @RequestBody JSONObject requestBody,
                                @AuthenticationPrincipal AppUserDetails appUser) {
        String response;
        if (!requestBody.containsKey("reviewResponse")) {
            String message = "A reviewResponse message has not been provided";
            logger.warn(message);
            throw new BadRequestException(message);
        } else {
            response = (String) requestBody.get("reviewResponse");
            reviewService.respondToReview(businessId, reviewId, response, appUser);
        }
    }
}
