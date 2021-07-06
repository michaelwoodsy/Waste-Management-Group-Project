package org.seng302.project.repositoryLayer.repository;

import org.seng302.project.repositoryLayer.model.NewKeywordNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Repository for accessing NewKeywordNotifications
 */
@RepositoryRestResource
public interface NewKeywordNotificationRepository extends JpaRepository<NewKeywordNotification, Integer> {
}
