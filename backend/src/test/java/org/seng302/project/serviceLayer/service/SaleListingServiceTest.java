package org.seng302.project.serviceLayer.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.seng302.project.repositoryLayer.model.Business;
import org.seng302.project.repositoryLayer.model.InventoryItem;
import org.seng302.project.repositoryLayer.model.Product;
import org.seng302.project.repositoryLayer.model.SaleListing;
import org.seng302.project.repositoryLayer.repository.BusinessRepository;
import org.seng302.project.repositoryLayer.repository.InventoryItemRepository;
import org.seng302.project.repositoryLayer.repository.ProductRepository;
import org.seng302.project.repositoryLayer.repository.SaleListingRepository;
import org.seng302.project.repositoryLayer.specification.SaleListingSpecifications;
import org.seng302.project.serviceLayer.dto.saleListings.GetSalesListingDTO;
import org.seng302.project.serviceLayer.dto.saleListings.SearchSaleListingsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DataJpaTest
class SaleListingServiceTest {

    @Autowired
    private BusinessRepository businessRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private InventoryItemRepository inventoryItemRepository;
    @Autowired
    private SaleListingRepository saleListingRepository;

    private SalesListingService salesListingService;

    /**
     * Before each test, setup four sale listings with different parameters
     */
    @BeforeEach
    void setup() {
        salesListingService = new SalesListingService(saleListingRepository, businessRepository);


        Business business1 = new Business("First Business", null, null, "Retail Trade", 1);
        businessRepository.save(business1);

        Product product1 = new Product("TEST-1", "First Product", null, null, 5.00, business1.getId());
        productRepository.save(product1);
        InventoryItem inventoryItem1 = new InventoryItem(product1, 5, null, null, "2021-01-01", null, null, "2021-12-31");
        inventoryItemRepository.save(inventoryItem1);
        SaleListing saleListing1 = new SaleListing(business1.getId(), inventoryItem1, 10.00, null, LocalDateTime.parse("2021-08-25T00:00:00"), 5);
        saleListingRepository.save(saleListing1);

        Product product2 = new Product("TEST-2", "Second Product", null, null, 5.00, business1.getId());
        productRepository.save(product2);
        InventoryItem inventoryItem2 = new InventoryItem(product2, 10, null, null, "2021-01-01", null, null, "2021-12-31");
        inventoryItemRepository.save(inventoryItem1);
        SaleListing saleListing2 = new SaleListing(business1.getId(), inventoryItem2, 15.00, null, LocalDateTime.parse("2021-10-25T00:00:00"), 10);
        saleListingRepository.save(saleListing2);



        Business business2 = new Business("Second Business", null, null, "Retail Trade", 1);
        businessRepository.save(business2);

        Product product3 = new Product("TEST-3", "Third Product", null, null, 5.00, business2.getId());
        productRepository.save(product3);
        InventoryItem inventoryItem3 = new InventoryItem(product3, 5, null, null, "2021-01-01", null, null, "2021-12-31");
        inventoryItemRepository.save(inventoryItem3);
        SaleListing saleListing3 = new SaleListing(business2.getId(), inventoryItem3, 20.00, null, LocalDateTime.parse("2021-11-25T00:00:00"), 5);
        saleListingRepository.save(saleListing3);

        Product product4 = new Product("TEST-4", "Fourth Product", null, null, 5.00, business2.getId());
        productRepository.save(product4);
        InventoryItem inventoryItem4 = new InventoryItem(product4, 5, null, null, "2021-01-01", null, null, "2021-12-31");
        inventoryItemRepository.save(inventoryItem4);
        SaleListing saleListing4 = new SaleListing(business2.getId(), inventoryItem4, 30.00, null, LocalDateTime.parse("2021-12-25T00:00:00"), 5);
        saleListingRepository.save(saleListing4);
    }

    /**
     * Test that using an empty search returns the four sales listings
     */
    @Test
    void emptySearch_returns_four_listings() {
        SearchSaleListingsDTO dto = new SearchSaleListingsDTO(
                "",
                true,
                false,
                false,
                null,
                null,
                null,
                null,
                "",
                0
        );

        List<Object> response = salesListingService.searchSaleListings(dto);
        System.out.println(response);
        List<GetSalesListingDTO> listings = (List<GetSalesListingDTO>) response.get(0);
        long total = (long) response.get(1);

        Assertions.assertEquals(4, total);
    }

    /**
     * Test that searching by "fou" returns one sale listing with name "Fourth Product"
     */
    @Test
    void search_fou_returns_one_listing() {
        SearchSaleListingsDTO dto = new SearchSaleListingsDTO(
                "fou",
                true,
                false,
                false,
                null,
                null,
                null,
                null,
                "",
                0
        );

        List<Object> response = salesListingService.searchSaleListings(dto);
        System.out.println(response);
        List<GetSalesListingDTO> listings = (List<GetSalesListingDTO>) response.get(0);
        long total = (long) response.get(1);

        Assertions.assertEquals(1, total);

        GetSalesListingDTO listing = listings.get(0);

        Assertions.assertEquals("Fourth Product", listing.getInventoryItem().getProduct().getName());
    }

    /**
     * Test that searching by ""First Product"" (in quotes) returns one sale listing with name "First Product"
     */
    @Test
    void search_product_name_quoted_returns_one_listing() {
        SearchSaleListingsDTO dto = new SearchSaleListingsDTO(
                "\"First Product\"",
                true,
                false,
                false,
                null,
                null,
                null,
                null,
                "",
                0
        );

        List<Object> response = salesListingService.searchSaleListings(dto);
        System.out.println(response);
        List<GetSalesListingDTO> listings = (List<GetSalesListingDTO>) response.get(0);
        long total = (long) response.get(1);

        Assertions.assertEquals(1, total);

        GetSalesListingDTO listing = listings.get(0);

        Assertions.assertEquals("First Product", listing.getInventoryItem().getProduct().getName());
    }

}
