package org.seng302.project.repository_layer.specification;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.LikedSaleListing;
import org.seng302.project.repository_layer.model.SaleListing;
import org.seng302.project.repository_layer.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@DataJpaTest
class LikedSaleListingSpecificationTest extends AbstractInitializer {

    @Autowired
    private LikedSaleListingRepository likedSaleListingRepository;
    @Autowired
    private BusinessRepository businessRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private InventoryItemRepository inventoryItemRepository;
    @Autowired
    private SaleListingRepository saleListingRepository;

    private SaleListing saleListingNew;
    private SaleListing saleListingOld;

    @BeforeEach
    void setup() {
        // create and save test sale listings
        saleListingNew = getSaleListings().get(0);
        saleListingOld = getSaleListings().get(1);

        // save dependant entities (business, ect..)
        saleListingNew.setBusiness(businessRepository.save(saleListingNew.getBusiness()));
        saleListingOld.setBusiness(businessRepository.save(saleListingOld.getBusiness()));

        // don't need these, and will make a transient entity error if we don't get rid of them
        saleListingNew.getBusiness().setAddress(null);
        saleListingOld.getBusiness().setAddress(null);
        saleListingNew.setInventoryItem(null);
        saleListingOld.setInventoryItem(null);

        // set closing time of the two sale listings
        saleListingNew.setCloses(parseStringToDateTime("2021-09-05 14:00"));
        saleListingOld.setCloses(parseStringToDateTime("2021-09-03 14:00"));

        // save sale listings
        saleListingNew = saleListingRepository.save(saleListingNew);
        saleListingOld = saleListingRepository.save(saleListingOld);
    }

    /**
     * Takes a date time string with format yyyy-MM-dd HH:mm and converts to a LocalDateTime object.
     *
     * @param dateTime Date time string with format yyyy-MM-dd HH:mm.
     * @return LocalDateTime at the time specified in dateTime string.
     */
    private LocalDateTime parseStringToDateTime(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return LocalDateTime.parse(dateTime, formatter);
    }

    /**
     * Tests the saleListingClosesBefore specification method returns only likedSaleListings for sale listings expired
     * when there is only two likedSaleListings.
     */
    @Test
    void saleListingClosesBefore_blueSkySmall_returnsExpected() {
        // create two liked listings and save
        var likedListingOld = new LikedSaleListing();
        likedListingOld.setListing(saleListingOld);
        var likedListingNew = new LikedSaleListing();
        likedListingNew.setListing(saleListingNew);
        likedListingOld = likedSaleListingRepository.save(likedListingOld);
        likedSaleListingRepository.save(likedListingNew);

        // get listings before 2021-09-04
        Specification<LikedSaleListing> spec = LikedSaleListingSpecification
                .saleListingClosesBefore(parseStringToDateTime("2021-09-04 14:00"));
        List<LikedSaleListing> foundLikedListings = likedSaleListingRepository.findAll(spec);

        // check it is just likedListingOld
        Assertions.assertEquals(List.of(likedListingOld), foundLikedListings);
    }

    /**
     * Tests the saleListingClosesBefore specification method returns only likedSaleListings for sale listings expired
     * when there is three likedSaleListings and all expired.
     */
    @Test
    void saleListingClosesBefore_blueSkyMedium_returnsExpected() {
        // create two liked listings and save
        var likedListingOld1 = new LikedSaleListing();
        likedListingOld1.setListing(saleListingOld);
        likedListingOld1 = likedSaleListingRepository.save(likedListingOld1);
        var likedListingOld2 = new LikedSaleListing();
        likedListingOld2.setListing(saleListingOld);
        likedListingOld2 = likedSaleListingRepository.save(likedListingOld2);
        var likedListingNew1 = new LikedSaleListing();
        likedListingNew1.setListing(saleListingNew);
        likedListingNew1 = likedSaleListingRepository.save(likedListingNew1);
        likedSaleListingRepository.save(likedListingNew1);

        // get listings before 2021-09-04
        Specification<LikedSaleListing> spec = LikedSaleListingSpecification
                .saleListingClosesBefore(parseStringToDateTime("2021-09-05 14:00"));
        List<LikedSaleListing> foundLikedListings = likedSaleListingRepository.findAll(spec);

        // check it is just likedListingOld
        Assertions.assertEquals(List.of(likedListingOld1, likedListingOld2, likedListingNew1), foundLikedListings);
    }

}
