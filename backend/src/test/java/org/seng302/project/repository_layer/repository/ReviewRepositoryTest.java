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
class ReviewRepositoryTest extends AbstractInitializer {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private SaleHistoryRepository saleHistoryRepository;
    @Autowired
    private BusinessRepository businessRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AddressRepository addressRepository;

    Review review;

    @BeforeEach
    void setup() {
        Sale sale = new Sale(getSaleListings().get(0));
        saleHistoryRepository.save(sale);
        User user = this.getTestUser();
        addressRepository.save(user.getHomeAddress());
        user = userRepository.save(user);
        Business business =  sale.getBusiness();
        addressRepository.save(business.getAddress());
        business = businessRepository.save(business);
        review = new Review(sale, business, user, 5, "Very Good!");
    }

    /**
     * Basic test to show that the entity can be saved.
     */
    @Test
    void reviewRepository_savingEntity_noError() {
        assertDoesNotThrow(() -> reviewRepository.save(review));
    }

    /**
     * Basic test to show that the entity can be saved and is actually stored.
     */
    @Test
    void reviewRepository_savingEntity_isSaved() {
        reviewRepository.save(review);
        Optional<Review> foundReview = reviewRepository.findById(review.getReviewId());
        Assertions.assertTrue(foundReview.isPresent());
    }
}
