package gradle.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.seng302.project.controller.UserController;
import org.seng302.project.model.Address;
import org.seng302.project.model.User;
import org.springframework.beans.factory.annotation.Autowired;

public class LogInSteps {

    @Autowired
    private UserController userController;

    @Given("I have created an account with the username {string} and the password {string}")
    public void i_have_created_an_account_with_the_username_and_the_password(String username, String password) {
        String firstName = "Test";
        String lastName = "User";
        String dateOfBirth = "2001-02-15";

        Address homeAddress = new Address(
                "", "", "", "", "New Zealand", "");

        User createdUser = new User(
                firstName, lastName, "", "","", username,
                dateOfBirth, "", homeAddress, password);

        userController.createUser(createdUser);
    }

    @When("I try to log-in with username {string} and incorrect password {string}")
    public void i_try_to_log_in_with_username_and_incorrect_password(String username, String password) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("Log-in is unsuccessful and the system generates an error message")
    public void log_in_is_unsuccessful_and_the_system_generates_an_error_message() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I try to log-in with correct username {string} and password {string}")
    public void i_try_to_log_in_with_correct_username_and_password(String username, String password) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("The log-in is successful")
    public void the_log_in_is_successful() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

}
