package org.seng302.project.service_layer.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.*;
import org.seng302.project.repository_layer.repository.*;
import org.seng302.project.service_layer.dto.sale_listings.GetSaleListingDTO;
import org.seng302.project.service_layer.dto.sale_listings.SearchSaleListingsDTO;
import org.seng302.project.service_layer.exceptions.BadRequestException;
import org.seng302.project.service_layer.exceptions.NotAcceptableException;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
class SaleListingServiceTest extends AbstractInitializer {

    private final BusinessRepository businessRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final SaleListingRepository saleListingRepository;
    private final LikedSaleListingRepository likedSaleListingRepository;
    private final SaleHistoryRepository saleHistoryRepository;
    private final UserRepository userRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final SaleListingService saleListingService;

    User testUser;

    Integer business1Id;
    Integer business2Id;

    SaleListing saleListing1;
    SaleListing saleListing2;

    @Autowired
    SaleListingServiceTest(BusinessRepository businessRepository,
                           AddressRepository addressRepository,
                           ProductRepository productRepository,
                           InventoryItemRepository inventoryItemRepository,
                           SaleListingRepository saleListingRepository,
                           LikedSaleListingRepository likedSaleListingRepository,
                           SaleHistoryRepository saleHistoryRepository,
                           UserRepository userRepository,
                           UserNotificationRepository userNotificationRepository) {
        this.businessRepository = businessRepository;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.saleListingRepository = saleListingRepository;
        this.likedSaleListingRepository = likedSaleListingRepository;
        this.saleHistoryRepository = saleHistoryRepository;
        this.userRepository = userRepository;
        this.userNotificationRepository = userNotificationRepository;
        this.saleListingService = new SaleListingService(
                this.saleListingRepository,
                this.likedSaleListingRepository,
                this.saleHistoryRepository,
                this.inventoryItemRepository,
                this.userRepository,
                this.userNotificationRepository);
    }

    /**
     * Before each test, setup four sale listings with different parameters
     */
    @BeforeEach
    void setup() {
        this.testUser = this.getTestUser();
        addressRepository.save(testUser.getHomeAddress());
        userRepository.save(testUser);

        Address address1 = new Address(null, null, "Rangiora", null, "Netherlands", null);
        Business business1 = new Business("First Business", null, address1, "Retail Trade", 1);
        addressRepository.save(address1);
        businessRepository.save(business1);
        business1Id = business1.getId();

        Product product1 = new Product("TEST-1", "First Product", null, null, 5.00, business1.getId());
        productRepository.save(product1);
        InventoryItem inventoryItem1 = new InventoryItem(product1, 5, null, null, "2021-01-01", null, null, "2021-12-01");
        inventoryItemRepository.save(inventoryItem1);
        saleListing1 = new SaleListing(business1, inventoryItem1, 10.00, null, LocalDateTime.parse("2021-08-25T00:00:00"), 5);
        saleListingRepository.save(saleListing1);

        Product product2 = new Product("TEST-2", "Second Product", null, null, 5.00, business1.getId());
        productRepository.save(product2);
        InventoryItem inventoryItem2 = new InventoryItem(product2, 10, null, null, "2021-01-01", null, null, "2021-12-02");
        inventoryItemRepository.save(inventoryItem1);
        saleListing2 = new SaleListing(business1, inventoryItem2, 15.00, null, LocalDateTime.parse("2021-10-25T00:00:00"), 10);
        saleListingRepository.save(saleListing2);

        Address address2 = new Address(null, null, "Christchurch", null, "New Zealand", null);
        Business business2 = new Business("Second Business", null, address2, "Charitable Organisation", 1);
        addressRepository.save(address2);
        businessRepository.save(business2);
        business2Id = business2.getId();

        Product product3 = new Product("TEST-3", "Third Product", null, null, 5.00, business2.getId());
        productRepository.save(product3);
        InventoryItem inventoryItem3 = new InventoryItem(product3, 5, null, null, "2021-01-01", null, null, "2021-12-03");
        inventoryItemRepository.save(inventoryItem3);
        SaleListing saleListing3 = new SaleListing(business2, inventoryItem3, 20.00, null, LocalDateTime.parse("2021-11-25T00:00:00"), 5);
        saleListingRepository.save(saleListing3);

        Product product4 = new Product("TEST-4", "Fourth Product", null, null, 5.00, business2.getId());
        productRepository.save(product4);
        InventoryItem inventoryItem4 = new InventoryItem(product4, 5, null, null, "2021-01-01", null, null, "2021-12-04");
        inventoryItemRepository.save(inventoryItem4);
        SaleListing saleListing4 = new SaleListing(business2, inventoryItem4, 30.00, null, LocalDateTime.parse("2021-12-25T00:00:00"), 5);
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
     * Tests that searching for listing by business type with exact string 'trade' returns two listings from the first business
     */
    @Test
    void searchByBusinessType_trade_returnsTwoListings() {
        String searchTerm = "trade";
        Specification<SaleListing> spec = saleListingService.searchByBusinessType(new String[]{searchTerm});
        List<SaleListing> listings = saleListingRepository.findAll(spec);
        Assertions.assertEquals(2, listings.size());
        Assertions.assertEquals(business1Id, listings.get(0).getBusiness().getId());
        Assertions.assertEquals(business1Id, listings.get(1).getBusiness().getId());
    }

    /**
     * Tests that searching for listing by business type with string ""Charitable Organisation"" returns two listings from the second business
     */
    @Test
    void searchByBusinessType_exact_returnsTwoListings() {
        String searchTerm = "\"charitable organisation\"";
        Specification<SaleListing> spec = saleListingService.searchByBusinessType(new String[]{searchTerm});
        List<SaleListing> listings = saleListingRepository.findAll(spec);
        Assertions.assertEquals(2, listings.size());
        Assertions.assertEquals(business2Id, listings.get(0).getBusiness().getId());
        Assertions.assertEquals(business2Id, listings.get(1).getBusiness().getId());
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
        List<GetSaleListingDTO> listings = (List<GetSaleListingDTO>) response.get(0);
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
        List<GetSaleListingDTO> listings = (List<GetSaleListingDTO>) response.get(0);
        long total = (long) response.get(1);

        Assertions.assertEquals(1, total);

        GetSaleListingDTO listing = listings.get(0);

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
        List<GetSaleListingDTO> listings = (List<GetSaleListingDTO>) response.get(0);
        long total = (long) response.get(1);

        Assertions.assertEquals(1, total);

        GetSaleListingDTO listing = listings.get(0);

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
        List<GetSaleListingDTO> listings = (List<GetSaleListingDTO>) response.get(0);
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
        List<GetSaleListingDTO> listings = (List<GetSaleListingDTO>) response.get(0);
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
        List<GetSaleListingDTO> listings = (List<GetSaleListingDTO>) response.get(0);
        long total = (long) response.get(1);

        Assertions.assertEquals(2, total);

        Assertions.assertEquals(business1Id, listings.get(0).getBusiness().getId());
        Assertions.assertEquals(business1Id, listings.get(1).getBusiness().getId());
    }

    /**
     * Test that searching by "retail" in business type returns two listings from the first business
     */
    @Test
    void search_retail_in_businesses_type_returns_two_listings() {
        SearchSaleListingsDTO dto = new SearchSaleListingsDTO(
                "retail",
                false,
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
        List<GetSaleListingDTO> listings = (List<GetSaleListingDTO>) response.get(0);
        long total = (long) response.get(1);

        Assertions.assertEquals(2, total);

        Assertions.assertEquals(business1Id, listings.get(0).getBusiness().getId());
        Assertions.assertEquals(business1Id, listings.get(1).getBusiness().getId());
    }

    /**
     * Test that searching by exact string ""Charitable Organisation"" in business type returns two listings from the second business
     */
    @Test
    void search_exact_in_businesses_type_returns_two_listings() {
        SearchSaleListingsDTO dto = new SearchSaleListingsDTO(
                "\"Charitable Organisation\"",
                false,
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
        List<GetSaleListingDTO> listings = (List<GetSaleListingDTO>) response.get(0);
        long total = (long) response.get(1);

        Assertions.assertEquals(2, total);

        Assertions.assertEquals(business2Id, listings.get(0).getBusiness().getId());
        Assertions.assertEquals(business2Id, listings.get(1).getBusiness().getId());
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
        List<GetSaleListingDTO> listings = (List<GetSaleListingDTO>) response.get(0);
        long total = (long) response.get(1);

        Assertions.assertEquals(3, total);

        GetSaleListingDTO listing1 = listings.get(0);
        Assertions.assertTrue(listing1.getPrice() >= 15 && listing1.getPrice() <= 40);

        GetSaleListingDTO listing2 = listings.get(0);
        Assertions.assertTrue(listing2.getPrice() >= 15 && listing2.getPrice() <= 40);

        GetSaleListingDTO listing3 = listings.get(0);
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
        List<GetSaleListingDTO> listings = (List<GetSaleListingDTO>) response.get(0);
        long total = (long) response.get(1);

        Assertions.assertEquals(3, total);

        GetSaleListingDTO listing1 = listings.get(0);
        Assertions.assertTrue(listing1.getPrice() <= 20);

        GetSaleListingDTO listing2 = listings.get(0);
        Assertions.assertTrue(listing2.getPrice() <= 20);

        GetSaleListingDTO listing3 = listings.get(0);
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
        List<GetSaleListingDTO> listings = (List<GetSaleListingDTO>) response.get(0);
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
        List<GetSaleListingDTO> listings = (List<GetSaleListingDTO>) response.get(0);
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
        List<GetSaleListingDTO> listings = (List<GetSaleListingDTO>) response.get(0);
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
        List<GetSaleListingDTO> listings = (List<GetSaleListingDTO>) response.get(0);
        long total = (long) response.get(1);

        Assertions.assertEquals(4, total);

        Assertions.assertEquals("First Product", listings.get(0).getInventoryItem().getProduct().getName());
        Assertions.assertEquals("Fourth Product", listings.get(1).getInventoryItem().getProduct().getName());
        Assertions.assertEquals("Second Product", listings.get(2).getInventoryItem().getProduct().getName());
        Assertions.assertEquals("Third Product", listings.get(3).getInventoryItem().getProduct().getName());
    }

    /**
     * Test that ordering listings by business country returns a ordered list
     */
    @Test
    void search_order_business_country() {
        SearchSaleListingsDTO dto = new SearchSaleListingsDTO(
                "",
                false,
                false,
                false,
                false,
                null,
                null,
                null,
                null,
                "country",
                0
        );

        List<Object> response = saleListingService.searchSaleListings(dto);
        System.out.println(response);
        List<GetSaleListingDTO> listings = (List<GetSaleListingDTO>) response.get(0);
        long total = (long) response.get(1);

        Assertions.assertEquals(4, total);

        Assertions.assertEquals(business1Id, listings.get(0).getBusiness().getId());
        Assertions.assertEquals(business1Id, listings.get(1).getBusiness().getId());
        Assertions.assertEquals(business2Id, listings.get(2).getBusiness().getId());
        Assertions.assertEquals(business2Id, listings.get(3).getBusiness().getId());
    }

    /**
     * Test that ordering listings by business city returns a ordered list
     */
    @Test
    void search_order_business_city() {
        SearchSaleListingsDTO dto = new SearchSaleListingsDTO(
                "",
                false,
                false,
                false,
                false,
                null,
                null,
                null,
                null,
                "city",
                0
        );

        List<Object> response = saleListingService.searchSaleListings(dto);
        System.out.println(response);
        List<GetSaleListingDTO> listings = (List<GetSaleListingDTO>) response.get(0);
        long total = (long) response.get(1);

        Assertions.assertEquals(4, total);

        Assertions.assertEquals(business2Id, listings.get(0).getBusiness().getId());
        Assertions.assertEquals(business2Id, listings.get(1).getBusiness().getId());
        Assertions.assertEquals(business1Id, listings.get(2).getBusiness().getId());
        Assertions.assertEquals(business1Id, listings.get(3).getBusiness().getId());
    }

    /**
     * Test that ordering listings by business name returns a ordered list
     */
    @Test
    void search_order_business_name() {
        SearchSaleListingsDTO dto = new SearchSaleListingsDTO(
                "",
                false,
                false,
                false,
                false,
                null,
                null,
                null,
                null,
                "seller",
                0
        );

        List<Object> response = saleListingService.searchSaleListings(dto);
        System.out.println(response);
        List<GetSaleListingDTO> listings = (List<GetSaleListingDTO>) response.get(0);
        long total = (long) response.get(1);

        Assertions.assertEquals(4, total);

        Assertions.assertEquals(business1Id, listings.get(0).getBusiness().getId());
        Assertions.assertEquals(business1Id, listings.get(1).getBusiness().getId());
        Assertions.assertEquals(business2Id, listings.get(2).getBusiness().getId());
        Assertions.assertEquals(business2Id, listings.get(3).getBusiness().getId());
    }

    /**
     * Test that ordering listings by expiry date soonest returns a ordered list
     */
    @Test
    void search_order_expiry_soonest() {
        SearchSaleListingsDTO dto = new SearchSaleListingsDTO(
                "",
                false,
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
        List<GetSaleListingDTO> listings = (List<GetSaleListingDTO>) response.get(0);
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
    void search_order_expiry_latest() {
        SearchSaleListingsDTO dto = new SearchSaleListingsDTO(
                "",
                false,
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
        List<GetSaleListingDTO> listings = (List<GetSaleListingDTO>) response.get(0);
        long total = (long) response.get(1);

        Assertions.assertEquals(4, total);

        Assertions.assertEquals("Fourth Product", listings.get(0).getInventoryItem().getProduct().getName());
        Assertions.assertEquals("Third Product", listings.get(1).getInventoryItem().getProduct().getName());
        Assertions.assertEquals("Second Product", listings.get(2).getInventoryItem().getProduct().getName());
        Assertions.assertEquals("First Product", listings.get(3).getInventoryItem().getProduct().getName());
    }

    /**
     * Test that trying to unlike a sale listing that doesn't exist throws a NotAcceptableException
     */
    @Test
    void unlikeSaleListing_invalidListingId_throwsException() {
        AppUserDetails user = new AppUserDetails(this.testUser);

        Assertions.assertThrows(NotAcceptableException.class,
                () -> saleListingService.unlikeSaleListing(1000, user));
    }

    /**
     * Test that trying to unlike a sale listing that isn't liked by the user throws a BadRequestException
     */
    @Test
    void unlikeSaleListing_listingNotLiked_throwsException() {
        LikedSaleListing listing = new LikedSaleListing(this.testUser, this.saleListing1);
        likedSaleListingRepository.save(listing);

        AppUserDetails user = new AppUserDetails(this.testUser);
        Assertions.assertThrows(BadRequestException.class,
                () -> saleListingService.unlikeSaleListing(this.saleListing2.getId(), user));
    }
}
