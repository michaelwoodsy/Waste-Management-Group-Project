package gradle.cucumber.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.*;
import org.seng302.project.repository_layer.repository.*;
import org.seng302.project.service_layer.dto.sales_report.GetSalesReportDTO;
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
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

/**
 * Cucumber steps for the U41 Sales Report story
 */
@AutoConfigureTestDatabase
@Transactional
public class SalesReportSteps extends AbstractInitializer {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

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
    public SalesReportSteps(UserRepository userRepository,
                            BusinessRepository businessRepository,
                            SaleHistoryRepository saleHistoryRepository,
                            ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.saleHistoryRepository = saleHistoryRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Before each test
     */
    @BeforeEach
    @Autowired
    void setup(WebApplicationContext context, UserNotificationRepository userNotificationRepository) {
        userNotificationRepository.deleteAll();
        saleHistoryRepository.deleteAll();
        businessRepository.deleteAll();
        userRepository.deleteAll();


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
        business.setAdministrators(List.of(owner));
        business = businessRepository.save(business);

        product = new Product("PROD", "Product", "Just a product", "Me",
                2.00, business.getId());
        inventoryItem = new InventoryItem(product, 2, 0.50, 1.00,
                "2021-05-21", "2021-11-21", "2021-11-15", "2021-11-21");
        saleListing = new SaleListing(business, inventoryItem, 1.00, "", LocalDateTime.now().minusDays(1), 2);
    }

    @AfterEach
    void teardown() {
        saleHistoryRepository.deleteAll();
        businessRepository.deleteAll();
        userRepository.deleteAll();
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
                .get("/businesses/{id}/sales", business.getId())
                .param("periodStart", LocalDate.of(2021, 7, 1).toString())
                .param("periodEnd", LocalDate.of(2021, 7, 31).toString())
                .param("granularity", "all")
                .with(user(new AppUserDetails(owner)))
        );
    }

    @Then("The {int} sales from July are shown")
    public void the_sales_from_july_are_shown(Integer julySalesCount) throws Exception {
        String contentAsString = mockMvcResult.andReturn().getResponse().getContentAsString();
        JSONArray sales = new JSONArray(contentAsString);
        System.out.println(sales.getString(0));
        GetSalesReportDTO report = objectMapper.readValue(sales.getString(0), GetSalesReportDTO.class);
        Assertions.assertEquals(julySalesCount, report.getSales().size());
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
                .get("/businesses/{id}/sales", business.getId())
                .param("periodStart", LocalDate.of(year, 1, 1).toString())
                .param("periodEnd", LocalDate.of(year, 12, 31).toString())
                .param("granularity", "all")
                .with(user(new AppUserDetails(owner)))
        );
    }

    @Then("The {int} sales from {int} are shown") //salesCount = 3, year = 2020
    public void the_sales_from_are_shown(Integer salesCount, Integer year) throws Exception {
        String contentAsString = mockMvcResult.andReturn().getResponse().getContentAsString();
        JSONArray sales = new JSONArray(contentAsString);
        GetSalesReportDTO report = objectMapper.readValue(sales.getString(0), GetSalesReportDTO.class);
        Assertions.assertEquals(salesCount, report.getSales().size());
    }

    //AC3

    @Given("I had sales on {string}, {string}, and {string}")
    public void i_had_sales_on_and(String date1, String date2, String date3) {
        Sale sale1 = new Sale(saleListing);
        sale1.setDateSold(LocalDate.parse(date1).atStartOfDay());
        saleHistoryRepository.save(sale1);

        Sale sale2 = new Sale(saleListing);
        sale2.setDateSold(LocalDate.parse(date2).atStartOfDay());
        saleHistoryRepository.save(sale2);

        Sale sale3 = new Sale(saleListing);
        sale3.setDateSold(LocalDate.parse(date3).atStartOfDay());
        saleHistoryRepository.save(sale3);
    }

    @When("I select a report from {string} to {string}")
    public void i_select_a_report_from_to(String date1, String date2) throws Exception {
        mockMvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get("/businesses/{id}/sales", business.getId())
                .param("periodStart", date1)
                .param("periodEnd", date2)
                .param("granularity", "all")
                .with(user(new AppUserDetails(owner)))
        );
    }

    @Then("The {int} sales from that period are shown")
    public void the_sales_from_that_period_are_shown(Integer salesCount) throws Exception {
        String contentAsString = mockMvcResult.andReturn().getResponse().getContentAsString();
        JSONArray sales = new JSONArray(contentAsString);
        GetSalesReportDTO report = objectMapper.readValue(sales.getString(0), GetSalesReportDTO.class);
        Assertions.assertEquals(salesCount, report.getSales().size());
    }

    //AC4
    @Given("In June I had one sale worth ${double} and one sale worth ${double}")
    public void in_june_i_had_one_sale_worth_$_and_one_sale_worth_$(Double salePrice1, Double salePrice2) {
        Sale sale1 = new Sale(saleListing);
        sale1.setDateSold(LocalDateTime.of(2021, 6, 4, 14, 30));
        sale1.setPrice(salePrice1);
        saleHistoryRepository.save(sale1);

        Sale sale2 = new Sale(saleListing);
        sale2.setDateSold(LocalDateTime.of(2021, 6, 5, 14, 30));
        sale2.setPrice(salePrice2);
        saleHistoryRepository.save(sale2);
    }

    @When("I request a sales report for June")
    public void i_request_a_sales_report_for_june() throws Exception {
        mockMvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get("/businesses/{id}/sales", business.getId())
                .param("periodStart", LocalDate.of(2021, 6, 1).toString())
                .param("periodEnd", LocalDate.of(2021, 6, 30).toString())
                .param("granularity", "all")
                .with(user(new AppUserDetails(owner)))
        );
    }

    @Then("The report shows a total number of {int}")
    public void the_report_shows_a_total_number_of(Integer salesCount) throws Exception {
        String contentAsString = mockMvcResult.andReturn().getResponse().getContentAsString();
        JSONArray sales = new JSONArray(contentAsString);
        GetSalesReportDTO report = objectMapper.readValue(sales.getString(0), GetSalesReportDTO.class);
        Assertions.assertEquals(salesCount, report.getSales().size());
    }

    @Then("The report shows a total value of ${double}")
    public void the_report_shows_a_total_value_of_$(Double expectedTotalValue) throws UnsupportedEncodingException, JSONException {
        String contentAsString = mockMvcResult.andReturn().getResponse().getContentAsString();
        JSONArray sales = new JSONArray(contentAsString);
        Double totalValue = 0.00;
        for (int i = 0; i < sales.length(); i++) {
            JSONObject sale = sales.getJSONObject(i);
            double price = sale.getDouble("totalPurchaseValue");
            totalValue += price;
        }

        Assertions.assertEquals(expectedTotalValue, totalValue);
    }

    //AC5

    @When("I request a sales report from June to August with monthly granularity")
    public void i_request_a_sales_report_from_june_to_august_with_monthly_granularity() throws Exception {
        mockMvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get("/businesses/{id}/sales", business.getId())
                .param("periodStart", LocalDate.of(2021, 6, 1).toString())
                .param("periodEnd", LocalDate.of(2021, 8, 31).toString())
                .param("granularity", "monthly")
                .with(user(new AppUserDetails(owner)))
        );
    }

    @Then("The report shows total numbers of {int} for June, {int} for July and {int} for August")
    public void the_report_shows_total_numbers_of_for_june_for_july_and_for_august(Integer juneSalesCount,
                                                                                   Integer julySalesCount,
                                                                                   Integer augSalesCount) throws UnsupportedEncodingException, JSONException {
        String contentAsString = mockMvcResult.andReturn().getResponse().getContentAsString();
        JSONArray sales = new JSONArray(contentAsString); //TODO: java.lang.String cannot be converted to JSONArray

        Assertions.assertEquals(juneSalesCount, sales.getJSONObject(0).getInt("purchaseCount"));
        Assertions.assertEquals(julySalesCount, sales.getJSONObject(1).getInt("purchaseCount"));
        Assertions.assertEquals(augSalesCount, sales.getJSONObject(2).getInt("purchaseCount"));
    }

    @Given("I had ${double} worth of sales in June, ${double} worth of sales in July and ${double} worth of sales in August")
    public void i_had_$_worth_of_sales_in_june_$_worth_of_sales_in_july_and_$_worth_of_sales_in_august(Double juneTotalValue,
                                                                                                       Double julyTotalValue,
                                                                                                       Double augTotalValue) {
        Sale sale1 = new Sale(saleListing);
        sale1.setDateSold(LocalDateTime.of(2021, 6, 4, 14, 30));
        sale1.setPrice(juneTotalValue);
        saleHistoryRepository.save(sale1);

        Sale sale2 = new Sale(saleListing);
        sale2.setDateSold(LocalDateTime.of(2021, 7, 5, 14, 30));
        sale2.setPrice(julyTotalValue);
        saleHistoryRepository.save(sale2);

        Sale sale3 = new Sale(saleListing);
        sale3.setDateSold(LocalDateTime.of(2021, 8, 5, 14, 30));
        sale3.setPrice(augTotalValue);
        saleHistoryRepository.save(sale3);

    }

    @Then("The report shows total values of ${double} for June, ${double} for July and ${double} for August")
    public void the_report_shows_total_values_of_$_for_june_$_for_july_and_$_for_august(Double juneTotalValue,
                                                                                        Double julyTotalValue,
                                                                                        Double augTotalValue) throws UnsupportedEncodingException, JSONException {

        String contentAsString = mockMvcResult.andReturn().getResponse().getContentAsString();
        JSONArray sales = new JSONArray(contentAsString);

        Assertions.assertEquals(juneTotalValue, sales.getJSONObject(0).getInt("totalPurchaseValue"));
        Assertions.assertEquals(julyTotalValue, sales.getJSONObject(1).getInt("totalPurchaseValue"));
        Assertions.assertEquals(augTotalValue, sales.getJSONObject(2).getInt("totalPurchaseValue"));
    }

}
