package org.seng302.project.repository_layer.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Parent class for all notifications
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@MappedSuperclass
public class Notification {

    private Integer id;
    private String type;
    private String message;
    private boolean hasRead;
    private LocalDateTime created = LocalDateTime.now();

    /**
     * Constructor for creating a new Notification object.
     * @param message Message details of notification.
     */
    public Notification(String type, String message) {
        this.type = type;
        this.message = message;
        this.hasRead = false;
    }

    @Id // this field (attribute) is the primary key of the table
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement the ID
    @Column(name = "notification_id")
    public Integer getId() {
        return this.id;
    }

    @Column(name = "has_read")
    public boolean isHasRead() {
        return this.hasRead;
    }
}
