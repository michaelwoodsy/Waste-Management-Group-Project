package org.seng302.project.repositoryLayer.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * UserNotification class for storing user's system notifications.
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
@Inheritance(strategy = InheritanceType.JOINED)
public class UserNotification extends Notification {

    private User user;

    /**
     * Constructor for creating a new UserNotification object.
     *
     * @param user                        User to which notification is assigned to.
     * @param message                     Message details of notification.
     */
    public UserNotification(User user, String message) {
        super(message);
        this.user = user;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    public User getUser() {
        return this.user;
    }
}
