package org.seng302.project.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository for interfacing with products in the database.
 */
public interface InventoryItemRepository extends JpaRepository<InventoryItem, String> {

    /**
     * Gets all products for a specific business.
     *
     * @param businessId Id of the business to filter products by.
     * @return List of products that belong to the business.
     */
    List<InventoryItem> findAllByBusinessId(@Param("businessId") Integer businessId);

    /**
     * Gets a single product based on the id.
     * @param id Product id
     * @return The product with the matching id and business id.
     */
    Optional<InventoryItem> findById(@Param("id") Integer id);
}
