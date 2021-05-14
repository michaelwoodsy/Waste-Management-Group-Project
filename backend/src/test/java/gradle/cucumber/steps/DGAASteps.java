package gradle.cucumber.steps;


import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.seng302.project.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;


@AutoConfigureMockMvc
public class DGAASteps {

    private User testUser;
    private Integer testUserId;
    private Address testAddress;
    private Business testBusiness;
    private Integer testBusinessId;


    private final UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private final AddressRepository addressRepository;

    private final BusinessRepository businessRepository;

    private String dgaaEmail = "admin";
    private String dgaaPassword = "password";

    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    private RequestBuilder putAdminRequest;

    @Autowired
    public DGAASteps(UserRepository userRepository,
                             AddressRepository addressRepository,
                     BusinessRepository businessRepository) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.businessRepository = businessRepository;
    }


    @BeforeEach
    public void setup() {
        if (System.getenv("DGAA_EMAIL") != null) {
            dgaaEmail = System.getenv("DGAA_EMAIL");
        }
        if (System.getenv("DGAA_PASSWORD") != null) {
            dgaaPassword = System.getenv("DGAA_PASSWORD");
        }
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

//    //AC3
//
//    @Given("The DGAA has predefined username {string} and password {string}")
//    public void the_dgaa_has_predefined_username_and_password(String username, String password) {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    @When("The DGAA username and password are changed to {string} and {string}")
//    public void the_dgaa_username_and_password_are_changed_to_and(String newUsername, String newPassword) {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//    @Then("The DGAA username and password are changed in the database")
//    public void the_dgaa_username_and_password_are_changed_in_the_database() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    //AC4
//
    @Given("There exists a user with the first name {string} and last name {string}")
    public void there_exists_a_user_with_the_first_name_and_last_name(String fName, String lName) {
        testUser = new User(fName, lName, "", "", "", "bob.beans@gmail.com",
                "2000-05-21", "+64 3 555 0129", null, "1337-H%nt3r2");
        testAddress = new Address("", "", "", "", "New Zealand","");
        addressRepository.save(testAddress);
        testUser.setPassword(passwordEncoder.encode(testUser.getPassword()));
        testUserId = userRepository.save(testUser).getId();
    }
//
//    @When("The DGAA searches with the search string {string}")
//    public void the_dgaa_searches_with_the_search_string(String searchString) {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//    @Then("The user with the first name {string} and last name {string} is returned to the DGAA")
//    public void the_user_with_the_first_name_and_last_name_is_returned_to_the_dgaa(String fName, String lName) {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }

    //AC6

    @When("The DGAA assigns admin rights to the user")
    public void the_dgaa_assigns_admin_rights_to_the_user() throws Exception {

        RequestBuilder putAdminRequest = MockMvcRequestBuilders
                .put("/users/{id}/makeadmin", testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic(dgaaEmail, dgaaPassword));

        this.mockMvc.perform(putAdminRequest)
                .andExpect(MockMvcResultMatchers.status().isOk()) // We expect a 200 response
                .andReturn();

    }
    @Then("The role of the user is updated to GAA")
    public void the_role_of_the_user_is_updated_to_gaa() {
        User retrievedUser = userRepository.findByEmail(testUser.getEmail()).get(0);
        Assertions.assertEquals("globalApplicationAdmin", retrievedUser.getRole());
    }

    //AC7

    @Given("There exists a user with a role of GAA")
    public void there_exists_a_user_with_a_role_of_gaa() {
        testUser = new User("Bob", "Beans", "", "", "", "bob.beans@gmail.com",
                "2000-05-21", "+64 3 555 0129", null, "1337-H%nt3r2");
        testAddress = new Address("", "", "", "", "New Zealand","");
        testUser.setRole("globalApplicationAdmin");
        addressRepository.save(testAddress);
        testUser.setPassword(passwordEncoder.encode(testUser.getPassword()));
        testUserId = userRepository.save(testUser).getId();
    }

    @When("The DGAA removes admin rights from the user")
    public void the_dgaa_removes_admin_rights_from_the_user() throws Exception {
        RequestBuilder putAdminRequest = MockMvcRequestBuilders
                .put("/users/{id}/revokeadmin", testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic(dgaaEmail, dgaaPassword));

        this.mockMvc.perform(putAdminRequest)
                .andExpect(MockMvcResultMatchers.status().isOk()) // We expect a 200 response
                .andReturn();
    }

    @Then("The role of the user is updated to user")
    public void the_role_of_the_user_is_updated_to_user() {
        User retrievedUser = userRepository.findByEmail(testUser.getEmail()).get(0);
        Assertions.assertEquals("user", retrievedUser.getRole());
    }

    //AC8

    @Given("There exists a business with the name {string}")
    public void there_exists_a_business_with_the_name(String businessName) {
        testBusiness = new Business(businessName, "Test business", testAddress, "Retail Trade", 1);
        testBusinessId = businessRepository.save(testBusiness).getId();
    }

    @When("A DGAA assigns admin rights to the business")
    public void a_dgaa_assigns_admin_rights_to_the_business() {
        putAdminRequest = MockMvcRequestBuilders
                .put("/businesses/{id}/makeadmin", testBusinessId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic(dgaaEmail, dgaaPassword));

    }

    @Then("An exception is thrown")
    public void an_exception_is_thrown() throws Exception {
        this.mockMvc.perform(putAdminRequest)
                .andExpect(MockMvcResultMatchers.status().isNotFound()) // We expect a 404 response
                .andReturn();
    }
}
