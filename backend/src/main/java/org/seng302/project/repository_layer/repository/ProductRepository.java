package org.seng302.project.repository_layer.repository;

import org.seng302.project.repository_layer.model.Product;
import org.seng302.project.repository_layer.model.ProductId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository for interfacing with products in the database.
 */
public interface ProductRepository extends JpaRepository<Product, ProductId>,
        JpaSpecificationExecutor<Product> {

    /**
     * Gets all products for a specific business.
     *
     * @param businessId Id of the business to filter products by.
     * @return List of products that belong to the business.
     */
    List<Product> findAllByBusinessId(@Param("businessId") Integer businessId);

    /**
     * Gets a single product based on the id and business id.
     *
     * @param id         Product id
     * @param businessId Business id
     * @return The product with the matching id and business id.
     */
    Optional<Product> findByIdAndBusinessId(@Param("id") String id, @Param("businessId") Integer businessId);
}
