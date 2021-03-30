package org.seng302.project.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository for interacting with Business objects in the database.
 */
public interface BusinessRepository extends JpaRepository<Business, Integer> {

    List<Business> findByName(@Param("name") String name);

}
