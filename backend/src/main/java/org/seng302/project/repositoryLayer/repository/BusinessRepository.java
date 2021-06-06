package org.seng302.project.repositoryLayer.repository;

import org.seng302.project.repositoryLayer.model.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository for interacting with Business objects in the database.
 */
public interface BusinessRepository extends JpaRepository<Business, Integer> {

    /**
     * Allows finding a business by name.
     *
     * @param name name of business.
     * @return list of businesses with matching name.
     */
    List<Business> findByName(@Param("name") String name);

}
