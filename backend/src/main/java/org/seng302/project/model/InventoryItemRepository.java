package org.seng302.project.model;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository for interfacing with products in the database.
 */
public interface InventoryItemRepository extends JpaRepository<InventoryItem, String> {

    /**
     * Gets all inventory items for a specific business.
     *
     * @param businessId Id of the business to filter inventory items by.
     * @return List of inventory items that belong to the business.
     */
    @Query("from InventoryItem i where i.product.businessId = :businessId")
    List<InventoryItem> findAllByBusinessId(@Param("businessId") Integer businessId);

    /**
     * Gets a single inventory item based on the id.
     * @param id inventory item id
     * @return The inventory item with the matching id.
     */
    Optional<InventoryItem> findById(@Param("id") Integer id);
}
