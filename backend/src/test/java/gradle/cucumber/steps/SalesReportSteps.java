package gradle.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.*;
import org.seng302.project.repository_layer.repository.*;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;


import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

/**
 * Cucumber steps for the U41 Sales Report story
 */
@AutoConfigureTestDatabase
@Transactional
public class SalesReportSteps extends AbstractInitializer {

    private MockMvc mockMvc;

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final SaleHistoryRepository saleHistoryRepository;

    private Business business;
    private User owner;
    private Product product;
    private InventoryItem inventoryItem;
    private SaleListing saleListing;

    private ResultActions mockMvcResult;

    @Autowired
    public SalesReportSteps(UserRepository userRepository, BusinessRepository businessRepository,
                            SaleHistoryRepository saleHistoryRepository) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.saleHistoryRepository = saleHistoryRepository;
    }

    /**
     * Before each test
     */
    @BeforeEach
    @Autowired
    void setup(WebApplicationContext context) {
        this.initialise();
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        owner = this.getTestUserBusinessAdmin();
        owner.setHomeAddress(null);
        owner = userRepository.save(owner);

        business = this.getTestBusiness();
        business.setAddress(null);
        business.setPrimaryAdministratorId(owner.getId());
        business = businessRepository.save(business);

        product = new Product("PROD", "Product", "Just a product", "Me",
                2.00, business.getId());
        inventoryItem = new InventoryItem(product, 2, 0.50, 1.00,
                "2021-05-21", "2021-11-21", "2021-11-15", "2021-11-21");
        saleListing = new SaleListing(business, inventoryItem, 1.00, "", LocalDateTime.now().minusDays(1), 2);
    }

    @AfterEach
    void teardown() {
        businessRepository.deleteAll();
        userRepository.deleteAll();
        saleHistoryRepository.deleteAll();
    }

    //AC2
    @Given("I had {int} sales in June, {int} sales in July and {int} sale in August")
    public void i_had_sales_in_june_sales_in_july_and_sale_in_august(Integer juneSalesCount, Integer julySalesCount,
                                                                     Integer augSalesCount) {
        for (int i = 0; i < juneSalesCount; i++) {
            Sale sale = new Sale(saleListing);
            sale.setDateSold(LocalDateTime.of(2021, 6, 4, 14, 30));
            saleHistoryRepository.save(sale);
        }
        for (int i = 0; i < julySalesCount; i++) {
            Sale sale = new Sale(saleListing);
            sale.setDateSold(LocalDateTime.of(2021, 7, 4, 14, 30));
            saleHistoryRepository.save(sale);
        }
        for (int i = 0; i < augSalesCount; i++) {
            Sale sale = new Sale(saleListing);
            sale.setDateSold(LocalDateTime.of(2021, 8, 4, 14, 30));
            saleHistoryRepository.save(sale);
        }
    }


    @When("I request a sales report for July")
    public void i_request_a_sales_report_for_july() throws Exception {
        mockMvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get("/businesses/{id}/salesReport", business.getId())
                .param("periodStart", LocalDate.of(2021, 7, 1).toString())
                .param("periodEnd", LocalDate.of(2021, 7, 31).toString())
                .param("granularity", "all")
                .with(user(new AppUserDetails(owner)))
        );
        //TODO: User with id 2 can not perform this action as they are not an administrator of business with id 1. :(
    }

    @Then("The {int} sales from July are shown")
    public void the_sales_from_july_are_shown(Integer julySalesCount) throws JSONException, UnsupportedEncodingException {
        String contentAsString = mockMvcResult.andReturn().getResponse().getContentAsString();
        JSONArray results = new JSONArray(contentAsString);
        //TODO: compare total sales in results to julySalesCount
    }


    @Given("I had {int} sales in {int}, {int} sales in {int} and {int} sale in {int}")
    public void i_had_sales_in_sales_in_and_sale_in(Integer salesCount1, Integer year1, Integer salesCount2,
                                                    Integer year2, Integer salesCount3, Integer year3) {
        for (int i = 0; i < salesCount1; i++) {
            Sale sale = new Sale(saleListing);
            sale.setDateSold(LocalDateTime.of(year1, 6, 4, 14, 30));
            saleHistoryRepository.save(sale);
        }
        for (int i = 0; i < salesCount2; i++) {
            Sale sale = new Sale(saleListing);
            sale.setDateSold(LocalDateTime.of(year2, 7, 4, 14, 30));
            saleHistoryRepository.save(sale);
        }
        for (int i = 0; i < salesCount3; i++) {
            Sale sale = new Sale(saleListing);
            sale.setDateSold(LocalDateTime.of(year3, 8, 4, 14, 30));
            saleHistoryRepository.save(sale);
        }
    }

    @When("I request a sales report for {int}") //year = 2020
    public void i_request_a_sales_report_for(Integer year) throws Exception {
        mockMvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get("/businesses/{id}/salesReport", business.getId())
                .param("periodStart", LocalDate.of(year, 1, 1).toString())
                .param("periodEnd", LocalDate.of(year, 12, 31).toString())
                .param("granularity", "all")
                .with(user(new AppUserDetails(owner)))
        );
    }

    @Then("The {int} sales from {int} are shown") //salesCount = 3, year = 2020
    public void the_sales_from_are_shown(Integer salesCount, Integer year) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    //AC3

    @Given("I had sales on {string}, {string}, and {string}")
    public void i_had_sales_on_and(String date1, String date2, String date3) {
        Sale sale1 = new Sale(saleListing);
        sale1.setDateSold(LocalDateTime.parse(date1));
        saleHistoryRepository.save(sale1);

        Sale sale2 = new Sale(saleListing);
        sale2.setDateSold(LocalDateTime.parse(date2));
        saleHistoryRepository.save(sale2);

        Sale sale3 = new Sale(saleListing);
        sale3.setDateSold(LocalDateTime.parse(date3));
        saleHistoryRepository.save(sale3);
    }

    @When("I select a report from {string} to {string}")
    public void i_select_a_report_from_to(String date1, String date2) throws Exception {
        mockMvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get("/businesses/{id}/salesReport", business.getId())
                .param("periodStart", date1)
                .param("periodEnd", date2)
                .param("granularity", "all")
                .with(user(new AppUserDetails(owner)))
        );
    }

    @Then("The {int} sales from that period are shown")
    public void the_sales_from_that_period_are_shown(Integer salesCount) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    //AC4
    @Given("In June I had one sale worth ${double} and one sale worth ${double}")
    public void in_june_i_had_one_sale_worth_$_and_one_sale_worth_$(Double salePrice1, Double salePrice2) {
        Sale sale1 = new Sale(saleListing);
        sale1.setDateSold(LocalDateTime.of(2021, 6, 4, 14, 30));
        sale1.setPrice(salePrice1);
        saleHistoryRepository.save(sale1);

        Sale sale2 = new Sale(saleListing);
        sale2.setDateSold(LocalDateTime.of(2021, 6, 4, 14, 30));
        sale1.setPrice(salePrice2);
        saleHistoryRepository.save(sale2);
    }

    @When("I request a sales report for June")
    public void i_request_a_sales_report_for_june() throws Exception {
        mockMvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get("/businesses/{id}/salesReport", business.getId())
                .param("periodStart", LocalDate.of(2021, 6, 1).toString())
                .param("periodEnd", LocalDate.of(2021, 6, 31).toString())
                .param("granularity", "all")
                .with(user(new AppUserDetails(owner)))
        );
    }

    @Then("The report shows a total number of {int}")
    public void the_report_shows_a_total_number_of(Integer salesCount) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("The report shows a total value of ${double}")
    public void the_report_shows_a_total_value_of_$(Double totalValue) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    //AC5

    @When("I request a sales report from June to August with monthly granularity")
    public void i_request_a_sales_report_from_june_to_august_with_monthly_granularity() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("The report shows total numbers of {int} for June, {int} for July and {int} for August")
    public void the_report_shows_total_numbers_of_for_june_for_july_and_for_august(Integer juneSalesCount,
                                                                                   Integer julySalesCount,
                                                                                   Integer augSalesCount) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Given("I had ${double} worth of sales in June, ${double} worth of sales in July and ${double} worth of sales in August")
    public void i_had_$_worth_of_sales_in_june_$_worth_of_sales_in_july_and_$_worth_of_sales_in_august(Double juneTotalValue,
                                                                                                       Double julyTotalValue,
                                                                                                       Double augTotalValue) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("The report shows total values of ${double} for June, ${double} for July and ${double} for August")
    public void the_report_shows_total_values_of_$_for_june_$_for_july_and_$_for_august(Double juneTotalValue,
                                                                                        Double julyTotalValue,
                                                                                        Double augTotalValue) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

}
