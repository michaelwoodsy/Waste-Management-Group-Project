package org.seng302.project.repository_layer.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * BusinessNotification class for storing business' notifications.
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
@Inheritance(strategy = InheritanceType.JOINED)
public class BusinessNotification extends Notification {

    private Business business;

    /**
     * Constructor for creating a new BusinessNotification object.
     *
     * @param message Message details of notification.
     * @param business    Business to which notification is assigned to.
     */
    public BusinessNotification(String message, Business business) {
        super("business", message);
        this.business = business;
    }

    /**
     * Constructor allowing subclasses to set the type of the notification
     *
     * @param type    type of the notification
     * @param message message details of the notification
     * @param business    the business who the notification is for
     */
    public BusinessNotification(String type, String message, Business business) {
        super(type, message);
        this.business = business;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "business_id")
    public Business getBusiness() {
        return this.business;
    }
}
