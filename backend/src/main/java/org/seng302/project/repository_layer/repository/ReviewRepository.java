package org.seng302.project.repository_layer.repository;

import org.seng302.project.repository_layer.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    /**
     * Method for getting reviews left on a business
     *
     * @param businessId The id of the business to get reviews for.
     * @param pageable pageable object used to get a specific page
     * @return List of reviews
     */
    Page<Review> findAllByBusinessId(@Param("businessId") Integer businessId, Pageable pageable);

    /**
     * Method for getting reviews left by a user
     *
     * @param userId The id of the user to ger reviews for.
     * @return List of reviews
     */
    List<Review> findAllByUserId(@Param("userId") Integer userId);
}
