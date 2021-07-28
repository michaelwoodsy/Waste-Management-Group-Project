package org.seng302.project.repository_layer.repository;

import org.seng302.project.repository_layer.model.AdminNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * AdminNotification repository which acts as an interface to the database for manipulating admin notifications.
 */
@RepositoryRestResource
public interface AdminNotificationRepository extends JpaRepository<AdminNotification, Integer> {
}
