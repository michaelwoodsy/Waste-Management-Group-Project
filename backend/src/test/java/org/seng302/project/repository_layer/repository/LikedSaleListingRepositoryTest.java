package org.seng302.project.repository_layer.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DataJpaTest
class LikedSaleListingRepositoryTest extends AbstractInitializer {

    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BusinessRepository businessRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private InventoryItemRepository inventoryItemRepository;
    @Autowired
    private SaleListingRepository saleListingRepository;
    @Autowired
    private LikedSaleListingRepository likedSaleListingRepository;

    /**
     * Before each test, setup four sale listings with different parameters
     */
    @BeforeEach
    void setup() {
        this.initialise();
        List<SaleListing> listings = this.getSaleListings();
        User user1 = this.getTestUser();
        user1.setId(null);
        addressRepository.save(user1.getHomeAddress());
        userRepository.save(user1);
        User user2 = this.getTestOtherUser();
        user2.setId(null);
        addressRepository.save(user2.getHomeAddress());
        userRepository.save(user2);
        User user3 = this.getTestSystemAdmin();
        user3.setId(null);
        addressRepository.save(user3.getHomeAddress());
        userRepository.save(user3);

        for (SaleListing listing: listings) {
            addressRepository.save(listing.getBusiness().getAddress());
            Business business = businessRepository.save(listing.getBusiness());
            listing.getInventoryItem().getProduct().setBusinessId(business.getId());
            Product product = productRepository.save(listing.getInventoryItem().getProduct());
            listing.getInventoryItem().setProduct(product);
            InventoryItem item = inventoryItemRepository.save(listing.getInventoryItem());
            listing.setInventoryItem(item);
            saleListingRepository.save(listing);
        }
        listings = saleListingRepository.findAll();
        //Make users like some listings

        LikedSaleListing like = new LikedSaleListing(user1, listings.get(0));
        likedSaleListingRepository.save(like);
        like = new LikedSaleListing(user2, listings.get(0));
        likedSaleListingRepository.save(like);

        like = new LikedSaleListing(user1, listings.get(1));
        likedSaleListingRepository.save(like);

        like = new LikedSaleListing(user2, listings.get(2));
        likedSaleListingRepository.save(like);

        like = new LikedSaleListing(user1, listings.get(3));
        likedSaleListingRepository.save(like);
        like = new LikedSaleListing(user2, listings.get(3));
        likedSaleListingRepository.save(like);
        like = new LikedSaleListing(user3, listings.get(3));
        likedSaleListingRepository.save(like);
    }

    /**
     * Test the findPopularByCountry method with New Zealand as the country.
     */
    @Test
    void likedSaleListing_findPopularByCountry_NewZealand() {
        List<List<Object>> response = likedSaleListingRepository.findPopularByCountry("New Zealand");
        Assertions.assertEquals(2, response.size());
        //Checking that the first listing has more likes than the second listing
        Long likes1 = (Long) response.get(0).get(1);
        Long likes2 = (Long) response.get(1).get(1);
        Assertions.assertTrue(likes1 >= likes2);

        SaleListing listing1 = (SaleListing) response.get(0).get(0);
        Assertions.assertEquals("New Zealand", listing1.getBusiness().getAddress().getCountry());
        SaleListing listing2 = (SaleListing) response.get(1).get(0);
        Assertions.assertEquals("New Zealand", listing2.getBusiness().getAddress().getCountry());
    }

    /**
     * Test the findPopularByCountry method with Netherlands as the country.
     */
    @Test
    void likedSaleListing_findPopularByCountry_Netherlands() {
        List<List<Object>> response = likedSaleListingRepository.findPopularByCountry("Netherlands");
        Assertions.assertEquals(2, response.size());
        //Checking that the first listing has more likes than the second listing
        Long likes1 = (Long) response.get(0).get(1);
        Long likes2 = (Long) response.get(1).get(1);
        Assertions.assertTrue(likes1 >= likes2);

        SaleListing listing1 = (SaleListing) response.get(0).get(0);
        Assertions.assertEquals("Netherlands", listing1.getBusiness().getAddress().getCountry());
        SaleListing listing2 = (SaleListing) response.get(1).get(0);
        Assertions.assertEquals("Netherlands", listing2.getBusiness().getAddress().getCountry());
    }
}
