package gradle.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.seng302.project.service_layer.dto.address.AddressDTO;
import org.seng302.project.service_layer.dto.user.PostUserDTO;
import org.seng302.project.service_layer.dto.user.LoginCredentialsDTO;
import org.seng302.project.web_layer.controller.UserController;
import org.seng302.project.service_layer.exceptions.register.ExistingRegisteredEmailException;
import org.seng302.project.service_layer.exceptions.InvalidLoginException;
import org.springframework.beans.factory.annotation.Autowired;

public class LogInSteps {

    private final UserController userController;
    private Integer loginExceptionCount = 0;

    @Autowired
    public LogInSteps(UserController userController) {
        this.userController = userController;
    }

    @Given("I have created an account with the username {string} and the password {string}")
    public void i_have_created_an_account_with_the_username_and_the_password(String username, String password) {
        String firstName = "Test";
        String lastName = "User";
        String dateOfBirth = "2001-02-15";

        AddressDTO homeAddress = new AddressDTO(
                "", "", "", "", "New Zealand", "");

        PostUserDTO postUserDTO = new PostUserDTO(
                firstName, lastName, "", "","", username,
                dateOfBirth, "", homeAddress, password);

        try {
            userController.createUser(postUserDTO);
        } catch (ExistingRegisteredEmailException existingRegisteredEmailException) {
            System.out.println("Account already registered");
        }
    }

    @When("I try to log-in with username {string} and incorrect password {string}")
    public void i_try_to_log_in_with_username_and_incorrect_password(String username, String password) {
        try {
            LoginCredentialsDTO loginCredentials = new LoginCredentialsDTO(username, password);
            userController.authenticate(loginCredentials);
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
            LoginCredentialsDTO loginCredentials = new LoginCredentialsDTO(username, password);
            userController.authenticate(loginCredentials);
        } catch (InvalidLoginException loginException) {
            loginExceptionCount += 1;
        }
    }

    @Then("The log-in is successful")
    public void the_log_in_is_successful() {
        Assertions.assertEquals(0, loginExceptionCount);
    }

}
