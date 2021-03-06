package org.seng302.project.service_layer.service;

import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.*;
import org.seng302.project.repository_layer.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

/**
 * Resale Statistics Service unit tests
 */
@DataJpaTest
class ResaleStatisticsServiceTest extends AbstractInitializer {

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
    private final ResaleStatisticsService resaleStatisticsService;

    @Autowired
    public ResaleStatisticsServiceTest(UserRepository userRepository,
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
        this.saleListingService = Mockito.mock(SaleListingService.class);
        this.resaleStatisticsService = new ResaleStatisticsService(this.userRepository, this.saleListingRepository, this.saleHistoryRepository);
    }

    @BeforeEach
    void setup() {
        User testUser = this.getTestUser();
        addressRepository.save(testUser.getHomeAddress());
        testUser.setId(null);
        userRepository.save(testUser);

        User testOtherUser = this.getTestOtherUser();
        addressRepository.save(testOtherUser.getHomeAddress());
        testOtherUser.setId(null);
        userRepository.save(testOtherUser);

        User testAdmin = this.getTestUserBusinessAdmin();
        addressRepository.save(testAdmin.getHomeAddress());
        testAdmin.setId(null);
        testAdmin = userRepository.save(testAdmin);

        Address address1 = new Address(null, null, "Rangiora", null, "Netherlands", null);
        Business business1 = new Business("First Business", null, address1, "Retail Trade", testAdmin.getId());
        addressRepository.save(address1);
        business1 = businessRepository.save(business1);

        Product product1 = new Product("TEST-1", "First Product", null, null, 5.00, business1.getId());
        productRepository.save(product1);
        InventoryItem inventoryItem = new InventoryItem(product1, 10, null, null, "2021-01-01", null, null, "2021-12-01");
        inventoryItem = inventoryItemRepository.save(inventoryItem);
        SaleListing saleListing1 = new SaleListing(business1, inventoryItem, 10.00, null, LocalDateTime.parse("2021-08-25T00:00:00"), 5);
        saleListingRepository.save(saleListing1);

        Product product2 = new Product("TEST-2", "Second Product", null, null, 5.00, business1.getId());
        productRepository.save(product2);
        InventoryItem inventoryItem2 = new InventoryItem(product2, 10, null, null, "2021-01-01", null, null, "2021-12-02");
        inventoryItem2 = inventoryItemRepository.save(inventoryItem2);
        SaleListing saleListing2 = new SaleListing(business1, inventoryItem2, 15.00, null, LocalDateTime.parse("2021-10-25T00:00:00"), 10);
        saleListingRepository.save(saleListing2);

        Address address2 = new Address(null, null, "Christchurch", null, "New Zealand", null);
        Business business2 = new Business("Second Business", null, address2, "Charitable Organisation", 1);
        addressRepository.save(address2);
        businessRepository.save(business2);

        Product product3 = new Product("TEST-3", "Third Product", null, null, 5.00, business2.getId());
        productRepository.save(product3);
        InventoryItem inventoryItem3 = new InventoryItem(product3, 5, null, null, "2021-01-01", null, null, "2021-12-03");
        inventoryItem3 = inventoryItemRepository.save(inventoryItem3);
        SaleListing saleListing3 = new SaleListing(business2, inventoryItem3, 20.00, null, LocalDateTime.parse("2021-11-25T00:00:00"), 5);
        saleListingRepository.save(saleListing3);
        Sale sale1 = new Sale(saleListing3);
        saleHistoryRepository.save(sale1);

        Product product4 = new Product("TEST-4", "Fourth Product", null, null, 5.00, business2.getId());
        productRepository.save(product4);
        InventoryItem inventoryItem4 = new InventoryItem(product4, 5, null, null, "2021-01-01", null, null, "2021-12-04");
        inventoryItem4 = inventoryItemRepository.save(inventoryItem4);
        SaleListing saleListing4 = new SaleListing(business2, inventoryItem4, 30.00, null, LocalDateTime.parse("2021-12-25T00:00:00"), 5);
        saleListingRepository.save(saleListing4);
        Sale sale2 = new Sale(saleListing4);
        saleHistoryRepository.save(sale2);
    }

    @Test
    void getResaleStatistics_success(){
        JSONObject response = resaleStatisticsService.getStatistics();
        Assertions.assertEquals(Long.valueOf("3"), response.getAsNumber("totalUserCount"));
        Assertions.assertEquals(Long.valueOf("4"), response.getAsNumber("numAvailableListings"));
        Assertions.assertEquals(Long.valueOf("2"), response.getAsNumber("totalNumSales"));

    }
}
