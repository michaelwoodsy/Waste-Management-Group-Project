package org.seng302.project.repository_layer.repository;

import org.seng302.project.repository_layer.model.Business;
import org.seng302.project.repository_layer.model.BusinessNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * BusinessNotification repository which acts as an interface to the database for manipulating business notifications.
 */
@RepositoryRestResource
public interface BusinessNotificationRepository extends JpaRepository<BusinessNotification, Integer> {
    List<BusinessNotification> findAllByBusiness(@Param("business") Business business);
}
