package org.seng302.project.repository_layer.model;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Notification for a user when a business responds to their review
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
@PrimaryKeyJoinColumn(name = "review_reply_notification_id")
public class ReviewReplyNotification extends UserNotification {

    static final String MESSAGE_TEMPLATE = "%s has replied to your review.";
    private Review review;

    /**
     * Constructor for a ReviewReplyNotification
     */
    public ReviewReplyNotification(Review review) {
        super("reviewReply", "", review.getUser());
        this.setMessage(createMessage(review));
        this.review = review;
    }

    /**
     * Creates the message for the notification
     * @param review the review that has a new reply
     * @return a message saying that the business has replied
     */
    private String createMessage(Review review) {
        return String.format(MESSAGE_TEMPLATE, review.getBusiness().getName());
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "review_id")
    public Review getReview() {
        return this.review;
    }
}
