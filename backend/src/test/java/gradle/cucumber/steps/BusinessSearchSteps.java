package gradle.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONArray;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.seng302.project.repositoryLayer.model.Business;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.BusinessRepository;
import org.seng302.project.serviceLayer.service.BusinessService;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.seng302.project.webLayer.controller.BusinessController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

/**
 * Steps for the Cucumber tests relating to U23 Search for business
 */
public class BusinessSearchSteps {

    private final BusinessController businessController;

    private MockMvc mockMvc;

    private final BusinessService businessService;

    //TODO: tests fail because businessRepository is null
    @MockBean
    private BusinessRepository businessRepository;

    private User testUser;
    private Business testBusiness1;
    private Business testBusiness2;

    private RequestBuilder searchBusinessRequest;


    @Autowired
    public BusinessSearchSteps(BusinessController businessController,
                               BusinessService businessService) {
        this.businessController = businessController;
        this.businessService = businessService;

    }

    /**
     * Set up MockMvc, and test user & businesses
     * @param context Autowired WebApplicationContext for MockMvc
     */
    @BeforeEach
    @Autowired
    public void setup(WebApplicationContext context) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        testUser = new User("Test", "User", "", "", "I'm a test user", "testuser@gmail.com",
                "2000-07-28", "123 123 1234", null, "SecurePassword789");
    }

    //AC2

    @Given("A business exists with the name {string}")
    public void a_business_exists_with_the_name(String businessName) {
        testBusiness1 = new Business(businessName, "description", null, "Retail Trade", 1);
        given(businessRepository.findAll(any(Specification.class)))
                .willReturn(List.of(testBusiness1));
    }

    @When("I enter {string} as a search term")
    public void i_enter_as_a_search_term(String searchTerm) {
        searchBusinessRequest = MockMvcRequestBuilders
                .get("/businesses/search?searchQuery={searchQuery}", searchTerm)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

    }

    @Then("The business called {string} is in the search results")
    public void the_business_called_is_in_the_search_results(String businessName) throws Exception {
        MvcResult searchResult = this.mockMvc.perform(searchBusinessRequest)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String searchResultString = searchResult.getResponse().getContentAsString();
        JSONArray searchResultArray = new JSONArray(searchResultString);

        Assertions.assertEquals(businessName, searchResultArray.getJSONObject(0).getString("name"));
    }

    @Then("The business called {string} is NOT in the search results")
    public void the_business_called_is_not_in_the_search_results(String businessName) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Given("A business exists with the name {string} and a business exists with the name {string}")
    public void a_business_exists_with_the_name_and_a_business_exists_with_the_name(String businessName1, String businessName2) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I enter \"\"Cara's Cookies\"\" as a search term")
    public void i_enter_cara_s_cookies_as_a_search_term() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("The business called {string} is the ONLY search result")
    public void the_business_called_is_the_only_search_result(String businessName1) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    //AC4

    @Given("Two businesses exist with the type {string}")
    public void two_businesses_exist_with_the_type(String businessType) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I search by the business type {string}")
    public void i_search_by_the_business_type(String businessType) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("The two businesses are the ONLY search results")
    public void the_two_businesses_are_the_only_search_results() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Given("Two businesses, with the names {string} and {string} exist with the type {string}")
    public void two_businesses_with_the_names_and_exist_with_the_type(String businessName1, String businessName2,
                                                                      String businessType) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I search by the business type {string} and the search term {string}")
    public void i_search_by_the_business_type_and_the_search_term(String businessType, String searchTerm) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

}
