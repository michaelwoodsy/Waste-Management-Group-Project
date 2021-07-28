package org.seng302.project.repository_layer.model;

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

    private String cardTitle;

    /**
     * Constructor for creating a new CardExpiryNotification object.
     *
     * @param cardTitle title of the card this notification is about.
     */
    public CardExpiryNotification(User user, String message, String cardTitle) {
        super("cardExpiry", message, user);
        this.cardTitle = cardTitle;
    }

}
