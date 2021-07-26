package gradle.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.seng302.project.repositoryLayer.model.Address;
import org.seng302.project.repositoryLayer.model.Business;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.AddressRepository;
import org.seng302.project.repositoryLayer.repository.BusinessRepository;
import org.seng302.project.serviceLayer.dto.address.AddressDTO;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;


/**
 * Steps for the Cucumber tests relating to U23 Search for business
 */
public class BusinessSearchSteps {

    private MockMvc mockMvc;

    private final BusinessRepository businessRepository;
    private final AddressRepository addressRepository;

    private User testUser;
    private Business testBusiness1;
    private Business testBusiness2;

    private RequestBuilder searchBusinessRequest;


    @Autowired
    public BusinessSearchSteps(BusinessRepository businessRepository, AddressRepository addressRepository) {
        this.businessRepository =  businessRepository;
        this.addressRepository = addressRepository;
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

        businessRepository.deleteAll();
    }

    //AC2
    @Given("A business exists with the name {string}")
    public void a_business_exists_with_the_name(String businessName) {
        Address address = new Address();
        address.setCountry("New Zealand");
        addressRepository.save(address);

        testBusiness1 = new Business(businessName, "description", address, "Retail Trade", 1);
        businessRepository.save(testBusiness1);
    }

    @When("I enter {string} as a search term")
    public void i_enter_as_a_search_term(String searchTerm) {
        searchBusinessRequest = MockMvcRequestBuilders
                .get("/businesses/search?searchQuery={searchQuery}&pageNumber=0&sortBy=", searchTerm)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));
    }

    @Then("The business called {string} is in the search results")
    public void the_business_called_is_in_the_search_results(String businessName) throws Exception {
        MvcResult searchResult = this.mockMvc.perform(searchBusinessRequest)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        //Convert result into string
        String searchResultString = searchResult.getResponse().getContentAsString();
        //Convert result string into JSONArray
        JSONArray searchResultArray = new JSONArray(searchResultString);
        //Get the list of businesses in the result array
        JSONArray businessesArray = searchResultArray.getJSONArray(0);
        //Get the first business in the businessesArray
        JSONObject business = businessesArray.getJSONObject(0);

        Assertions.assertEquals(businessName, business.getString("name"));
    }

    @Then("The business called {string} is NOT in the search results")
    public void the_business_called_is_not_in_the_search_results(String businessName) throws Exception {
        MvcResult searchResult = this.mockMvc.perform(searchBusinessRequest)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        //Convert result into string
        String searchResultString = searchResult.getResponse().getContentAsString();
        //Convert result string into JSONArray
        JSONArray searchResultArray = new JSONArray(searchResultString);
        //Get the list of businesses in the result array
        JSONArray businessesArray = searchResultArray.getJSONArray(0);

        Assertions.assertEquals(0, businessesArray.length());
    }

    @Given("A business exists with the name {string} and a business exists with the name {string}")
    public void a_business_exists_with_the_name_and_a_business_exists_with_the_name(String businessName1, String businessName2) {
        Address address = new Address();
        address.setCountry("New Zealand");
        addressRepository.save(address);

        testBusiness1 = new Business(businessName1, "description", address, "Retail Trade", 1);
        businessRepository.save(testBusiness1);
        testBusiness2 = new Business(businessName2, "description", address, "Retail Trade", 1);
        businessRepository.save(testBusiness2);
    }

    @When("I enter \"\"Cara's Cookies\"\" as a search term")
    public void i_enter_cara_s_cookies_as_a_search_term() {
        searchBusinessRequest = MockMvcRequestBuilders
                .get("/businesses/search?searchQuery={searchQuery}&pageNumber=0&sortBy=", "\"Cara's Cookies\"")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

    }

    @Then("The business called {string} is the ONLY search result")
    public void the_business_called_is_the_only_search_result(String businessName1) throws Exception {
        MvcResult searchResult = this.mockMvc.perform(searchBusinessRequest)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        //Convert result into string
        String searchResultString = searchResult.getResponse().getContentAsString();
        //Convert result string into JSONArray
        JSONArray searchResultArray = new JSONArray(searchResultString);
        //Get the list of businesses in the result array
        JSONArray businessesArray = searchResultArray.getJSONArray(0);
        //Get the first business in the businessesArray
        JSONObject business = businessesArray.getJSONObject(0);

        Assertions.assertEquals(1, businessesArray.length());
        Assertions.assertEquals(businessName1, business.getString("name"));
    }

    //AC4

    @Given("Two businesses exist with the type {string}")
    public void two_businesses_exist_with_the_type(String businessType) {
        Address address = new Address();
        address.setCountry("New Zealand");
        addressRepository.save(address);

        testBusiness1 = new Business("Business1", "description", address, businessType, 1);
        businessRepository.save(testBusiness1);
        testBusiness2 = new Business("Business2", "description", address, businessType, 1);
        businessRepository.save(testBusiness2);
    }

    @When("I search by the business type {string}")
    public void i_search_by_the_business_type(String businessType) {
        searchBusinessRequest = MockMvcRequestBuilders
                .get("/businesses/search?searchQuery={searchQuery}&businessType={businessType}&pageNumber=0&sortBy=", "Business", businessType)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));
    }

    @Then("The two businesses are the ONLY search results")
    public void the_two_businesses_are_the_only_search_results() throws Exception {
        MvcResult searchResult = this.mockMvc.perform(searchBusinessRequest)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        //Convert result into string
        String searchResultString = searchResult.getResponse().getContentAsString();
        //Convert result string into JSONArray
        JSONArray searchResultArray = new JSONArray(searchResultString);
        //Get the list of businesses in the result array
        JSONArray businessesArray = searchResultArray.getJSONArray(0);
        //Get the businesses in the businessesArray
        JSONObject firstBusiness = businessesArray.getJSONObject(0);
        JSONObject secondBusiness = businessesArray.getJSONObject(1);


        Assertions.assertEquals(2, businessesArray.length());
        Assertions.assertEquals(testBusiness1.getName(), firstBusiness.getString("name"));
        Assertions.assertEquals(testBusiness2.getName(), secondBusiness.getString("name"));
    }

    @Given("Two businesses, with the names {string} and {string} exist with the type {string}")
    public void two_businesses_with_the_names_and_exist_with_the_type(String businessName1, String businessName2,
                                                                      String businessType) {
        Address address = new Address();
        address.setCountry("New Zealand");
        addressRepository.save(address);

        testBusiness1 = new Business(businessName1, "description", address, businessType, 1);
        businessRepository.save(testBusiness1);
        testBusiness2 = new Business(businessName2, "description", address, businessType, 1);
        businessRepository.save(testBusiness2);
    }

    @When("I search by the business type {string} and the search term {string}")
    public void i_search_by_the_business_type_and_the_search_term(String businessType, String searchTerm) {
        searchBusinessRequest = MockMvcRequestBuilders
                .get("/businesses/search?searchQuery={searchQuery}&businessType={businessType}&pageNumber=0&sortBy=", searchTerm, businessType)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));
    }

}
