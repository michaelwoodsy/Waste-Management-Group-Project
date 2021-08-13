package org.seng302.project.repository_layer.repository;

import org.seng302.project.repository_layer.model.LikedSaleListing;
import org.seng302.project.repository_layer.model.SaleListing;
import org.seng302.project.repository_layer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LikedSaleListingRepository extends JpaRepository<LikedSaleListing, Integer> {

    List<LikedSaleListing> findByListingAndUser(SaleListing listing, User user);

    List<LikedSaleListing> findAllByListing(SaleListing listing);

    List<LikedSaleListing> findAllByUser(User user);

    List<LikedSaleListing> findAllByListingId(@Param("listingId") Integer listingId);

}
