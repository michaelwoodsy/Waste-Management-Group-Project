package gradle.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.seng302.project.controller.UserController;
import org.seng302.project.exceptions.ExistingRegisteredEmailException;
import org.seng302.project.exceptions.InvalidLoginException;
import org.seng302.project.model.Address;
import org.seng302.project.model.LoginCredentials;
import org.seng302.project.model.User;
import org.springframework.beans.factory.annotation.Autowired;


public class LogInSteps {

    @Autowired
    private UserController userController;

    private Integer loginExceptionCount = 0;

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

        try {
            userController.createUser(createdUser);
        } catch (ExistingRegisteredEmailException existingRegisteredEmailException) {
            System.out.println("Account already registered");
        }
    }

    @When("I try to log-in with username {string} and incorrect password {string}")
    public void i_try_to_log_in_with_username_and_incorrect_password(String username, String password) {
        try {
            LoginCredentials login = new LoginCredentials(username, password);
            userController.authenticate(login);
        } catch (InvalidLoginException loginException) {
            loginExceptionCount += 1;
        }
    }

    @Then("Log-in is unsuccessful and the system generates an error message")
    public void log_in_is_unsuccessful_and_the_system_generates_an_error_message() {
        Assertions.assertEquals(1, loginExceptionCount);
    }

    @When("I try to log-in with correct username {string} and password {string}")
    public void i_try_to_log_in_with_correct_username_and_password(String username, String password) {
        try {
            LoginCredentials login = new LoginCredentials(username, password);
            userController.authenticate(login);
        } catch (InvalidLoginException loginException) {
            loginExceptionCount += 1;
        }
    }

    @Then("The log-in is successful")
    public void the_log_in_is_successful() {
        Assertions.assertEquals(0, loginExceptionCount);
    }

}
