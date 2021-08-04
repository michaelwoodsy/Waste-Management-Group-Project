package org.seng302.project.repository_layer.repository;

import org.seng302.project.repository_layer.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for Sale entities.
 */
public interface SaleHistoryRepository extends JpaRepository<Sale, Integer> {}
