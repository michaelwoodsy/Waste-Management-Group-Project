package org.seng302.project.repository_layer.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Notification for a user that had a sale listing liked when it was sold.
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
@PrimaryKeyJoinColumn(name = "notification_id")
public class InterestedUserNotification extends UserNotification {

    static final String MESSAGE_TEMPLATE = "The sale listing for '%s' you liked has been sold to another user.";

    public InterestedUserNotification(User user, SaleListing saleListing) {
        super( );
        var product = saleListing.getInventoryItem().getProduct();
        this.setType("interested");
        this.setUser(user);
        this.setMessage(createMessage(product));
    }

    /**
     * Returns the message for the notification.
     * @param product Product that was listed.
     * @return Message to be returned with notification.
     */
    private String createMessage(Product product) {
        return String.format(MESSAGE_TEMPLATE, product.getName());
    }

}
