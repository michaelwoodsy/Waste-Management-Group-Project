package org.seng302.project.service_layer.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.*;
import org.seng302.project.repository_layer.repository.*;
import org.seng302.project.service_layer.dto.sale_listings.GetSaleListingDTO;
import org.seng302.project.service_layer.dto.sale_listings.PostSaleListingDTO;
import org.seng302.project.service_layer.dto.sale_listings.SearchSaleListingsDTO;
import org.seng302.project.service_layer.exceptions.BadRequestException;
import org.seng302.project.service_layer.exceptions.ForbiddenException;
import org.seng302.project.service_layer.exceptions.InvalidDateException;
import org.seng302.project.service_layer.exceptions.NotAcceptableException;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


import static org.mockito.ArgumentMatchers.any;



@DataJpaTest
class SaleListingServiceTest extends AbstractInitializer {

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final SaleListingRepository saleListingRepository;
    private final LikedSaleListingRepository likedSaleListingRepository;
    private final UserService userService;
    private final BusinessService businessService;
    private final SaleHistoryRepository saleHistoryRepository;
    private final UserNotificationRepository userNotificationRepository;

    private final SaleListingService saleListingService;


    Business business1;
    Business business2;
    Integer business2Id;
    User testUser;
    User testOtherUser;
    User testAdmin;
    InventoryItem inventoryItem;
    SaleListing saleListing1;
    SaleListing saleListing2;
    SaleListing saleListing3;
    SaleListing saleListing4;
    SaleListing saleListing5;
    SaleListing saleListing6;
    SaleListing saleListing7;
    SaleListing saleListing8;

    @Autowired
    SaleListingServiceTest(UserRepository userRepository,
                           BusinessRepository businessRepository,
                           AddressRepository addressRepository,
                           ProductRepository productRepository,
                           InventoryItemRepository inventoryItemRepository,
                           SaleListingRepository saleListingRepository,
                           LikedSaleListingRepository likedSaleListingRepository,
                           SaleHistoryRepository saleHistoryRepository,
                           UserNotificationRepository userNotificationRepository) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.saleListingRepository = saleListingRepository;
        this.likedSaleListingRepository = likedSaleListingRepository;
        this.saleHistoryRepository = saleHistoryRepository;
        this.userNotificationRepository = userNotificationRepository;

        this.userService = Mockito.mock(UserService.class);
        this.businessService = Mockito.mock(BusinessService.class);

        this.saleListingService = new SaleListingService(
                userService,
                businessService,
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
        testUser = this.getTestUser();
        addressRepository.save(testUser.getHomeAddress());
        testUser.setId(null);
        testUser = userRepository.save(testUser);

        this.testOtherUser = this.getTestOtherUser();
        addressRepository.save(testOtherUser.getHomeAddress());
        testOtherUser.setId(null);
        userRepository.save(testOtherUser);

        testAdmin = this.getTestUserBusinessAdmin();
        addressRepository.save(testAdmin.getHomeAddress());
        testAdmin.setId(null);
        testAdmin = userRepository.save(testAdmin);

        Address address1 = new Address(null, null, "Rangiora", null, "Netherlands", null);
        business1 = new Business("First Business", null, address1, "Retail Trade", testAdmin.getId());
        addressRepository.save(address1);
        business1 = businessRepository.save(business1);

        Product product1 = new Product("TEST-1", "First Product", null, null, 5.00, business1.getId());
        productRepository.save(product1);
        inventoryItem = new InventoryItem(product1, 10, null, null, "2021-01-01", null, null, "2021-12-23");
        inventoryItem = inventoryItemRepository.save(inventoryItem);
        saleListing1 = new SaleListing(business1, inventoryItem, 10.00, null, LocalDateTime.parse("2021-12-22T00:00:00"), 5);
        saleListing1.setFeatured(true);
        saleListing1 = saleListingRepository.save(saleListing1);

        Product product2 = new Product("TEST-2", "Second Product", null, null, 5.00, business1.getId());
        productRepository.save(product2);
        InventoryItem inventoryItem2 = new InventoryItem(product2, 10, null, null, "2021-01-02", null, null, "2021-12-24");
        inventoryItem2 = inventoryItemRepository.save(inventoryItem2);
        saleListing2 = new SaleListing(business1, inventoryItem2, 15.00, null, LocalDateTime.parse("2021-12-23T00:00:00"), 10);
        saleListing2 = saleListingRepository.save(saleListing2);

        Address address2 = new Address(null, null, "Christchurch", null, "New Zealand", null);
        business2 = new Business("Second Business", null, address2, "Charitable Organisation", 1);
        addressRepository.save(address2);
        business2 = businessRepository.save(business2);
        business2Id = business2.getId();

        Product product3 = new Product("TEST-3", "Third Product", null, null, 5.00, business2.getId());
        productRepository.save(product3);
        InventoryItem inventoryItem3 = new InventoryItem(product3, 5, null, null, "2021-01-03", null, null, "2021-12-25");
        inventoryItem3 = inventoryItemRepository.save(inventoryItem3);
        saleListing3 = new SaleListing(business2, inventoryItem3, 20.00, null, LocalDateTime.parse("2021-12-24T00:00:00"), 5);
        saleListingRepository.save(saleListing3);

        Product product4 = new Product("TEST-4", "Fourth Product", null, null, 5.00, business2.getId());
        productRepository.save(product4);
        InventoryItem inventoryItem4 = new InventoryItem(product4, 5, null, null, "2021-01-04", null, null, "2021-12-26");
        inventoryItem4 = inventoryItemRepository.save(inventoryItem4);
        saleListing4 = new SaleListing(business2, inventoryItem4, 30.00, null, LocalDateTime.parse("2021-12-25T00:00:00"), 5);
        saleListingRepository.save(saleListing4);

        Product product5 = new Product("TEST-5", "Fifth Product", null, null, 5.00, business1.getId());
        productRepository.save(product5);
        InventoryItem inventoryItem5 = new InventoryItem(product5, 5, null, null, "2021-01-05", null, null, "2021-12-27");
        inventoryItem5 = inventoryItemRepository.save(inventoryItem5);
        saleListing5 = new SaleListing(business1, inventoryItem5, 30.00, null, LocalDateTime.parse("2021-12-26T00:00:00"), 5);
        saleListingRepository.save(saleListing5);

        Product product6 = new Product("TEST-6", "Sixth Product", null, null, 5.00, business1.getId());
        productRepository.save(product6);
        InventoryItem inventoryItem6 = new InventoryItem(product6, 5, null, null, "2021-01-06", null, null, "2021-12-28");
        inventoryItem6 = inventoryItemRepository.save(inventoryItem6);
        saleListing6 = new SaleListing(business1, inventoryItem6, 30.00, null, LocalDateTime.parse("2021-12-27T00:00:00"), 5);
        saleListingRepository.save(saleListing6);

        Product product7 = new Product("TEST-7", "Seventh Product", null, null, 5.00, business1.getId());
        productRepository.save(product7);
        InventoryItem inventoryItem7 = new InventoryItem(product7, 5, null, null, "2021-01-07", null, null, "2021-12-29");
        inventoryItem7 = inventoryItemRepository.save(inventoryItem7);
        saleListing7 = new SaleListing(business1, inventoryItem7, 30.00, null, LocalDateTime.parse("2021-12-28T00:00:00"), 5);
        saleListingRepository.save(saleListing7);

        Product product8 = new Product("TEST-8", "Eighth Product", null, null, 5.00, business1.getId());
        productRepository.save(product8);
        InventoryItem inventoryItem8 = new InventoryItem(product8, 5, null, null, "2021-01-08", null, null, "2021-12-30");
        inventoryItem8 = inventoryItemRepository.save(inventoryItem8);
        saleListing8 = new SaleListing(business1, inventoryItem8, 30.00, null, LocalDateTime.parse("2021-12-29T00:00:00"), 5);
        saleListingRepository.save(saleListing8);

        Mockito.when(userService.getUserByEmail(testUser.getEmail()))
                .thenReturn(userRepository.findByEmail(testUser.getEmail()).get(0));
        Mockito.when(userService.getUserByEmail(testAdmin.getEmail()))
                .thenReturn(userRepository.findByEmail(testAdmin.getEmail()).get(0));
    }

    /**
     * Tests that a NotAcceptableException is thrown when
     * someone tries accessing the listings of a nonexistent business.
     */
    @Test
    void getBusinessListings_nonExistentBusiness_NotAcceptableException() {
        Mockito.doThrow(new NotAcceptableException("message"))
                .when(businessService).checkBusiness(any(Integer.class));

        AppUserDetails appUser = new AppUserDetails(testUser);

        Assertions.assertThrows(NotAcceptableException.class,
                () -> saleListingService.getBusinessListings(99, appUser));

    }

    /**
     * Tests the successful case of getting the listings for a business
     */
    @Test
    void getBusinessListings_success() {
        AppUserDetails appUser = new AppUserDetails(testUser);
        List<GetSaleListingDTO> listings = saleListingService.getBusinessListings(business1.getId(), appUser);

        //We expect to get the 2 listings added in the setup() method
        Assertions.assertEquals(6, listings.size());
    }

    /**
     * Tests that a NotAcceptableException is thrown when
     * someone tries adding a listing to a nonexistent business.
     */
    @Test
    void postBusinessListings_nonExistentBusiness_NotAcceptableException() {
        Mockito.doThrow(new NotAcceptableException(""))
                .when(businessService).checkBusiness(any(Integer.class));

        AppUserDetails appUser = new AppUserDetails(testUser);

        PostSaleListingDTO requestDTO = new PostSaleListingDTO(
                inventoryItem.getId(), 2.20, "Hmmm", "2022-02-29T04:34:55.931Z", 5);

        Assertions.assertThrows(NotAcceptableException.class,
                () -> saleListingService.newBusinessListing(requestDTO, 99, appUser));
    }

    /**
     * Tests that a ForbiddenException is thrown when
     * a random user tries adding a listing to a business.
     */
    @Test
    void postBusinessListings_notAdmin_ForbiddenException() {
        Mockito.when(businessService.checkBusiness(business1.getId()))
                .thenReturn(business1);

        Mockito.doThrow(new ForbiddenException(""))
                .when(businessService).checkUserCanDoBusinessAction(any(AppUserDetails.class), any(Business.class));

        AppUserDetails appUser = new AppUserDetails(testUser);

        PostSaleListingDTO requestDTO = new PostSaleListingDTO(
                inventoryItem.getId(), 2.20, "Hmmm", "2022-02-29T04:34:55.931Z", 5);

        Integer businessId = business1.getId();
        Assertions.assertThrows(ForbiddenException.class,
                () -> saleListingService.newBusinessListing(requestDTO, businessId, appUser));
    }

    /**
     * Tests that a BadRequestException is thrown when
     * a business admin tries adding a listing with an invalid quantity
     */
    @Test
    void postBusinessListings_invalidQuantity_BadRequestException() {
        AppUserDetails appUser = new AppUserDetails(testAdmin);

        PostSaleListingDTO requestDTO = new PostSaleListingDTO(
                inventoryItem.getId(), 2.20, "Hmmm", "2022-02-29T04:34:55.931Z", inventoryItem.getQuantity() + 2);

        Integer businessId = business1.getId();
        Assertions.assertThrows(BadRequestException.class,
                () -> saleListingService.newBusinessListing(requestDTO, businessId, appUser));
    }

    /**
     * Tests that a BadRequestException is thrown when
     * a business admin tries adding a listing with an invalid inventory item id
     */
    @Test
    void postBusinessListings_invalidInventoryItem_BadRequestException() {
        AppUserDetails appUser = new AppUserDetails(testAdmin);

        PostSaleListingDTO requestDTO = new PostSaleListingDTO(
                inventoryItem.getId() + 50, 2.20, "Hmmm", "2022-02-29T04:34:55.931Z", inventoryItem.getQuantity());

        Integer businessId = business1.getId();
        Assertions.assertThrows(BadRequestException.class,
                () -> saleListingService.newBusinessListing(requestDTO, businessId, appUser));
    }

    /**
     * Tests that a InvalidDateException is thrown when
     * a business admin tries adding a listing with an invalid closing date
     */
    @Test
    void postBusinessListings_invalidDate_InvalidDateException() {
        AppUserDetails appUser = new AppUserDetails(testAdmin);

        PostSaleListingDTO requestDTO = new PostSaleListingDTO(
                inventoryItem.getId(), 2.20, "Hmmm", "2021/02/26", 1);

        Integer businessId = business1.getId();
        Assertions.assertThrows(InvalidDateException.class,
                () -> saleListingService.newBusinessListing(requestDTO, businessId, appUser));
    }

    /**
     * Tests the successful case of adding a sale listing
     */
    @Test
    void postBusinessListings_success() {
        Mockito.when(businessService.checkBusiness(business1.getId()))
                .thenReturn(business1);

        AppUserDetails appUser = new AppUserDetails(testAdmin);

        PostSaleListingDTO requestDTO = new PostSaleListingDTO(
                inventoryItem.getId(), 2.20, "Hmmm", "2021-11-29T04:34:55.931Z", 1);

        saleListingService.newBusinessListing(requestDTO, business1.getId(), appUser);
        List<SaleListing> listings = saleListingRepository.findAllByBusinessId(business1.getId());

        //The business had 6 listings added to it in the setup() method, and we check 1 more was added to this
        Assertions.assertEquals(7, listings.size());
    }

    /**
     * Tests that searching for listing by business name with different strings returns correct number of listings
     */
    @ParameterizedTest
    @CsvSource({
            "first, 2",
            "second, 2",
            "business, 4",
            "\"first\", 0",
            "\"first business\", 2",
            "\"second\", 0",
            "\"second business\", 2",
            "random, 0",
            "first AND second, 0"
    })
    void searchByBusinessName_business_returnsBothListings(String searchTerm, Integer expectedListings) {
        Specification<SaleListing> spec = saleListingService.searchByBusinessName(new String[]{searchTerm});
        List<SaleListing> listings = saleListingRepository.findAll(spec);
        Assertions.assertEquals(expectedListings, listings.size());
    }

    /**
     * Tests that searching for listing by business name with string 'first' and 'second' returns all four listings
     */
    @Test
    void searchByBusinessName_firstOrSecond_returnsBothListings() {
        Specification<SaleListing> spec = saleListingService.searchByBusinessName(new String[]{"first", "second"});
        List<SaleListing> listings = saleListingRepository.findAll(spec);
        Assertions.assertEquals(8, listings.size());
    }

    /**
     * Tests that searching for listing by business type with exact string 'trade' returns two listings from the first business
     */
    @Test
    void searchByBusinessType_trade_returnsTwoListings() {
        String searchTerm = "trade";
        Specification<SaleListing> spec = saleListingService.searchByBusinessType(new String[]{searchTerm});
        List<SaleListing> listings = saleListingRepository.findAll(spec);
        Assertions.assertEquals(6, listings.size());
        Assertions.assertEquals(business1.getId(), listings.get(0).getBusiness().getId());
        Assertions.assertEquals(business1.getId(), listings.get(1).getBusiness().getId());
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

        List<Object> response = saleListingService.searchSaleListings(dto, new AppUserDetails(testUser));
        List<GetSaleListingDTO> listings = (List<GetSaleListingDTO>) response.get(0);
        long total = (long) response.get(1);

        Assertions.assertEquals(8, total);
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

        List<Object> response = saleListingService.searchSaleListings(dto, new AppUserDetails(testUser));
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

        List<Object> response = saleListingService.searchSaleListings(dto, new AppUserDetails(testUser));
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

        List<Object> response = saleListingService.searchSaleListings(dto, new AppUserDetails(testUser));
        List<GetSaleListingDTO> listings = (List<GetSaleListingDTO>) response.get(0);
        long total = (long) response.get(1);

        Assertions.assertEquals(6, total);

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

        List<Object> response = saleListingService.searchSaleListings(dto, new AppUserDetails(testUser));
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

        List<Object> response = saleListingService.searchSaleListings(dto, new AppUserDetails(testUser));
        List<GetSaleListingDTO> listings = (List<GetSaleListingDTO>) response.get(0);
        long total = (long) response.get(1);

        Assertions.assertEquals(6, total);

        Assertions.assertEquals(business1.getId(), listings.get(0).getBusiness().getId());
        Assertions.assertEquals(business1.getId(), listings.get(1).getBusiness().getId());
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

        List<Object> response = saleListingService.searchSaleListings(dto, new AppUserDetails(testUser));
        List<GetSaleListingDTO> listings = (List<GetSaleListingDTO>) response.get(0);
        long total = (long) response.get(1);

        Assertions.assertEquals(6, total);

        Assertions.assertEquals(business1.getId(), listings.get(0).getBusiness().getId());
        Assertions.assertEquals(business1.getId(), listings.get(1).getBusiness().getId());
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

        List<Object> response = saleListingService.searchSaleListings(dto, new AppUserDetails(testUser));
        List<GetSaleListingDTO> listings = (List<GetSaleListingDTO>) response.get(0);
        long total = (long) response.get(1);

        Assertions.assertEquals(2, total);

        Assertions.assertEquals(business2.getId(), listings.get(0).getBusiness().getId());
        Assertions.assertEquals(business2.getId(), listings.get(1).getBusiness().getId());
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

        List<Object> response = saleListingService.searchSaleListings(dto, new AppUserDetails(testUser));
        List<GetSaleListingDTO> listings = (List<GetSaleListingDTO>) response.get(0);
        long total = (long) response.get(1);

        Assertions.assertEquals(7, total);

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

        List<Object> response = saleListingService.searchSaleListings(dto, new AppUserDetails(testUser));
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
                "2021-12-20",
                "2021-12-23",
                "",
                0
        );

        List<Object> response = saleListingService.searchSaleListings(dto, new AppUserDetails(testUser));
        List<GetSaleListingDTO> listings = (List<GetSaleListingDTO>) response.get(0);
        long total = (long) response.get(1);

        Assertions.assertEquals(2, total);

        Assertions.assertEquals("First Product", listings.get(0).getInventoryItem().getProduct().getName());
        Assertions.assertEquals("Second Product", listings.get(1).getInventoryItem().getProduct().getName());
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

        List<Object> response = saleListingService.searchSaleListings(dto, new AppUserDetails(testUser));
        List<GetSaleListingDTO> listings = (List<GetSaleListingDTO>) response.get(0);
        long total = (long) response.get(1);

        Assertions.assertEquals(8, total);

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

        List<Object> response = saleListingService.searchSaleListings(dto, new AppUserDetails(testUser));
        List<GetSaleListingDTO> listings = (List<GetSaleListingDTO>) response.get(0);
        long total = (long) response.get(1);

        Assertions.assertEquals(8, total);

        Assertions.assertEquals(30.00, listings.get(0).getPrice());
        Assertions.assertEquals(30.00, listings.get(1).getPrice());
        Assertions.assertEquals(30.00, listings.get(2).getPrice());
        Assertions.assertEquals(30.00, listings.get(3).getPrice());
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

        List<Object> response = saleListingService.searchSaleListings(dto, new AppUserDetails(testUser));
        List<GetSaleListingDTO> listings = (List<GetSaleListingDTO>) response.get(0);
        long total = (long) response.get(1);

        Assertions.assertEquals(8, total);

        Assertions.assertEquals("Eighth Product", listings.get(0).getInventoryItem().getProduct().getName());
        Assertions.assertEquals("Fifth Product", listings.get(1).getInventoryItem().getProduct().getName());
        Assertions.assertEquals("First Product", listings.get(2).getInventoryItem().getProduct().getName());
        Assertions.assertEquals("Fourth Product", listings.get(3).getInventoryItem().getProduct().getName());
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

        List<Object> response = saleListingService.searchSaleListings(dto, new AppUserDetails(testUser));
        List<GetSaleListingDTO> listings = (List<GetSaleListingDTO>) response.get(0);
        long total = (long) response.get(1);

        Assertions.assertEquals(8, total);

        Assertions.assertEquals(business1.getId(), listings.get(0).getBusiness().getId());
        Assertions.assertEquals(business1.getId(), listings.get(1).getBusiness().getId());
        Assertions.assertEquals(business2Id, listings.get(6).getBusiness().getId());
        Assertions.assertEquals(business2Id, listings.get(7).getBusiness().getId());
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

        List<Object> response = saleListingService.searchSaleListings(dto, new AppUserDetails(testUser));
        List<GetSaleListingDTO> listings = (List<GetSaleListingDTO>) response.get(0);
        long total = (long) response.get(1);

        Assertions.assertEquals(8, total);

        Assertions.assertEquals(business2.getId(), listings.get(0).getBusiness().getId());
        Assertions.assertEquals(business2.getId(), listings.get(1).getBusiness().getId());
        Assertions.assertEquals(business1.getId(), listings.get(2).getBusiness().getId());
        Assertions.assertEquals(business1.getId(), listings.get(3).getBusiness().getId());
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

        List<Object> response = saleListingService.searchSaleListings(dto, new AppUserDetails(testUser));
        List<GetSaleListingDTO> listings = (List<GetSaleListingDTO>) response.get(0);
        long total = (long) response.get(1);

        Assertions.assertEquals(8, total);

        Assertions.assertEquals(business1.getId(), listings.get(0).getBusiness().getId());
        Assertions.assertEquals(business1.getId(), listings.get(1).getBusiness().getId());
        Assertions.assertEquals(business2Id, listings.get(6).getBusiness().getId());
        Assertions.assertEquals(business2Id, listings.get(7).getBusiness().getId());
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

        List<Object> response = saleListingService.searchSaleListings(dto, new AppUserDetails(testUser));
        List<GetSaleListingDTO> listings = (List<GetSaleListingDTO>) response.get(0);
        long total = (long) response.get(1);

        Assertions.assertEquals(8, total);

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

        List<Object> response = saleListingService.searchSaleListings(dto, new AppUserDetails(testUser));
        List<GetSaleListingDTO> listings = (List<GetSaleListingDTO>) response.get(0);
        long total = (long) response.get(1);

        Assertions.assertEquals(8, total);

        Assertions.assertEquals("Eighth Product", listings.get(0).getInventoryItem().getProduct().getName());
        Assertions.assertEquals("Seventh Product", listings.get(1).getInventoryItem().getProduct().getName());
        Assertions.assertEquals("Sixth Product", listings.get(2).getInventoryItem().getProduct().getName());
        Assertions.assertEquals("Fifth Product", listings.get(3).getInventoryItem().getProduct().getName());
    }

    /**
     * Test that trying to like a sale listing that hasn't been liked by this user adds it to the sale listing repository
     */
    @Test
    void likedSaleListing_listingNotLiked_success() {
        //Check that there are no liked listings and the user has no liked listings
        Assertions.assertEquals(0, likedSaleListingRepository.findAll().size());
        Assertions.assertEquals(0, userRepository.findByEmail(this.testUser.getEmail()).get(0).getLikedSaleListings().size());

        //Like the listing (this changes the user in the repository)
        saleListingService.likeSaleListing(this.saleListing1.getId(), new AppUserDetails(this.testUser));

        //Get user from repository
        this.testUser = userRepository.findByEmail(this.testUser.getEmail()).get(0);

        //Check that the liked sale listing has been added to the repository
        Assertions.assertEquals(1, likedSaleListingRepository.findAll().size());
        //Check that the user's liked listing list contains the same liked listing as in the repository
        Assertions.assertEquals(likedSaleListingRepository.findByListingAndUser(this.saleListing1, this.testUser).get(0),
                userRepository.findByEmail(this.testUser.getEmail()).get(0).getLikedSaleListings().get(0));

    }

    /**
     * Test that trying to like a sale listing that has already been liked by this user throws a
     * BadRequestException
     */
    @Test
    void likedSaleListing_listingAlreadyLiked_BadRequestException() {
        //Check that there are no liked listings
        Assertions.assertEquals(0, likedSaleListingRepository.findAll().size());

        //Like the listing (this changes the user in the repository)
        saleListingService.likeSaleListing(this.saleListing1.getId(), new AppUserDetails(this.testUser));

        //Get user from repository
        this.testUser = userRepository.findByEmail(this.testUser.getEmail()).get(0);

        //Check that the liked sale listing has been added to the repository
        Assertions.assertEquals(1, likedSaleListingRepository.findAll().size());
        //Check that the user's liked listing list contains the same liked listing as in the repository
        Assertions.assertEquals(likedSaleListingRepository.findByListingAndUser(this.saleListing1, this.testUser).get(0),
                userRepository.findByEmail(this.testUser.getEmail()).get(0).getLikedSaleListings().get(0));

        Integer saleListingId = this.saleListing1.getId();
        AppUserDetails appUser = new AppUserDetails(this.testUser);
        //Try to like the listing again
        Assertions.assertThrows(BadRequestException.class,
                () -> saleListingService.likeSaleListing(saleListingId, appUser));
    }

    /**
     * Test that trying to like a sale listing that does not exist throws a NotAcceptableException
     */
    @Test
    void likedSaleListing_listingDoesNotExist_NotAcceptableException() {
        Integer saleListingId = 100;
        AppUserDetails appUser = new AppUserDetails(this.testUser);

        //Check that there are no liked listings
        Assertions.assertEquals(0, likedSaleListingRepository.findAll().size());
        //Check that the listing doesn't exist
        Assertions.assertEquals(Optional.empty(), saleListingRepository.findById(saleListingId));
        //Try to like the listing that doesn't exist

        Assertions.assertThrows(NotAcceptableException.class,
                () -> saleListingService.likeSaleListing(saleListingId, appUser));
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

        Integer id = this.saleListing2.getId();
        AppUserDetails user = new AppUserDetails(this.testUser);
        Assertions.assertThrows(BadRequestException.class,
                () -> saleListingService.unlikeSaleListing(id, user));
    }

    /**
     * Test that trying to unlike a sale listing that has been liked by a user results in success
     */
    @Test
    void unlikeSaleListing_validRequest_success() {
        LikedSaleListing listing = new LikedSaleListing(this.testUser, this.saleListing1);
        testUser.addLikedListing(listing);
        testUser = userRepository.save(testUser);

        Integer id = this.saleListing1.getId();
        AppUserDetails user = new AppUserDetails(this.testUser);
        saleListingService.unlikeSaleListing(id, user);

        Optional<User> userOptional = userRepository.findById(testUser.getId());
        userOptional.ifPresent(value -> testUser = value);
        List<LikedSaleListing> likedSaleListings = likedSaleListingRepository.findByListingAndUser(this.saleListing1, this.testUser);
        Assertions.assertEquals(0, likedSaleListings.size());
        Assertions.assertEquals(0, testUser.getLikedSaleListings().size());
    }

    /**
     * Test that trying to purchase a listing that does not exist results in a NotAcceptableException being thrown
     */
    @Test
    void purchase_listing_listing_not_exist() {
        AppUserDetails user = new AppUserDetails(this.testUser);
        Assertions.assertThrows(NotAcceptableException.class,
                () -> saleListingService.buySaleListing(1000, user));
    }

    /**
     * Test that when purchasing a listing, a purchaser notification is created for the purchaser
     */
    @Test
    void purchase_listing_listing_purchase_notification_sent() {
        AppUserDetails user = new AppUserDetails(this.testUser);
        Assertions.assertDoesNotThrow(() -> saleListingService.buySaleListing(saleListing1.getId(), user));

        List<UserNotification> notifications = userNotificationRepository.findAll();
        Assertions.assertEquals(1, notifications.size());
        PurchaserNotification notification = (PurchaserNotification) notifications.get(0);
        Assertions.assertEquals(testUser.getId(), notification.getUser().getId());
        Assertions.assertEquals(testUser.getEmail(), notification.getUser().getEmail());
    }

    /**
     * Test that when purchasing a listing, a two notifications are created
     * One for the purchaser and one for the other user who liked the listing
     * note: the buyer also likes the listing but doesnt get sent the interested user notification as well
     */
    @Test
    void purchase_listing_listing_notifications_sent() {
        AppUserDetails user = new AppUserDetails(this.testUser);
        LikedSaleListing like1 = new LikedSaleListing(testUser, saleListing1);
        LikedSaleListing like2 = new LikedSaleListing(testOtherUser, saleListing1);
        likedSaleListingRepository.save(like1);
        testUser.addLikedListing(like1);
        likedSaleListingRepository.save(like2);
        testOtherUser.addLikedListing(like2);

        saleListingService.buySaleListing(saleListing1.getId(), user);

        List<UserNotification> notifications = userNotificationRepository.findAll();
        Assertions.assertEquals(2, notifications.size());

        //First one is a purchaser notification
        PurchaserNotification purchaserNotification = (PurchaserNotification) notifications.get(0);
        Assertions.assertEquals(testUser.getId(), purchaserNotification.getUser().getId());
        Assertions.assertEquals(testUser.getEmail(), purchaserNotification.getUser().getEmail());

        //Second one is a Interested user notification
        InterestedUserNotification interestedUserNotification = (InterestedUserNotification) notifications.get(1);
        Assertions.assertEquals(testOtherUser.getId(), interestedUserNotification.getUser().getId());
        Assertions.assertEquals(testOtherUser.getEmail(), interestedUserNotification.getUser().getEmail());
    }

    /**
     * Test that when purchasing a listing, the inventory items quantity is reduced and the sales listing is removed
     */
    @Test
    void purchase_listing_item_quantity_reduced_and_listing_removed() {
        AppUserDetails user = new AppUserDetails(this.testUser);
        InventoryItem item = saleListing1.getInventoryItem();
        Integer requiredQuantity = item.getQuantity() - saleListing1.getQuantity();

        saleListingService.buySaleListing(saleListing1.getId(), user);

        Optional<InventoryItem> itemOptional = inventoryItemRepository.findById(item.getId());
        Assertions.assertTrue(itemOptional.isPresent());
        item = itemOptional.get();
        Assertions.assertEquals(requiredQuantity, item.getQuantity());

        Optional<SaleListing> listingOptional = saleListingRepository.findById(saleListing1.getId());
        Assertions.assertTrue(listingOptional.isEmpty());
    }

    /**
     * Test that when purchasing a listing which has the last of an inventory item in it, the inventory item is removed and so is the sale listing
     */
    @Test
    void purchase_listing_item_item_and_listing_removed() {
        AppUserDetails user = new AppUserDetails(this.testUser);
        InventoryItem item = saleListing3.getInventoryItem();

        saleListingService.buySaleListing(saleListing3.getId(), user);

        Optional<InventoryItem> itemOptional = inventoryItemRepository.findById(item.getId());
        Assertions.assertTrue(itemOptional.isEmpty());

        Optional<SaleListing> listingOptional = saleListingRepository.findById(saleListing3.getId());
        Assertions.assertTrue(listingOptional.isEmpty());
    }

    /**
     * Tests the successful case for tagging a sale listing
     */
    @Test
    void tagSaleListing_success() {

        testUser = userRepository.findByEmail(testUser.getEmail()).get(0);
        LikedSaleListing listing = new LikedSaleListing(testUser, saleListing1);
        likedSaleListingRepository.save(listing);
        testUser.addLikedListing(listing);
        userRepository.save(testUser);

        AppUserDetails user = new AppUserDetails(this.testUser);
        Integer listingId = saleListing1.getId();

        saleListingService.tagSaleListing(listingId, "red", user);

        LikedSaleListing updatedLikedListing = likedSaleListingRepository
                .findByListingAndUser(saleListing1, testUser).get(0);

        Assertions.assertTrue(updatedLikedListing.getTag().matchesTag("red"));
    }

    /**
     * Tests that a BadRequestException is thrown when an invalid
     * tag is provided
     */
    @Test
    void tagSaleListing_invalidTag_badRequestException() {
        AppUserDetails user = new AppUserDetails(this.testUser);
        Integer listingId = saleListing1.getId();

        Assertions.assertThrows(BadRequestException.class,
                () -> saleListingService.tagSaleListing(listingId, "maroon", user));
    }

    /**
     * Tests that a NotAcceptableException is thrown when the user tries
     * tagging a listing that doesn't exist
     */
    @Test
    void tagSaleListing_nonExistentListing_notAcceptableException() {
        AppUserDetails user = new AppUserDetails(this.testUser);

        Assertions.assertThrows(NotAcceptableException.class,
                () -> saleListingService.tagSaleListing(45434, "red", user));
    }

    /**
     * Tests that a BadRequestException is thrown when the user tries
     * tagging a listing they haven't liked.
     */
    @Test
    void tagSaleListing_notLikedListing_badRequestException() {
        AppUserDetails user = new AppUserDetails(this.testUser);
        Integer listingId = saleListing1.getId();

        Assertions.assertThrows(BadRequestException.class,
                () -> saleListingService.tagSaleListing(listingId, "red", user));
    }

    /**
     * Tests the successful case for starring a sale listing when you have
     * not already starred it
     */
    @Test
    void starSaleListing_successWhenNotStarred_OK(){
        testUser = userRepository.findByEmail(testUser.getEmail()).get(0);
        LikedSaleListing listing = new LikedSaleListing(testUser, saleListing1);
        listing.setStarred(false);
        likedSaleListingRepository.save(listing);
        testUser.addLikedListing(listing);
        userRepository.save(testUser);

        AppUserDetails user = new AppUserDetails(this.testUser);
        Integer listingId = saleListing1.getId();

        saleListingService.starSaleListing(listingId, true, user);

        LikedSaleListing updatedLikedListing = likedSaleListingRepository
                .findByListingAndUser(saleListing1, testUser).get(0);

        Assertions.assertTrue(updatedLikedListing.isStarred());
    }

    /**
     * Tests the successful case for un-starring a sale listing when you have
     * already starred it
     */
    @Test
    void starSaleListing_successWhenAlreadyStarred_OK(){
        testUser = userRepository.findByEmail(testUser.getEmail()).get(0);
        LikedSaleListing listing = new LikedSaleListing(testUser, saleListing1);
        listing.setStarred(true);
        likedSaleListingRepository.save(listing);
        testUser.addLikedListing(listing);
        userRepository.save(testUser);

        AppUserDetails user = new AppUserDetails(this.testUser);
        Integer listingId = saleListing1.getId();

        saleListingService.starSaleListing(listingId, false, user);

        LikedSaleListing updatedLikedListing = likedSaleListingRepository
                .findByListingAndUser(saleListing1, testUser).get(0);

        Assertions.assertFalse(updatedLikedListing.isStarred());
    }

    /**
     * Tests that a BadRequestException is thrown when the user tries
     * starring a listing they haven't liked.
     */
    @Test
    void starSaleListing_notLikedListing_badRequestException() {
        AppUserDetails user = new AppUserDetails(this.testUser);
        Integer listingId = saleListing1.getId();

        Assertions.assertThrows(BadRequestException.class,
                () -> saleListingService.starSaleListing(listingId, true, user));
    }

    /**
     * Tests that a NotAcceptableException is thrown when the user tries
     * starring a listing that doesn't exist
     */
    @Test
    void starSaleListing_nonExistentListing_notAcceptableException() {
        AppUserDetails user = new AppUserDetails(this.testUser);

        Assertions.assertThrows(NotAcceptableException.class,
                () -> saleListingService.starSaleListing(45434, true, user));
    }

    /**
     * Tests that getFeaturedSaleListings method only returns featured listings
     */
    @Test
    void getFeaturedSaleListings_returnsOnlyFeaturedListings() {
        List<GetSaleListingDTO> business1Listings = saleListingService.getFeaturedSaleListings(business1.getId(), testUser);
        List<GetSaleListingDTO> business2Listings = saleListingService.getFeaturedSaleListings(business2Id, testUser);
        Assertions.assertEquals(1, business1Listings.size());
        Assertions.assertEquals(0, business2Listings.size());
    }

    /**
     * Tests the successful case for featuring a sale listing when you have
     * not already featured it (makes the sale listing featured)
     */
    @Test
    void featureSaleListing_successWhenNotFeatured_OK(){
        Assertions.assertFalse(saleListing1.isFeatured());

        AppUserDetails user = new AppUserDetails(testAdmin);
        saleListingService.featureSaleListing(saleListing1.getId(), business1.getId(), true, user);

        saleListing1 = saleListingRepository.getOne(saleListing1.getId());
        Assertions.assertTrue(saleListing1.isFeatured());
    }

    /**
     * Tests the successful case for featuring a sale listing when you have
     * already featured it (makes the sale listing no longer featured)
     */
    @Test
    void featureSaleListing_successWhenAlreadyFeatured_OK(){
        saleListing1.setFeatured(true);
        saleListingRepository.save(saleListing1);
        Assertions.assertTrue(saleListing1.isFeatured());

        AppUserDetails user = new AppUserDetails(testAdmin);
        saleListingService.featureSaleListing(saleListing1.getId(), business1.getId(),false, user);

        saleListing1 = saleListingRepository.getOne(saleListing1.getId());
        Assertions.assertFalse(saleListing1.isFeatured());
    }

    /**
     * Tests that a NotAcceptableException is thrown when
     * a someone tries featuring a sale listing to a nonexistent business.
     */
    @Test
    void featureSaleListing_nonExistentBusiness_NotAcceptableException(){
        Mockito.doThrow(new NotAcceptableException(""))
                .when(businessService).checkBusiness(any(Integer.class));

        AppUserDetails user = new AppUserDetails(testAdmin);
        Integer saleListingID = saleListing1.getId();

        Assertions.assertThrows(NotAcceptableException.class,
                () -> saleListingService.featureSaleListing(saleListingID, 99, true, user));
    }

    /**
     * Tests that a Forbidden Exception is thrown when someone tries featuring a sale listing
     * for a business they are not an admin of
     */
    @Test
    void featureSaleListing_notAdmin_ForbiddenException() {
        Mockito.when(businessService.checkBusiness(business1.getId()))
                .thenReturn(business1);

        Mockito.doThrow(new ForbiddenException(""))
                .when(businessService).checkUserCanDoBusinessAction(any(AppUserDetails.class), any(Business.class));
        AppUserDetails user = new AppUserDetails(testUser);
        Integer saleListingID = saleListing1.getId();
        Integer businessID = business1.getId();
        Assertions.assertThrows(ForbiddenException.class,
                () -> saleListingService.featureSaleListing(saleListingID, businessID, true, user));
    }

    /**
     * Tests that a Not Acceptable Exception is thrown when a user tries to feature a sale listing that
     * does not exist
     */
    @Test
    void featureSaleListing_invalidSaleListingID_NotAcceptableException(){
        AppUserDetails user = new AppUserDetails(testAdmin);
        Integer businessID = business1.getId();
        Assertions.assertThrows(NotAcceptableException.class,
                () -> saleListingService.featureSaleListing(99, businessID, true, user));
    }

    /**
     * Tests that a Bad Request Exception is thrown when a user tries to feature a sale listing when they
     * already have the max number of featured sale listings 5
     */
    @Test
    void featureSaleListing_tooManyFeatured_BadRequestException(){
        saleListing1.setFeatured(true);
        saleListingRepository.save(saleListing1);
        saleListing2.setFeatured(true);
        saleListingRepository.save(saleListing2);
        saleListing5.setFeatured(true);
        saleListingRepository.save(saleListing5);
        saleListing6.setFeatured(true);
        saleListingRepository.save(saleListing6);
        saleListing8.setFeatured(true);
        saleListingRepository.save(saleListing8);

        AppUserDetails user = new AppUserDetails(testAdmin);
        Integer listingId = saleListing7.getId();
        Integer businessID = business1.getId();
        Assertions.assertThrows(BadRequestException.class,
                () -> saleListingService.featureSaleListing(listingId, businessID, true, user));
    }
}
