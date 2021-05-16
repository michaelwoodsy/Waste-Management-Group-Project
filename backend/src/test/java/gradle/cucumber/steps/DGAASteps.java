package gradle.cucumber.steps;


import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.seng302.project.controller.UserController;
import org.seng302.project.controller.authentication.AppUserDetails;
import org.seng302.project.model.*;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;



public class DGAASteps {

    private User testUser;
    private Integer testUserId;
    private Address testAddress;

    private final UserController userController;

    private final UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private final AddressRepository addressRepository;


    private String dgaaEmail = "admin";
    private String dgaaPassword = "password";
    private User testDGAA;


    @Autowired
    public DGAASteps(UserRepository userRepository,
                             AddressRepository addressRepository,
                     UserController userController) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.userController = userController;
    }


    @BeforeEach
    public void setup() {
        if (System.getenv("DGAA_EMAIL") != null) {
            dgaaEmail = System.getenv("DGAA_EMAIL");
        }
        if (System.getenv("DGAA_PASSWORD") != null) {
            dgaaPassword = System.getenv("DGAA_PASSWORD");
        }

        testDGAA = new User("Admin", "Admin", "", "", "", dgaaEmail,
                "2000-05-21", "+64 3 555 0129", null, dgaaPassword);
        testAddress = new Address("", "", "", "", "New Zealand","");
        addressRepository.save(testAddress);
        testDGAA.setPassword(passwordEncoder.encode(testDGAA.getPassword()));
        testDGAA = userRepository.save(testDGAA);
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
    public void the_dgaa_assigns_admin_rights_to_the_user() {

        //TODO: this throws unexpected exception in controller: null
        // Can't get past this line in controller:
        // User requestMaker = userRepository.findByEmail(appUser.getUsername()).get(0);

        userController.dgaaMakeAdmin(testUserId, new AppUserDetails(testDGAA));
    }

    @Then("The role of the user is updated to GAA")
    public void the_role_of_the_user_is_updated_to_gaa() {

        //TODO: role is still user :(
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
    public void the_dgaa_removes_admin_rights_from_the_user() {
        userController.dgaaRevokeAdmin(testUserId, new AppUserDetails(testDGAA));
    }

    @Then("The role of the user is updated to user")
    public void the_role_of_the_user_is_updated_to_user() {
        User retrievedUser = userRepository.findByEmail(testUser.getEmail()).get(0);
        Assertions.assertEquals("user", retrievedUser.getRole());
    }

}
