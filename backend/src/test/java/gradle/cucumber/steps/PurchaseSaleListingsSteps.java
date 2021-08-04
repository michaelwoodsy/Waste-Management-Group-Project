package gradle.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.Business;
import org.seng302.project.repository_layer.model.Product;
import org.seng302.project.repository_layer.model.SaleListing;
import org.seng302.project.repository_layer.model.User;
import org.seng302.project.repository_layer.repository.*;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

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
    private final CardRepository cardRepository;

    private ResultActions result;



    @Autowired
    public PurchaseSaleListingsSteps(UserRepository userRepository,
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
     * Before each test, setup four sale listings with different parameters
     */
    @BeforeEach
    @Autowired
    void setup(WebApplicationContext context) {

    }


    @Given("A sales listing exists for a business")
    public void aSalesListingExistsForABusiness() {
    }


    @When("A user purchases the sale listing")
    public void aUserPurchasesTheSaleListing() {
    }

    //AC3: When a sale listing is purchased, the sellers inventory is updated to reflect that the goods have been sold

    @Then("The businesses inventory item that the sale listing is for is updated to show that the sale listing was purchased")
    public void theBusinessesInventoryItemThatTheSaleListingIsForIsUpdatedToShowThatTheSaleListingWasPurchased() {
    }

    //AC4: The sale listing is removed after purchase

    @Then("The sale listing gets removed from the businesses sales listings")
    public void theSaleListingGetsRemovedFromTheBusinessesSalesListings() {
    }

    //AC5: After a sales listing is purchased, information about the sale is stored in sales history

    @Then("The sale listing is stored in sales history")
    public void theSaleListingIsStoredInSalesHistory() {
    }
}