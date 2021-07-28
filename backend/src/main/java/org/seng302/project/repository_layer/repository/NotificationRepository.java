package org.seng302.project.repository_layer.repository;

import org.seng302.project.repository_layer.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Notification repository which acts as an interface to the database for manipulating notifications.
 */
@RepositoryRestResource
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
}
