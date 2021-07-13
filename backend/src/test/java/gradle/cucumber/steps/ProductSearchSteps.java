package gradle.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repositoryLayer.model.Business;
import org.seng302.project.repositoryLayer.model.Product;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.AddressRepository;
import org.seng302.project.repositoryLayer.repository.BusinessRepository;
import org.seng302.project.repositoryLayer.repository.ProductRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

/**
 * Cucumber steps for U28 Product Search
 */
public class ProductSearchSteps extends AbstractInitializer {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final ProductRepository productRepository;

    private Business testBusiness;
    private User user;
    private User owner;

    private MockMvc mockMvc;

    private RequestBuilder searchProductRequest;

    @Autowired
    public ProductSearchSteps(AddressRepository addressRepository,
                              UserRepository userRepository,
                              BusinessRepository businessRepository,
                              ProductRepository productRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.productRepository = productRepository;

    }

    /**
     * Initialises entities from AbstractInitializer
     */
    @BeforeEach
    @Autowired
    public void setup(WebApplicationContext context) {
        productRepository.deleteAll();
        businessRepository.deleteAll();
        userRepository.deleteAll();

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        this.initialise();

        user = this.getTestUser();
        addressRepository.save(user.getHomeAddress());
        user = userRepository.save(user);

        owner = this.getTestUserBusinessAdmin();
        addressRepository.save(owner.getHomeAddress());
        owner = userRepository.save(owner);


        testBusiness = this.getTestBusiness();
        testBusiness.setPrimaryAdministratorId(owner.getId());
        addressRepository.save(testBusiness.getAddress());
        testBusiness = businessRepository.save(testBusiness);
    }


    //AC1

    @Given("A product catalogue has products with the names {string} and {string}")
    public void a_product_catalogue_has_products_with_the_names_and(String productName1, String productName2) {

        productRepository.save(new Product("PROD1", productName1, "The first product", "Us",
                null, testBusiness.getId()));
        productRepository.save(new Product("PROD2", productName2, "The second product", "Us",
                null, testBusiness.getId()));
    }

    @When("The business admin searches with the term {string} to match a product name")
    public void the_business_admin_searches_with_the_term_to_match_a_product_name(String searchTerm) throws JSONException {
        JSONObject requestBody = new JSONObject();
        requestBody.put("matchingId", false);
        requestBody.put("matchingName", true);
        requestBody.put("matchingDescription", false);
        requestBody.put("matchingManufacturer", false);

        searchProductRequest = MockMvcRequestBuilders
                .get("/businesses/{businessId}/products/search?searchQuery={searchQuery}",
                        testBusiness.getId(), searchTerm)
                .content(requestBody.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(owner)));
    }

    @Then("Only the product with the name {string} is included in the results")
    public void only_the_product_with_the_name_is_included_in_the_results(String productName) throws Exception {
        MvcResult searchResult = mockMvc.perform(searchProductRequest)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String searchResultString = searchResult.getResponse().getContentAsString();
        JSONArray searchResultArray = new JSONArray(searchResultString);
        System.out.println(searchResultArray);

        Assertions.assertEquals(1, searchResultArray.length());
        Assertions.assertEquals(productName, searchResultArray.getJSONObject(0).getString("name"));
    }

    //AC2

    @When("A user who is not the business admin tries searching the catalogue")
    public void a_user_who_is_not_the_business_admin_tries_searching_the_catalogue() throws JSONException {
        JSONObject requestBody = new JSONObject();
        requestBody.put("matchingId", false);
        requestBody.put("matchingName", true);
        requestBody.put("matchingDescription", false);
        requestBody.put("matchingManufacturer", false);

        searchProductRequest = MockMvcRequestBuilders
                .get("/businesses/{businessId}/products/search?searchQuery={searchQuery}",
                        testBusiness.getId(), "beans")
                .content(requestBody.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(user)));
    }

    @Then("The user is prevented from doing so")
    public void the_user_is_prevented_from_doing_so() throws Exception {
        mockMvc.perform(searchProductRequest)
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    //AC3
    @Then("Both products with the names {string} and {string} are included in the results")
    public void both_products_with_the_names_and_are_included_in_the_results(String productName1, String productName2)
            throws Exception {
        MvcResult searchResult = mockMvc.perform(searchProductRequest)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String searchResultString = searchResult.getResponse().getContentAsString();
        JSONArray searchResultArray = new JSONArray(searchResultString);

        Assertions.assertEquals(2, searchResultArray.length());
        Assertions.assertEquals(productName1, searchResultArray.getJSONObject(0).getString("name"));
        Assertions.assertEquals(productName2, searchResultArray.getJSONObject(1).getString("name"));
    }

    //Search term is quoted here: "Baked Beans" vs Baked Beans
    @When("The business admin searches with the term \"\"Baked Beans\"\" to match a product name")
    public void the_business_admin_searches_with_the_term_baked_beans_to_match_a_product_name() throws JSONException {
        JSONObject requestBody = new JSONObject();
        requestBody.put("matchingId", false);
        requestBody.put("matchingName", true);
        requestBody.put("matchingDescription", false);
        requestBody.put("matchingManufacturer", false);

        searchProductRequest = MockMvcRequestBuilders
                .get("/businesses/{businessId}/products/search?searchQuery={searchQuery}",
                        testBusiness.getId(), "\"Baked Beans\"")
                .content(requestBody.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(owner)));
    }

    //AC4

    @Given("A product catalogue has a product with the name {string} and a different product with the id {string}")
    public void a_product_catalogue_has_a_product_with_the_name_and_a_different_product_with_the_id(String product1Name,
                                                                                                    String product2Id) {
        productRepository.save(new Product("PROD1", product1Name, "The first product", "Us",
                null, testBusiness.getId()));
        productRepository.save(new Product(product2Id, "Product 2", "The second product", "Us",
                null, testBusiness.getId()));
    }

    @When("The business admin searches with the term {string} to match a product id")
    public void the_business_admin_searches_with_the_term_to_match_a_product_id(String searchTerm) throws JSONException {
        JSONObject requestBody = new JSONObject();
        requestBody.put("matchingId", true);
        requestBody.put("matchingName", false);
        requestBody.put("matchingDescription", false);
        requestBody.put("matchingManufacturer", false);

        searchProductRequest = MockMvcRequestBuilders
                .get("/businesses/{businessId}/products/search?searchQuery={searchQuery}",
                        testBusiness.getId(), searchTerm)
                .content(requestBody.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(owner)));// Write code here that turns the phrase above into concrete actions
    }

    @Then("Only the product with the id {string} is included in the results")
    public void only_the_product_with_the_id_is_included_in_the_results(String productId) throws Exception {
        MvcResult searchResult = mockMvc.perform(searchProductRequest)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String searchResultString = searchResult.getResponse().getContentAsString();
        JSONArray searchResultArray = new JSONArray(searchResultString);

        System.out.println(searchResultArray);

        Assertions.assertEquals(1, searchResultArray.length());
        Assertions.assertEquals(productId, searchResultArray.getJSONObject(0).getString("id"));
    }

}
