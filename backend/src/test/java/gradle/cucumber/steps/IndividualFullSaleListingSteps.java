package gradle.cucumber.steps;

import org.junit.jupiter.api.BeforeEach;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.*;
import org.seng302.project.repository_layer.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

/**
 * Steps for the Cucumber tests relating to U30 - Individual Full Sale Listing
 */
public class IndividualFullSaleListingSteps extends AbstractInitializer {
    private MockMvc mockMvc;

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final SaleListingRepository saleListingRepository;
    private final CardRepository cardRepository;

    User testUser;

    Business business1;
    Business business2;

    private ResultActions result;



    @Autowired
    public IndividualFullSaleListingSteps(UserRepository userRepository,
                                   BusinessRepository businessRepository,
                                   AddressRepository addressRepository,
                                   ProductRepository productRepository,
                                   InventoryItemRepository inventoryItemRepository,
                                   SaleListingRepository saleListingRepository,
                                   CardRepository cardRepository) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.saleListingRepository = saleListingRepository;
        this.cardRepository = cardRepository;
    }

    /**
     * Before each test, setup four sale listings with different parameters and 1 liked sale listing
     *
     */
    @BeforeEach
    @Autowired
    void setup(WebApplicationContext context) {
        //need to clear the local repositories (these are repositories only used in this test class as I used the @AutoConfigureTestDatabase annotation)
        cardRepository.deleteAll();
        userRepository.deleteAll();
        saleListingRepository.deleteAll();
        inventoryItemRepository.deleteAll();
        productRepository.deleteAll();
        businessRepository.deleteAll();
        addressRepository.deleteAll();

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        this.initialise();
        testUser = this.getTestUser();
        addressRepository.save(testUser.getHomeAddress());
        userRepository.save(testUser);

        List<SaleListing> listings = this.getSaleListings();

        for (SaleListing listing : listings) {
            addressRepository.save(listing.getBusiness().getAddress());
            Business business = listing.getBusiness();
            businessRepository.save(business);
            Product product = listing.getInventoryItem().getProduct();
            product.setBusinessId(business.getId());
            productRepository.save(product);
            saleListingRepository.save(listing);
        }
    }


}
