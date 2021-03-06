package org.seng302.project.repository_layer.repository;

import org.seng302.project.repository_layer.model.User;
import org.seng302.project.repository_layer.model.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * UserNotification repository which acts as an interface to the database for manipulating user notifications.
 */
@RepositoryRestResource
public interface UserNotificationRepository extends JpaRepository<UserNotification, Integer> {
    List<UserNotification> findAllByUser(@Param("user") User user);
}
