package org.seng302.project.repository_layer.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity for a review that has been left on a sale.
 */
@Data
@NoArgsConstructor
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Integer reviewId;

    @OneToOne
    @JoinColumn(name = "sale_id")
    private Sale sale;

    @ManyToOne
    @JoinColumn(name = "business_id")
    private Business business;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Integer rating;
    private String reviewMessage;
    private String reviewResponse;
    private LocalDateTime created = LocalDateTime.now();

    public Review(Sale sale, Business business, User user, Integer rating, String reviewMessage) {
        this.sale = sale;
        this.business = sale.getBusiness();
        this.user = user;
        this.rating = rating;
        this.reviewMessage = reviewMessage;
        this.reviewResponse = null;
    }
}
