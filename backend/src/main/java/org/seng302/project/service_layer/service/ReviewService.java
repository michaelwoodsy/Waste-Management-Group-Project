package org.seng302.project.service_layer.service;

import org.seng302.project.repository_layer.model.Review;
import org.seng302.project.repository_layer.repository.ReviewRepository;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    private final BusinessService businessService;
    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewService(BusinessService businessService,
                         UserService userService,
                         ReviewRepository reviewRepository){
        this.businessService = businessService;
        this.reviewRepository = reviewRepository;
    }

    /**
     * Method that gets all reviews left by Users for a particular Business
     * @param businessId The ID of the Business you wish to get reviews of
     * @param user The User that is trying to access the reviews for this Business
     * @return a List of all the reviews for this Business
     */
    public List<Review> getBusinessReviews(Integer businessId,
                                            AppUserDetails user){
        // Get the business of the request
        businessService.checkBusiness(businessId);

        // Return a list of all the reviews belonging to the business (if there are none an empty list)
        return reviewRepository.findAllByBusinessId(businessId);
    }
}
