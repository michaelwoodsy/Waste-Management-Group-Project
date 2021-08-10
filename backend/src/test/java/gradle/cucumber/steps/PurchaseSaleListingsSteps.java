package gradle.cucumber.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.*;
import org.seng302.project.repository_layer.repository.*;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;


/**
 * Steps for the Cucumber tests relating to U29 Browse/Search Sales Listings
 */
@AutoConfigureTestDatabase
public class PurchaseSaleListingsSteps extends AbstractInitializer {

    private MockMvc mockMvc;

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final SaleListingRepository saleListingRepository;
    private final LikedSaleListingRepository likedSaleListingRepository;
    private final SaleHistoryRepository saleHistoryRepository;

    private final UserNotificationRepository userNotificationRepository;

    private SaleListing listing;
    private User testUser;
    private User testOtherUser;



    @Autowired
    public PurchaseSaleListingsSteps(UserRepository userRepository,
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
    }

    /**
     * Before each test, setup four sale listings with different parameters
     */
    @BeforeEach
    @Autowired
    void setup(WebApplicationContext context) {
        likedSaleListingRepository.deleteAll();
        userNotificationRepository.deleteAll();
        userRepository.deleteAll();

        this.initialise();
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        testUser = this.getTestUser();
        addressRepository.save(testUser.getHomeAddress());
        testUser.setId(null);
        userRepository.save(testUser);

        testOtherUser = this.getTestOtherUser();
        addressRepository.save(testOtherUser.getHomeAddress());
        testOtherUser.setId(null);
        userRepository.save(testOtherUser);
    }

    @AfterEach
    void teardown() {
        userNotificationRepository.deleteAll();
        likedSaleListingRepository.deleteAll();
        saleHistoryRepository.deleteAll();
        saleListingRepository.deleteAll();
        inventoryItemRepository.deleteAll();
        productRepository.deleteAll();
        businessRepository.deleteAll();
        addressRepository.deleteAll();
    }

    //AC1: When a sales listing is purchased all other users who have liked the sales listing are notified

    @Given("A sales listing exists for a business")
    public void aSalesListingExistsForABusiness() {

        listing = this.getSaleListings().get(0);

        addressRepository.save(listing.getBusiness().getAddress());
        Business business = listing.getBusiness();
        business = businessRepository.save(business);
        Product product = listing.getInventoryItem().getProduct();
        product.setBusinessId(business.getId());
        productRepository.save(product);
        inventoryItemRepository.save(listing.getInventoryItem());
        listing = saleListingRepository.save(listing);

        Assertions.assertEquals("First Product", listing.getInventoryItem().getProduct().getName());
        Assertions.assertEquals(10, listing.getInventoryItem().getQuantity());
        Assertions.assertEquals(4, listing.getQuantity());
    }

    @And("Other users have liked that sales listing")
    public void otherUsersHaveLikedThatSalesListing() {
        LikedSaleListing like1 = new LikedSaleListing(testUser, listing);
        likedSaleListingRepository.save(like1);
        testUser.addLikedListing(like1);
        userRepository.save(testUser);

        LikedSaleListing like2 = new LikedSaleListing(testOtherUser, listing);
        likedSaleListingRepository.save(like2);
        testOtherUser.addLikedListing(like2);
        userRepository.save(testOtherUser);
    }

    @When("A user purchases the sale listing")
    public void aUserPurchasesTheSaleListing() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/listings/{listingId}/buy", listing.getId())
                .with(user(new AppUserDetails(testUser)))).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Transactional
    @Then("Notifications are created for the users that liked that sales listing notifying them that it has been purchased")
    public void notificationsAreCreatedForTheUsersThatLikedThatSalesListingNotifyingThemThatItHasBeenPurchased() {
        List<UserNotification> notifications = userNotificationRepository.findAllByUser(testUser);
        Assertions.assertEquals(1, notifications.size());
        //The purchaser does not get a notification saying the listing has been sold
        Assertions.assertEquals(PurchaserNotification.class, notifications.get(0).getClass());

        notifications = userNotificationRepository.findAllByUser(testOtherUser);
        Assertions.assertEquals(1, notifications.size());
        //The purchaser does not get a notification saying the listing has been sold
        Assertions.assertEquals(InterestedUserNotification.class, notifications.get(0).getClass());

        InterestedUserNotification expectedNotification = new InterestedUserNotification(testOtherUser, listing);
        Assertions.assertEquals(expectedNotification.getMessage(), notifications.get(0).getMessage());

        var user1 = userRepository.findById(testUser.getId());
        Assertions.assertTrue(user1.isPresent());
        Assertions.assertEquals(0, user1.get().getLikedSaleListings().size());

        var user2 = userRepository.findById(testUser.getId());
        Assertions.assertTrue(user2.isPresent());
        Assertions.assertEquals(0, user2.get().getLikedSaleListings().size());
    }

    //AC2: A notification is sent to the purchaser of a sales listing detailing payment and collection details

    @Then("A notification is sent to the purchaser with payment and collection details")
    public void aNotificationIsSentToThePurchaserWithPaymentAndCollectionDetails() {
        List<UserNotification> notifications = userNotificationRepository.findAllByUser(testUser);
        Assertions.assertEquals(1, notifications.size());
        Assertions.assertEquals(PurchaserNotification.class, notifications.get(0).getClass());

        PurchaserNotification expectedNotification = new PurchaserNotification(testUser, listing, listing.getBusiness());
        Assertions.assertEquals(expectedNotification.getMessage(), notifications.get(0).getMessage());
    }

    //AC3: When a sale listing is purchased, the sellers inventory is updated to reflect that the goods have been sold

    @Then("The businesses inventory item that the sale listing is for is updated to show that the sale listing was purchased")
    public void theBusinessesInventoryItemThatTheSaleListingIsForIsUpdatedToShowThatTheSaleListingWasPurchased() {
        Integer newQuantity = listing.getInventoryItem().getQuantity() - listing.getQuantity();

        Optional<InventoryItem> itemOptional = inventoryItemRepository.findById(listing.getInventoryItem().getId());

        Assertions.assertTrue(itemOptional.isPresent());
        InventoryItem item = itemOptional.get();

        Assertions.assertEquals(newQuantity, item.getQuantity());
    }

    //AC4: The sale listing is removed after purchase

    @Then("The sale listing gets removed from the businesses sales listings")
    public void theSaleListingGetsRemovedFromTheBusinessesSalesListings() {
        Assertions.assertNotNull(listing.getId());
        Optional<SaleListing> listingOptional = saleListingRepository.findById(listing.getId());
        Assertions.assertTrue(listingOptional.isEmpty());
    }

    //AC5: After a sales listing is purchased, information about the sale is stored in sales history

    @Then("The sale listing is stored in sales history")
    public void theSaleListingIsStoredInSalesHistory() {
        Optional<Sale> saleOptional = saleHistoryRepository.findByOldListingId(listing.getId());
        Assertions.assertTrue(saleOptional.isPresent());
        Sale sale = saleOptional.get();

        Assertions.assertEquals(sale.getInventoryItem().getProduct().getName(), listing.getInventoryItem().getProduct().getName());
    }
}