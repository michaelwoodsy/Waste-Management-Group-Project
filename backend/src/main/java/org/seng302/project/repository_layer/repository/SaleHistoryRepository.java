package org.seng302.project.repository_layer.repository;

import org.seng302.project.repository_layer.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Repository for Sale entities.
 */
public interface SaleHistoryRepository extends JpaRepository<Sale, Integer> {

    Optional<Sale> findByOldListingId(@Param("oldListingId") Integer oldListingId);
}
