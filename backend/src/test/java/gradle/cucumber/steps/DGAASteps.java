package gradle.cucumber.steps;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.Mockito;
import org.seng302.project.model.DGAAChecker;
import org.springframework.boot.SpringApplication;


public class DGAASteps {

    public DGAAChecker mockDGAAChecker;
    public SpringApplication mockSpringApplication;

    @Before
    public void setup() {
        mockDGAAChecker = Mockito.mock(DGAAChecker.class);
        mockSpringApplication = Mockito.mock(SpringApplication.class);
    }

    //AC1
    @Given("The application is running")
    public void the_application_is_running() {
        mockSpringApplication.run();
    }

    @When("The application is \\(re-) started")
    public void the_application_is_re_started() {
        mockSpringApplication.run();
    }

    @Then("A check is run for the existence of the DGAA")
    public void a_check_is_run_for_the_existence_of_the_dgaa() {
        //TODO: Actually, there were zero interactions with this mock.
        // Even when this line was put in above method
        Mockito.verify(mockDGAAChecker, Mockito.times(1)).dgaaExists();
    }


//    //AC2
//
//    @When("A period of time has passed")
//    public void a_period_of_time_has_passed() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    //AC3
//
//    @Given("A DGAA does not exist")
//    public void a_dgaa_does_not_exist() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    @When("The existence of the DGAA is checked for")
//    public void the_existence_of_the_dgaa_is_checked_for() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    @Then("A DGAA is created automatically")
//    public void a_dgaa_is_created_automatically() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//    @Then("An entry is added into the error log")
//    public void an_entry_is_added_into_the_error_log() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
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
//    @Given("There exists a user with the first name {string} and last name {string}")
//    public void there_exists_a_user_with_the_first_name_and_last_name(String fName, String lName) {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
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
//
//    //AC6
//
//    @When("The DGAA assigns admin rights to the user with the first name {string} and last name {string}")
//    public void the_dgaa_assigns_admin_rights_to_the_user_with_the_first_name_and_last_name(String fName, String lName) {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//    @Then("The role of the user is updated to GAA")
//    public void the_role_of_the_user_is_updated_to_gaa() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    //AC7
//
//    @Given("There exists a user with a role of GAA")
//    public void there_exists_a_user_with_a_role_of_gaa() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    @When("The DGAA removes admin rights from the user")
//    public void the_dgaa_removes_admin_rights_from_the_user() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    @Then("The role of the user is updated to user")
//    public void the_role_of_the_user_is_updated_to_user() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    //AC8
//
//    @Given("There exists a business with the name {string}")
//    public void there_exists_a_business_with_the_name(String businessName) {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    @When("A DGAA assigns admin rights to the business")
//    public void a_dgaa_assigns_admin_rights_to_the_business() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    @Then("An exception is thrown")
//    public void an_exception_is_thrown() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }

}
