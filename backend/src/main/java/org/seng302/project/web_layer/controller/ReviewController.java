package org.seng302.project.web_layer.controller;

import net.minidev.json.JSONObject;
import org.seng302.project.repository_layer.model.Review;
import org.seng302.project.service_layer.service.ResaleStatisticsService;
import org.seng302.project.service_layer.service.ReviewService;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Rest controller for sale reviews.
 */
@RestController
public class ReviewController {
    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class.getName());

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(
            ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * Request to get all of a business' reviews
     * @param businessId The ID of the Business you want to get the reviews of
     * @param appUser The User who is trying to get the reviews of the Business
     * @return a list with all the reviews of that Business, an empty list if there are none
     */
    @GetMapping("/businesses/{businessId}/reviews")
    @ResponseStatus(HttpStatus.OK)
    public List<Review> getBusinessReviews(@PathVariable Integer businessId, @AuthenticationPrincipal AppUserDetails appUser){
        logger.info("Request to get all sale reviews of a Business with ID: {}", businessId);
        return reviewService.getBusinessReviews(businessId, appUser);
    }
}
