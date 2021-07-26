package org.seng302.project.serviceLayer.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.project.repositoryLayer.model.*;
import org.seng302.project.repositoryLayer.repository.*;
import org.seng302.project.serviceLayer.dto.saleListings.GetSalesListingDTO;
import org.seng302.project.serviceLayer.dto.saleListings.SearchSaleListingsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
class SaleListingServiceTest {

    private final BusinessRepository businessRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final SaleListingRepository saleListingRepository;
    private final SaleListingService saleListingService;

    Integer business1Id;
    Integer business2Id;

    @Autowired
    SaleListingServiceTest(BusinessRepository businessRepository,
                           AddressRepository addressRepository,
                           ProductRepository productRepository,
                           InventoryItemRepository inventoryItemRepository,
                           SaleListingRepository saleListingRepository) {
        this.businessRepository = businessRepository;
        this.addressRepository = addressRepository;
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
        Address address1 = new Address(null, null, null, null, "Netherlands", null);
        Business business1 = new Business("First Business", null, address1, "Retail Trade", 1);
        addressRepository.save(address1);
        businessRepository.save(business1);
        business1Id = business1.getId();

        Product product1 = new Product("TEST-1", "First Product", null, null, 5.00, business1.getId());
        productRepository.save(product1);
        InventoryItem inventoryItem1 = new InventoryItem(product1, 5, null, null, "2021-01-01", null, null, "2021-12-31");
        inventoryItemRepository.save(inventoryItem1);
        SaleListing saleListing1 = new SaleListing(business1.getId(), inventoryItem1, 20.00, null, LocalDateTime.parse("2021-08-25T00:00:00"), 5);
        saleListingRepository.save(saleListing1);

        Product product2 = new Product("TEST-2", "Second Product", null, null, 5.00, business1.getId());
        productRepository.save(product2);
        InventoryItem inventoryItem2 = new InventoryItem(product2, 10, null, null, "2021-01-01", null, null, "2021-12-31");
        inventoryItemRepository.save(inventoryItem1);
        SaleListing saleListing2 = new SaleListing(business1.getId(), inventoryItem2, 15.00, null, LocalDateTime.parse("2021-10-25T00:00:00"), 10);
        saleListingRepository.save(saleListing2);


        Address address2 = new Address(null, null, null, null, "New Zealand", null);
        Business business2 = new Business("Second Business", null, address2, "Retail Trade", 1);
        addressRepository.save(address2);
        businessRepository.save(business2);
        business2Id = business2.getId();

        Product product3 = new Product("TEST-3", "Third Product", null, null, 5.00, business2.getId());
        productRepository.save(product3);
        InventoryItem inventoryItem3 = new InventoryItem(product3, 5, null, null, "2021-01-01", null, null, "2021-12-31");
        inventoryItemRepository.save(inventoryItem3);
        SaleListing saleListing3 = new SaleListing(business2.getId(), inventoryItem3, 10.00, null, LocalDateTime.parse("2021-11-25T00:00:00"), 5);
        saleListingRepository.save(saleListing3);

        Product product4 = new Product("TEST-4", "Fourth Product", null, null, 5.00, business2.getId());
        productRepository.save(product4);
        InventoryItem inventoryItem4 = new InventoryItem(product4, 5, null, null, "2021-01-01", null, null, "2021-12-31");
        inventoryItemRepository.save(inventoryItem4);
        SaleListing saleListing4 = new SaleListing(business2.getId(), inventoryItem4, 30.00, null, LocalDateTime.parse("2021-12-25T00:00:00"), 5);
        saleListingRepository.save(saleListing4);
    }

    /**
     * Tests that searching for listing by business name with string 'first' returns first listing
     */
    @Test
    void searchByBusinessName_firstBusiness_returnsFirstListing() {
        String searchTerm = "first";
        Specification<SaleListing> spec = saleListingService.searchByBusinessName(new String[]{searchTerm});
        List<SaleListing> listings = saleListingRepository.findAll(spec);
        Assertions.assertEquals(2, listings.size());
        Assertions.assertEquals("First Product", listings.get(0).getInventoryItem().getProduct().getName());
        Assertions.assertEquals("Second Product", listings.get(1).getInventoryItem().getProduct().getName());
    }

    /**
     * Tests that searching for listing by business name with string 'second' returns second listing
     */
    @Test
    void searchByBusinessName_secondBusiness_returnsSecondListing() {
        String searchTerm = "second";
        Specification<SaleListing> spec = saleListingService.searchByBusinessName(new String[]{searchTerm});
        List<SaleListing> listings = saleListingRepository.findAll(spec);
        Assertions.assertEquals(2, listings.size());
        Assertions.assertEquals("Third Product", listings.get(0).getInventoryItem().getProduct().getName());
        Assertions.assertEquals("Fourth Product", listings.get(1).getInventoryItem().getProduct().getName());
    }

    /**
     * Tests that searching for listing by business name with string 'business' returns all four listings
     */
    @Test
    void searchByBusinessName_business_returnsBothListings() {
        String searchTerm = "business";
        Specification<SaleListing> spec = saleListingService.searchByBusinessName(new String[]{searchTerm});
        List<SaleListing> listings = saleListingRepository.findAll(spec);
        Assertions.assertEquals(4, listings.size());
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
        Assertions.assertEquals(2, listings.size());
        Assertions.assertEquals("First Product", listings.get(0).getInventoryItem().getProduct().getName());
        Assertions.assertEquals("Second Product", listings.get(1).getInventoryItem().getProduct().getName());
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
     * Tests that searching for listing by business name with string 'first' and 'second' returns all four listings
     */
    @Test
    void searchByBusinessName_firstOrSecond_returnsBothListings() {
        Specification<SaleListing> spec = saleListingService.searchByBusinessName(new String[]{"first", "second"});
        List<SaleListing> listings = saleListingRepository.findAll(spec);
        System.out.println(listings);
        Assertions.assertEquals(4, listings.size());
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

        List<Object> response = saleListingService.searchSaleListings(dto);
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

        List<Object> response = saleListingService.searchSaleListings(dto);
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

        List<Object> response = saleListingService.searchSaleListings(dto);
        System.out.println(response);
        List<GetSalesListingDTO> listings = (List<GetSalesListingDTO>) response.get(0);
        long total = (long) response.get(1);

        Assertions.assertEquals(1, total);

        GetSalesListingDTO listing = listings.get(0);

        Assertions.assertEquals("First Product", listing.getInventoryItem().getProduct().getName());
    }

    /**
     * Test that searching by "first" returns the sale listings from the first business (two of them)
     */
    @Test
    void search_first_in_businesses_returns_two_listings() {
        SearchSaleListingsDTO dto = new SearchSaleListingsDTO(
                "first",
                false,
                true,
                false,
                null,
                null,
                null,
                null,
                "",
                0
        );

        List<Object> response = saleListingService.searchSaleListings(dto);
        System.out.println(response);
        List<GetSalesListingDTO> listings = (List<GetSalesListingDTO>) response.get(0);
        long total = (long) response.get(1);

        Assertions.assertEquals(2, total);

        Assertions.assertEquals("First Product", listings.get(0).getInventoryItem().getProduct().getName());
        Assertions.assertEquals("Second Product", listings.get(1).getInventoryItem().getProduct().getName());
    }

    /**
     * Test that searching by "second" in business and product name returns 3 sale listings.
     * the sale listings from the second business (two of them) and the sale listing with the title "Second Product"
     */
    @Test
    void search_second_in_businesses_and_product_returns_three_listings() {
        SearchSaleListingsDTO dto = new SearchSaleListingsDTO(
                "second",
                true,
                true,
                false,
                null,
                null,
                null,
                null,
                "",
                0
        );

        List<Object> response = saleListingService.searchSaleListings(dto);
        System.out.println(response);
        List<GetSalesListingDTO> listings = (List<GetSalesListingDTO>) response.get(0);
        long total = (long) response.get(1);

        Assertions.assertEquals(3, total);
    }

    /**
     * Test that searching by "Nether" in business country returns two listings from the first business
     */
    @Test
    void search_nether_in_businesses_country_returns_two_listings() {
        SearchSaleListingsDTO dto = new SearchSaleListingsDTO(
                "Nether",
                false,
                false,
                true,
                null,
                null,
                null,
                null,
                "",
                0
        );

        List<Object> response = saleListingService.searchSaleListings(dto);
        System.out.println(response);
        List<GetSalesListingDTO> listings = (List<GetSalesListingDTO>) response.get(0);
        long total = (long) response.get(1);

        Assertions.assertEquals(2, total);

        Assertions.assertEquals(business1Id, listings.get(0).getBusinessId());
        Assertions.assertEquals(business1Id, listings.get(1).getBusinessId());
    }

    /**
     * Test that searching by price in between 15.00 and 40.00 returns three listings
     */
    @Test
    void search_price_between_14_40_returns_three_listings() {
        SearchSaleListingsDTO dto = new SearchSaleListingsDTO(
                "",
                false,
                false,
                false,
                15.00,
                40.00,
                null,
                null,
                "",
                0
        );

        List<Object> response = saleListingService.searchSaleListings(dto);
        System.out.println(response);
        List<GetSalesListingDTO> listings = (List<GetSalesListingDTO>) response.get(0);
        long total = (long) response.get(1);

        Assertions.assertEquals(3, total);

        GetSalesListingDTO listing1 = listings.get(0);
        Assertions.assertTrue(listing1.getPrice() >= 15 && listing1.getPrice() <= 40);

        GetSalesListingDTO listing2 = listings.get(0);
        Assertions.assertTrue(listing2.getPrice() >= 15 && listing2.getPrice() <= 40);

        GetSalesListingDTO listing3 = listings.get(0);
        Assertions.assertTrue(listing3.getPrice() >= 15 && listing3.getPrice() <= 40);
    }

    /**
     * Test that searching by price in less than or equal to 20 returns three listings
     */
    @Test
    void search_price_less_than_20_returns_three_listings() {
        SearchSaleListingsDTO dto = new SearchSaleListingsDTO(
                "",
                false,
                false,
                false,
                null,
                20.00,
                null,
                null,
                "",
                0
        );

        List<Object> response = saleListingService.searchSaleListings(dto);
        System.out.println(response);
        List<GetSalesListingDTO> listings = (List<GetSalesListingDTO>) response.get(0);
        long total = (long) response.get(1);

        Assertions.assertEquals(3, total);

        GetSalesListingDTO listing1 = listings.get(0);
        Assertions.assertTrue(listing1.getPrice() <= 20);

        GetSalesListingDTO listing2 = listings.get(0);
        Assertions.assertTrue(listing2.getPrice() <= 20);

        GetSalesListingDTO listing3 = listings.get(0);
        Assertions.assertTrue(listing3.getPrice() <= 20);
    }

    /**
     * Test that searching by closing date between 2021-10-20 and 2021-12-5 returns two listings
     */
    @Test
    void search_date_between_returns_two_listings() {
        SearchSaleListingsDTO dto = new SearchSaleListingsDTO(
                "",
                false,
                false,
                false,
                null,
                null,
                "2021-10-20",
                "2021-12-5",
                "",
                0
        );

        List<Object> response = saleListingService.searchSaleListings(dto);
        System.out.println(response);
        List<GetSalesListingDTO> listings = (List<GetSalesListingDTO>) response.get(0);
        long total = (long) response.get(1);

        Assertions.assertEquals(2, total);

        Assertions.assertEquals("Second Product", listings.get(0).getInventoryItem().getProduct().getName());
        Assertions.assertEquals("Third Product", listings.get(1).getInventoryItem().getProduct().getName());
    }

    /**
     * Test that ordering listings by lowest price returns a ordered list
     */
    @Test
    void search_order_lowest_price() {
        SearchSaleListingsDTO dto = new SearchSaleListingsDTO(
                "",
                false,
                false,
                false,
                null,
                null,
                null,
                null,
                "priceAsc",
                0
        );

        List<Object> response = saleListingService.searchSaleListings(dto);
        System.out.println(response);
        List<GetSalesListingDTO> listings = (List<GetSalesListingDTO>) response.get(0);
        long total = (long) response.get(1);

        Assertions.assertEquals(4, total);

        Assertions.assertEquals(10.00, listings.get(0).getPrice());
        Assertions.assertEquals(15.00, listings.get(1).getPrice());
        Assertions.assertEquals(20.00, listings.get(2).getPrice());
        Assertions.assertEquals(30.00, listings.get(3).getPrice());
    }

    /**
     * Test that ordering listings by highest price returns a ordered list
     */
    @Test
    void search_order_highest_price() {
        SearchSaleListingsDTO dto = new SearchSaleListingsDTO(
                "",
                false,
                false,
                false,
                null,
                null,
                null,
                null,
                "priceDesc",
                0
        );

        List<Object> response = saleListingService.searchSaleListings(dto);
        System.out.println(response);
        List<GetSalesListingDTO> listings = (List<GetSalesListingDTO>) response.get(0);
        long total = (long) response.get(1);

        Assertions.assertEquals(4, total);

        Assertions.assertEquals(30.00, listings.get(0).getPrice());
        Assertions.assertEquals(20.00, listings.get(1).getPrice());
        Assertions.assertEquals(15.00, listings.get(2).getPrice());
        Assertions.assertEquals(10.00, listings.get(3).getPrice());
    }

    /**
     * Test that ordering listings by product name returns a ordered list
     */
    @Test
    void search_order_product_name() {
        SearchSaleListingsDTO dto = new SearchSaleListingsDTO(
                "",
                false,
                false,
                false,
                null,
                null,
                null,
                null,
                "productName",
                0
        );

        List<Object> response = saleListingService.searchSaleListings(dto);
        System.out.println(response);
        List<GetSalesListingDTO> listings = (List<GetSalesListingDTO>) response.get(0);
        long total = (long) response.get(1);

        Assertions.assertEquals(4, total);

        Assertions.assertEquals("First Product", listings.get(0).getInventoryItem().getProduct().getName());
        Assertions.assertEquals("Fourth Product", listings.get(1).getInventoryItem().getProduct().getName());
        Assertions.assertEquals("Second Product", listings.get(2).getInventoryItem().getProduct().getName());
        Assertions.assertEquals("Third Product", listings.get(3).getInventoryItem().getProduct().getName());
    }

    /**
     * Test that ordering listings by expiry date soonest returns a ordered list
     */
    @Test
    void search_order_closes_soonest() {
        SearchSaleListingsDTO dto = new SearchSaleListingsDTO(
                "",
                false,
                false,
                false,
                null,
                null,
                null,
                null,
                "expiryDateAsc",
                0
        );

        List<Object> response = saleListingService.searchSaleListings(dto);
        System.out.println(response);
        List<GetSalesListingDTO> listings = (List<GetSalesListingDTO>) response.get(0);
        long total = (long) response.get(1);

        Assertions.assertEquals(4, total);

        Assertions.assertEquals("First Product", listings.get(0).getInventoryItem().getProduct().getName());
        Assertions.assertEquals("Second Product", listings.get(1).getInventoryItem().getProduct().getName());
        Assertions.assertEquals("Third Product", listings.get(2).getInventoryItem().getProduct().getName());
        Assertions.assertEquals("Fourth Product", listings.get(3).getInventoryItem().getProduct().getName());
    }

    /**
     * Test that ordering listings by expiry date latest returns a ordered list
     */
    @Test
    void search_order_closes_latest() {
        SearchSaleListingsDTO dto = new SearchSaleListingsDTO(
                "",
                false,
                false,
                false,
                null,
                null,
                null,
                null,
                "expiryDateDesc",
                0
        );

        List<Object> response = saleListingService.searchSaleListings(dto);
        System.out.println(response);
        List<GetSalesListingDTO> listings = (List<GetSalesListingDTO>) response.get(0);
        long total = (long) response.get(1);

        Assertions.assertEquals(4, total);

        Assertions.assertEquals("Fourth Product", listings.get(0).getInventoryItem().getProduct().getName());
        Assertions.assertEquals("Third Product", listings.get(1).getInventoryItem().getProduct().getName());
        Assertions.assertEquals("Second Product", listings.get(2).getInventoryItem().getProduct().getName());
        Assertions.assertEquals("First Product", listings.get(3).getInventoryItem().getProduct().getName());
    }
}
