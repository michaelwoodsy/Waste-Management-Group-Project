package org.seng302.project.repository_layer.repository;

import org.seng302.project.repository_layer.model.SaleListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleListingRepository extends JpaRepository<SaleListing, Integer>, JpaSpecificationExecutor<SaleListing> {

    /**
     * Method for getting sale listings that belong to a business.
     *
     * @param businessId The id of the business to get sale listings of.
     * @return List of sale listings
     */
    List<SaleListing> findAllByBusinessId(@Param("businessId") Integer businessId);

    /**
     * Method for getting sale listings that belong to an inventory item in a business.
     *
     * @param businessId      The id of the business the item belongs to.
     * @param inventoryItemId The id of the inventory item.
     * @return List of sale listings
     */
    List<SaleListing> findAllByBusinessIdAndInventoryItemId(
            @Param("businessId") Integer businessId,
            @Param("inventoryItemId") Integer inventoryItemId);

    /**
     * Gets all sale listings that close before a specified date.
     *
     * @param date Date the listings will have closed before.
     * @return List of sale listings.
     */
    List<SaleListing> findAllByClosesBefore(@Param("closes") LocalDateTime date);
}
