package org.seng302.project.repositoryLayer.repository;

import org.seng302.project.repositoryLayer.model.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * UserNotification repository which acts as an interface to the database for manipulating user notifications.
 */
@RepositoryRestResource
public interface UserNotificationRepository extends JpaRepository<UserNotification, Integer> {

}
