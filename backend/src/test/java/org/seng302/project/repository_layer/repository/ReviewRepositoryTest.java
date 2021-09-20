package org.seng302.project.repository_layer.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DataJpaTest
public class ReviewRepositoryTest extends AbstractInitializer {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private SaleHistoryRepository saleHistoryRepository;
    @Autowired
    private BusinessRepository businessRepository;
    @Autowired
    private UserRepository userRepository;

    Review review1;
    Review review2;

    @BeforeEach
    void setup() {
        this.initialise();
        Sale sale1 = new Sale(getSaleListings().get(0));
        Sale sale2 = new Sale(getSaleListings().get(1));
        saleHistoryRepository.save(sale1);
        saleHistoryRepository.save(sale2);
        Business business =  this.getTestBusiness();
        businessRepository.save(business);
        User user = this.getTestUser();
        userRepository.save(user);
        review1 = new Review(sale1, user, 5, "Very Good!");
        review2 = new Review(sale2, user, 1, "Not Very Good");
    }

//    /**
//     * Basic test to show that the entity can be saved.
//     */
//    @Test
//    void reviewRepository_savingEntity_noError() {
//        assertDoesNotThrow(() -> reviewRepository.save(review1));
//    }
//
//    /**
//     * Basic test to show that the entity can be saved and is actually stored.
//     */
//    @Test
//    void reviewRepository_savingEntity_isSaved() {
//        reviewRepository.save(review2);
//        Optional<Review> foundReview = reviewRepository.findById(review2.getReviewId());
//        Assertions.assertTrue(foundReview.isPresent());
//    }
}
