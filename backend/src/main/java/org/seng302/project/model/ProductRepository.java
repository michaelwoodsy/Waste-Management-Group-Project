package org.seng302.project.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository for interfacing with products in the database.
 */
public interface ProductRepository extends JpaRepository<Product, String> {

    /**
     * Gets all products for a specific business.
     *
     * @param businessId Id of the business to filter products by.
     * @return List of products that belong to the business.
     */
    List<Product> findAllByBusinessId(@Param("businessId") Integer businessId);
}
