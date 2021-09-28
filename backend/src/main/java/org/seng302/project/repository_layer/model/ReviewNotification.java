package org.seng302.project.repository_layer.model;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Notification for a business when a user leaves them a review
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
@PrimaryKeyJoinColumn(name = "review_notification_id")
public class ReviewNotification extends BusinessNotification {

    static final String MESSAGE_TEMPLATE = "%s has left you a review.";
    private Review review;

    /**
     * Constructor for a ReviewNotification
     */
    public ReviewNotification(Review review) {
        super("review", "", review.getBusiness());
        this.setMessage(createMessage(review));
        this.review = review;
    }

    /**
     * Creates the message for the notification
     * @param review the new review
     * @return a message saying that a user has left a review
     */
    private String createMessage(Review review) {
        return String.format(MESSAGE_TEMPLATE, review.getUser().getFirstName());
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "review_id")
    public Review getReview() {
        return this.review;
    }
}
