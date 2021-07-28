package org.seng302.project.repository_layer.repository;

import org.seng302.project.repository_layer.model.Business;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository for interacting with Business objects in the database.
 */
public interface BusinessRepository extends JpaRepository<Business, Integer>, JpaSpecificationExecutor<Business> {

    /**
     * Allows finding a business by name.
     *
     * @param name name of business.
     * @return list of businesses with matching name.
     */
    List<Business> findByName(@Param("name") String name);

    /**
     * Used when searching for businesses
     * @param sort this is used to sort the search
     * @return list of businesses that match the sort
     */
    List<Business> findAll(Sort sort);
}
