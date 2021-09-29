package gradle.cucumber.steps;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONArray;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.Business;
import org.seng302.project.repository_layer.model.Sale;
import org.seng302.project.repository_layer.model.User;
import org.seng302.project.repository_layer.repository.*;
import org.seng302.project.service_layer.dto.sales_report.GetSaleDTO;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Configurable
@AutoConfigureTestDatabase
public class SaleHistorySteps extends AbstractInitializer {

    private final MockMvc mockMvc;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final SaleHistoryRepository saleHistoryRepository;
    private final ReviewRepository reviewRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final ObjectMapper objectMapper;

    private User testUser;
    private Business testBusiness;
    private Sale testSale;

    private MvcResult result;

    @Autowired
    public SaleHistorySteps(
            AddressRepository addressRepository,
            UserRepository userRepository,
            BusinessRepository businessRepository,
            SaleHistoryRepository saleHistoryRepository,
            ObjectMapper objectMapper,
            WebApplicationContext webApplicationContext,
            UserNotificationRepository userNotificationRepository,
            ReviewRepository reviewRepository
    ) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.saleHistoryRepository = saleHistoryRepository;
        this.objectMapper = objectMapper;
        this.userNotificationRepository = userNotificationRepository;
        this.reviewRepository = reviewRepository;
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    @Autowired
    public void setup() {
        userNotificationRepository.deleteAll();
        reviewRepository.deleteAll();
        saleHistoryRepository.deleteAll();
        userRepository.deleteAll();
        businessRepository.deleteAll();
        addressRepository.deleteAll();

        testUser = this.getTestUser();
        testUser.setHomeAddress(addressRepository.save(testUser.getHomeAddress()));
        testUser = userRepository.save(testUser);

        User testBusinessAdmin = this.getTestUserBusinessAdmin();
        testBusinessAdmin.setHomeAddress(addressRepository.save(testBusinessAdmin.getHomeAddress()));
        userRepository.save(testBusinessAdmin);

        testBusiness = this.getTestBusiness();
        testBusiness.setPrimaryAdministratorId(testBusinessAdmin.getId());
        testBusiness.setAdministrators(List.of(testBusinessAdmin));
        testBusiness.setAddress(addressRepository.save(testBusiness.getAddress()));
        testBusiness = businessRepository.save(testBusiness);
    }

    @AfterEach
    public void teardown() {
        saleHistoryRepository.deleteAll();
        userRepository.deleteAll();
        businessRepository.deleteAll();
        addressRepository.deleteAll();
    }

    @Given("I am an individual user who has purchased a listing")
    public void i_am_an_individual_user_who_has_purchased_a_listing() {
        testSale = new Sale(this.getSaleListings().get(0));
        testSale.setBuyerId(testUser.getId());
        testSale.setBusiness(testBusiness);
        testSale = saleHistoryRepository.save(testSale);
        Assertions.assertEquals(testUser.getId(), testSale.getBuyerId());
    }

    @When("I make a request to view my sale history")
    public void i_make_a_request_to_view_my_sale_history() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/users/{userId}/purchases", testUser.getId())
                .param("pageNumber", String.valueOf(0))
                .param("sortBy", "")
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));
        result = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
    }

    @Then("The sales I have purchased are given to me")
    public void the_sales_i_have_purchased_are_given_to_me() throws Exception {

        String response = new JSONArray(result.getResponse().getContentAsString()).getJSONArray(0).toString();

        List<GetSaleDTO> purchases = objectMapper.readValue(response, new TypeReference<>() {
        });
        Assertions.assertEquals(1, purchases.size());
        Assertions.assertEquals(testSale.getOldListingId(), purchases.get(0).getOldListingId());
    }

}
