package org.seng302.project.repository_layer.repository;

import org.seng302.project.repository_layer.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    /**
     * Method for getting reviews left on a business
     *
     * @param businessId The id of the business to get reviews for.
     * @return List of reviews
     */
    List<Review> findAllByBusinessId(@Param("businessId") Integer businessId);

    /**
     * Method for getting reviews left by a user
     *
     * @param userId The id of the user to ger reviews for.
     * @return List of reviews
     */
    List<Review> findAllByUserId(@Param("userId") Integer userId);
}
