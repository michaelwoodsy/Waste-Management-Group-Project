package org.seng302.project.repositoryLayer.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.project.repositoryLayer.repository.BusinessRepository;
import org.seng302.project.repositoryLayer.repository.InventoryItemRepository;
import org.seng302.project.repositoryLayer.repository.ProductRepository;
import org.seng302.project.repositoryLayer.repository.SaleListingRepository;
import org.seng302.project.repositoryLayer.specification.SaleListingSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
class SaleListingSpecificationsTest {

    @Autowired
    private BusinessRepository businessRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private InventoryItemRepository inventoryItemRepository;
    @Autowired
    private SaleListingRepository saleListingRepository;

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
     * Test that using the hasName spec with "First Product" returns sale listing with first product
     */
    @Test
    void hasName_firstProduct_returnsFirstListing() {
        Specification<SaleListing> spec = SaleListingSpecifications.hasProductName("first product");
        List<SaleListing> result = saleListingRepository.findAll(spec);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("First Product", result.get(0).getInventoryItem().getProduct().getName());
    }

    /**
     * Test that using the hasName spec with "Second Product" returns sale listing with second product
     */
    @Test
    void hasName_secondProduct_returnsSecondListing() {
        Specification<SaleListing> spec = SaleListingSpecifications.hasProductName("second product");
        List<SaleListing> result = saleListingRepository.findAll(spec);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Second Product", result.get(0).getInventoryItem().getProduct().getName());
    }

    /**
     * Test that using the hasName spec with "Product" returns nothing
     */
    @Test
    void hasName_product_returnsNothing() {
        Specification<SaleListing> spec = SaleListingSpecifications.hasProductName("product");
        List<SaleListing> result = saleListingRepository.findAll(spec);
        Assertions.assertEquals(0, result.size());
    }

    /**
     * Test that using the containsName spec with "Product" returns both sale listings
     */
    @Test
    void containsName_product_returnsBothListings() {
        Specification<SaleListing> spec = SaleListingSpecifications.containsProductName("product");
        List<SaleListing> result = saleListingRepository.findAll(spec);
        Assertions.assertEquals(2, result.size());
    }

    /**
     * Test that using the containsName spec with "Random" returns nothing
     */
    @Test
    void containsName_random_returnsNothing() {
        Specification<SaleListing> spec = SaleListingSpecifications.containsProductName("random");
        List<SaleListing> result = saleListingRepository.findAll(spec);
        Assertions.assertEquals(0, result.size());
    }

    /**
     * Test that using the closesBefore spec with date in December returns both sale listings
     */
    @Test
    void closesBefore_december_returnsBothListings() {
        Specification<SaleListing> spec = SaleListingSpecifications.closesBefore(LocalDateTime.parse("2021-12-31T00:00:00"));
        List<SaleListing> result = saleListingRepository.findAll(spec);
        Assertions.assertEquals(2, result.size());
    }

    /**
     * Test that using the closesBefore spec with date in October returns first sale listing
     */
    @Test
    void closesBefore_october_returnsFirstListing() {
        Specification<SaleListing> spec = SaleListingSpecifications.closesBefore(LocalDateTime.parse("2021-10-30T00:00:00"));
        List<SaleListing> result = saleListingRepository.findAll(spec);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("First Product", result.get(0).getInventoryItem().getProduct().getName());
    }

    /**
     * Test that using the closesBefore spec with date in June returns nothing
     */
    @Test
    void closesBefore_june_returnsNothing() {
        Specification<SaleListing> spec = SaleListingSpecifications.closesBefore(LocalDateTime.parse("2021-06-30T00:00:00"));
        List<SaleListing> result = saleListingRepository.findAll(spec);
        Assertions.assertEquals(0, result.size());
    }

    /**
     * Test that using the closesAfter spec with date in June returns both sale listings
     */
    @Test
    void closesAfter_june_returnsBothListings() {
        Specification<SaleListing> spec = SaleListingSpecifications.closesAfter(LocalDateTime.parse("2021-06-30T00:00:00"));
        List<SaleListing> result = saleListingRepository.findAll(spec);
        Assertions.assertEquals(2, result.size());
    }

    /**
     * Test that using the closesAfter spec with date in October returns second sale listing
     */
    @Test
    void closesAfter_october_returnsSecondListing() {
        Specification<SaleListing> spec = SaleListingSpecifications.closesAfter(LocalDateTime.parse("2021-10-30T00:00:00"));
        List<SaleListing> result = saleListingRepository.findAll(spec);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Second Product", result.get(0).getInventoryItem().getProduct().getName());
    }

    /**
     * Test that using the closesAfter spec with date in December returns nothing
     */
    @Test
    void closesAfter_december_returnsNothing() {
        Specification<SaleListing> spec = SaleListingSpecifications.closesAfter(LocalDateTime.parse("2021-12-30T00:00:00"));
        List<SaleListing> result = saleListingRepository.findAll(spec);
        Assertions.assertEquals(0, result.size());
    }

    /**
     * Test that using the priceLessThan spec with 5.00 returns nothing
     */
    @Test
    void priceLessThan_five_returnsNothing() {
        Specification<SaleListing> spec = SaleListingSpecifications.priceLessThan(5.00);
        List<SaleListing> result = saleListingRepository.findAll(spec);
        Assertions.assertEquals(0, result.size());
    }

    /**
     * Test that using the priceLessThan spec with 15.00 returns first sale listing
     */
    @Test
    void priceLessThan_fifteen_returnsFirstListing() {
        Specification<SaleListing> spec = SaleListingSpecifications.priceLessThan(15.00);
        List<SaleListing> result = saleListingRepository.findAll(spec);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("First Product", result.get(0).getInventoryItem().getProduct().getName());
    }

    /**
     * Test that using the priceLessThan spec with 25.00 returns both sale listings
     */
    @Test
    void priceLessThan_twentyFive_returnsBothListings() {
        Specification<SaleListing> spec = SaleListingSpecifications.priceLessThan(25.00);
        List<SaleListing> result = saleListingRepository.findAll(spec);
        Assertions.assertEquals(2, result.size());
    }

    /**
     * Test that using the priceGreaterThan spec with 5.00 returns both sale listings
     */
    @Test
    void priceGreaterThan_five_returnsBothListings() {
        Specification<SaleListing> spec = SaleListingSpecifications.priceGreaterThan(5.00);
        List<SaleListing> result = saleListingRepository.findAll(spec);
        Assertions.assertEquals(2, result.size());
    }

    /**
     * Test that using the priceGreaterThan spec with 15.00 returns second sale listing
     */
    @Test
    void priceGreaterThan_fifteen_returnsSecondListing() {
        Specification<SaleListing> spec = SaleListingSpecifications.priceGreaterThan(15.00);
        List<SaleListing> result = saleListingRepository.findAll(spec);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Second Product", result.get(0).getInventoryItem().getProduct().getName());
    }

    /**
     * Test that using the priceGreaterThan spec with 25.00 returns nothing
     */
    @Test
    void priceGreaterThan_twentyFive_returnsNothing() {
        Specification<SaleListing> spec = SaleListingSpecifications.priceGreaterThan(25.00);
        List<SaleListing> result = saleListingRepository.findAll(spec);
        Assertions.assertEquals(0, result.size());
    }

    /**
     * Tests that finding sale listing by business ID 1 returns first listing
     */
    @Test
    void isBusinessId_firstBusiness_returnsFirstListing() {
        List<Business> business = businessRepository.findByName("First Business");
        Specification<SaleListing> spec = SaleListingSpecifications.isBusinessId(business.get(0).getId());
        List<SaleListing> result = saleListingRepository.findAll(spec);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("First Product", result.get(0).getInventoryItem().getProduct().getName());
    }

    /**
     * Tests that finding sale listing by business ID 1 returns first listing
     */
    @Test
    void isBusinessId_secondBusiness_returnsSecondListing() {
        List<Business> business = businessRepository.findByName("Second Business");
        Specification<SaleListing> spec = SaleListingSpecifications.isBusinessId(business.get(0).getId());
        List<SaleListing> result = saleListingRepository.findAll(spec);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Second Product", result.get(0).getInventoryItem().getProduct().getName());
    }

    /**
     * Tests that finding sale listing by business ID 1 returns first listing
     */
    @Test
    void isBusinessId_nonExistentBusiness_returnsNothing() {
        Specification<SaleListing> spec = SaleListingSpecifications.isBusinessId(1000);
        List<SaleListing> result = saleListingRepository.findAll(spec);
        Assertions.assertEquals(0, result.size());
    }

}
