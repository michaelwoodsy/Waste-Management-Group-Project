package org.seng302.project.repositoryLayer.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * NewKeywordNotification class for notifications that inform system administrators
 * of new keywords.
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
@PrimaryKeyJoinColumn(name = "new_card_expiry_notification_id")
public class CardExpiryNotification extends UserNotification {

    private Card card;

    /**
     * Constructor for creating a new CardExpiryNotification object.
     *
     * @param card                     card this notification is about.
     */
    public CardExpiryNotification(User user, String message,  Card card) {
        super(user, message);
        this.card = card;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "card_id")
    public Card getCard() {
        return this.card;
    }
}
