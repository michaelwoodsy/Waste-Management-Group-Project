package org.seng302.project.repository_layer.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/**
 * AdminNotification class for storing notifications for system administrators.
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
@Inheritance(strategy = InheritanceType.JOINED)
public class AdminNotification extends Notification {

    /**
     * Constructor for creating a new AdminNotification object
     *
     * @param message Message details of notification
     */
    public AdminNotification(String message) {
        super("admin", message);
    }

    /**
     * Constructor allowing subclasses to set the type field
     *
     * @param type type of the notification
     * @param message message details of notification
     */
    public AdminNotification(String type, String message) {
        super(type, message);
    }
}
