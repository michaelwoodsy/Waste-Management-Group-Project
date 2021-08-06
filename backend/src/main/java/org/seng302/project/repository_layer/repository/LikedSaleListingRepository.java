package org.seng302.project.repository_layer.repository;

import org.seng302.project.repository_layer.model.LikedSaleListing;
import org.seng302.project.repository_layer.model.SaleListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository for LikedSaleListing objects in the database.
 */
public interface LikedSaleListingRepository extends JpaRepository<LikedSaleListing, Integer> {

    /**
     * Allows finding Liked Sale Listings by sale listing.
     *
     * @param listing listing to search for.
     * @return list of Liked sale listings with matching sale listing.
     */
    List<LikedSaleListing> findAllByListing(@Param("listing") SaleListing listing);

}