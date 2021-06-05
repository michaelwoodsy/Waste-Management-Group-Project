package org.seng302.project.repositoryLayer.repository;

import org.seng302.project.repositoryLayer.model.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository for interfacing with products in the database.
 */
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Integer> {

    /**
     * Gets all inventory items for a specific business.
     *
     * @param businessId Id of the business to filter inventory items by.
     * @return List of inventory items that belong to the business.
     */
    @Query("from InventoryItem i where i.product.businessId = :businessId")
    List<InventoryItem> findAllByBusinessId(@Param("businessId") Integer businessId);

}
