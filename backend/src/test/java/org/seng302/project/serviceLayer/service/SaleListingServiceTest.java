package org.seng302.project.serviceLayer.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.project.repositoryLayer.model.Business;
import org.seng302.project.repositoryLayer.model.InventoryItem;
import org.seng302.project.repositoryLayer.model.Product;
import org.seng302.project.repositoryLayer.model.SaleListing;
import org.seng302.project.repositoryLayer.repository.BusinessRepository;
import org.seng302.project.repositoryLayer.repository.InventoryItemRepository;
import org.seng302.project.repositoryLayer.repository.ProductRepository;
import org.seng302.project.repositoryLayer.repository.SaleListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
class SaleListingServiceTest {

    private final BusinessRepository businessRepository;
    private final ProductRepository productRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final SaleListingRepository saleListingRepository;
    private final SaleListingService saleListingService;

    @Autowired
    SaleListingServiceTest(BusinessRepository businessRepository,
                           ProductRepository productRepository,
                           InventoryItemRepository inventoryItemRepository,
                           SaleListingRepository saleListingRepository) {
        this.businessRepository = businessRepository;
        this.productRepository = productRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.saleListingRepository = saleListingRepository;
        this.saleListingService = new SaleListingService(this.saleListingRepository, this.businessRepository);
    }

    /**
     * Before each test, setup two sale listings with different parameters
     */
    @BeforeEach
    void setup() {
        Business business1 = new Business("First Business", null, null, "Retail Trade", 1);
        businessRepository.save(business1);
        Product product1 = new Product("TEST-1", "First Product", null, null, 5.00, business1.getId());
        productRepository.save(product1);
        InventoryItem inventoryItem1 = new InventoryItem(product1, 5, null, null, "2021-01-01", null, null, "2021-12-31");
        inventoryItemRepository.save(inventoryItem1);
        SaleListing saleListing1 = new SaleListing(business1.getId(), inventoryItem1, 10.00, null, LocalDateTime.parse("2021-08-25T00:00:00"), 5);
        saleListingRepository.save(saleListing1);

        Business business2 = new Business("Second Business", null, null, "Retail Trade", 1);
        businessRepository.save(business2);
        Product product2 = new Product("TEST-2", "Second Product", null, null, 5.00, business2.getId());
        productRepository.save(product2);
        InventoryItem inventoryItem2 = new InventoryItem(product2, 5, null, null, "2021-01-01", null, null, "2021-12-31");
        inventoryItemRepository.save(inventoryItem2);
        SaleListing saleListing2 = new SaleListing(business2.getId(), inventoryItem2, 20.00, null, LocalDateTime.parse("2021-11-25T00:00:00"), 5);
        saleListingRepository.save(saleListing2);
    }

    /**
     * Tests that searching for listing by business name with string 'first' returns first listing
     */
    @Test
    void searchByBusinessName_firstBusiness_returnsFirstListing() {
        String searchTerm = "first";
        Specification<SaleListing> spec = saleListingService.searchByBusinessName(new String[]{searchTerm});
        List<SaleListing> listings = saleListingRepository.findAll(spec);
        Assertions.assertEquals(1, listings.size());
        Assertions.assertEquals("First Product", listings.get(0).getInventoryItem().getProduct().getName());
    }

    /**
     * Tests that searching for listing by business name with string 'second' returns second listing
     */
    @Test
    void searchByBusinessName_secondBusiness_returnsSecondListing() {
        String searchTerm = "second";
        Specification<SaleListing> spec = saleListingService.searchByBusinessName(new String[]{searchTerm});
        List<SaleListing> listings = saleListingRepository.findAll(spec);
        Assertions.assertEquals(1, listings.size());
        Assertions.assertEquals("Second Product", listings.get(0).getInventoryItem().getProduct().getName());
    }

    /**
     * Tests that searching for listing by business name with string 'business' returns both listings
     */
    @Test
    void searchByBusinessName_business_returnsBothListings() {
        String searchTerm = "business";
        Specification<SaleListing> spec = saleListingService.searchByBusinessName(new String[]{searchTerm});
        List<SaleListing> listings = saleListingRepository.findAll(spec);
        Assertions.assertEquals(2, listings.size());
    }

    /**
     * Tests that searching for listing by business name with string '"first"' returns nothing
     */
    @Test
    void searchByBusinessName_firstExact_returnsNothing() {
        String searchTerm = "\"first\"";
        Specification<SaleListing> spec = saleListingService.searchByBusinessName(new String[]{searchTerm});
        List<SaleListing> listings = saleListingRepository.findAll(spec);
        Assertions.assertEquals(0, listings.size());
    }

    /**
     * Tests that searching for listing by business name with string '"first business"' returns first listing
     */
    @Test
    void searchByBusinessName_firstBusinessExact_returnsFirstListing() {
        String searchTerm = "\"first business\"";
        Specification<SaleListing> spec = saleListingService.searchByBusinessName(new String[]{searchTerm});
        List<SaleListing> listings = saleListingRepository.findAll(spec);
        Assertions.assertEquals(1, listings.size());
        Assertions.assertEquals("First Product", listings.get(0).getInventoryItem().getProduct().getName());
    }

    /**
     * Tests that searching for listing by business name with string 'random' returns nothing
     */
    @Test
    void searchByBusinessName_random_returnsNothing() {
        String searchTerm = "random";
        Specification<SaleListing> spec = saleListingService.searchByBusinessName(new String[]{searchTerm});
        List<SaleListing> listings = saleListingRepository.findAll(spec);
        Assertions.assertEquals(0, listings.size());
    }

    /**
     * Tests that searching for listing by business name with string 'first' and 'second' returns both listings
     */
    @Test
    void searchByBusinessName_firstOrSecond_returnsBothListings() {
        Specification<SaleListing> spec = saleListingService.searchByBusinessName(new String[]{"first", "second"});
        List<SaleListing> listings = saleListingRepository.findAll(spec);
        Assertions.assertEquals(2, listings.size());
    }

    /**
     * Tests that searching for listing by business name with string 'first' and 'second' returns both listings
     */
    @Test
    void searchByBusinessName_firstAndSecond_returnsNothing() {
        String searchTerm = "first AND second";
        Specification<SaleListing> spec = saleListingService.searchByBusinessName(new String[]{searchTerm});
        List<SaleListing> listings = saleListingRepository.findAll(spec);
        Assertions.assertEquals(0, listings.size());
    }

}
