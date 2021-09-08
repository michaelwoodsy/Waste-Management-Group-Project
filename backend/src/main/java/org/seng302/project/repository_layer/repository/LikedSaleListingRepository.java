package org.seng302.project.repository_layer.repository;

import org.seng302.project.repository_layer.model.LikedSaleListing;
import org.seng302.project.repository_layer.model.SaleListing;
import org.seng302.project.repository_layer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface LikedSaleListingRepository extends JpaRepository<LikedSaleListing, Integer>, PagingAndSortingRepository<LikedSaleListing, Integer> {

    /**
     * Finds all liked by sale listings and user
     * @param listing listing to search by
     * @param user user to search by
     * @return List of LikedSaleListings
     */
    List<LikedSaleListing> findByListingAndUser(SaleListing listing, User user);

    /**
     * Finds all liked sale listings by listing
     * @param listing listing to search by
     * @return List of LikedSaleListings
     */
    List<LikedSaleListing> findAllByListing(SaleListing listing);

    /**
     * Finds all liked sale listings by user
     * @param user user to search by
     * @return List of LikedSaleListings
     */
    List<LikedSaleListing> findAllByUser(User user);

    /**
     * Finds all liked sale listings by listing id
     * @param listingId listing id to search by
     * @return List of LikedSaleListings
     */
    List<LikedSaleListing> findAllByListingId(@Param("listingId") Integer listingId);

    /**
     * finds the most popular sale listings by a specified country
     * @param country country to get popular sale listings for
     * @param pageable pageable object, to limit results
     * @return A list made up of sale listing, count "pairs" (lists)
     */
    @Query("select l.listing, count(l.listing) from LikedSaleListing l where l.listing.business.address.country = :country group by l.listing order by count(l.listing) desc")
    List<List<Object>> findPopularByCountry(@Param("country") String country, Pageable pageable);

    /**
     * finds the most popular sale listings for all countries
     * @param pageable pageable object, to limit results
     * @return A list made up of sale listing, count "pairs" (lists)
     */
    @Query("select l.listing, count(l.listing) from LikedSaleListing l group by l.listing order by count(l.listing) desc")
    List<List<Object>> findPopular(Pageable pageable);

}
