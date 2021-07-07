package gradle.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.seng302.project.repositoryLayer.model.*;
import org.seng302.project.repositoryLayer.repository.AddressRepository;
import org.seng302.project.repositoryLayer.repository.CardRepository;
import org.seng302.project.repositoryLayer.repository.MessageRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.webLayer.controller.UserController;
import org.seng302.project.serviceLayer.exceptions.register.ExistingRegisteredEmailException;
import org.seng302.project.serviceLayer.exceptions.register.InvalidEmailException;
import org.seng302.project.serviceLayer.exceptions.RequiredFieldsMissingException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class UserRegisterSteps {

    private final UserRepository userRepository;
    private final UserController userController;
    private final AddressRepository addressRepository;
    private final CardRepository cardRepository;
    private final MessageRepository messageRepository;
    private Integer existingRegisteredEmailExceptionCount = 0;
    private Integer requiredFieldsMissingExceptionCount = 0;
    private Integer invalidEmailExceptionCount = 0;

    @Autowired
    public UserRegisterSteps(UserRepository userRepository,
                             UserController userController,
                             AddressRepository addressRepository,
                             CardRepository cardRepository,
                             MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.userController = userController;
        this.addressRepository = addressRepository;
        this.cardRepository = cardRepository;
        this.messageRepository = messageRepository;
    }

    /**
     * Creates the existing test user. Called by
     * i_am_a_user_trying_to_register_a_new_user_with_an_already_taken_email
     * before that function tried recreating the user
     */
    public void createExistingUser(String email) {
        Address homeAddress = new Address(
                "", "", "", "", "New Zealand", "");

        addressRepository.save(homeAddress);

        User existingUser = new User(
                "Tom", "Rizzi", "", "","", email,
                "2001-02-15", "", homeAddress, "Ab123456");

        userRepository.save(existingUser);
    }

    @Given("I am trying to register as a new User")
    public void i_am_trying_to_register_as_a_new_user() {
        messageRepository.deleteAll();
        cardRepository.deleteAll();
        userRepository.deleteAll();
        addressRepository.deleteAll();
    }

    @When("I try to create the account with details:")
    public void i_try_to_create_the_account_with_details(io.cucumber.datatable.DataTable dataTable) {
        //Based off example with header and single row from:
        // https://javapointers.com/automation/cucumber/cucumber-data-tables-example-in-java/
        List<Map<String, String>> userMap = dataTable.asMaps(String.class, String.class);
        // |email       | password | firstName | lastName | dateOfBirth | homeAddress
        String email = userMap.get(0).get("email");
        String password = userMap.get(0).get("password");
        String firstName = userMap.get(0).get("firstName");
        String lastName = userMap.get(0).get("lastName");

        String dateOfBirth = userMap.get(0).get("dateOfBirth");

        //TODO: get this to use country from dataTable
        Address homeAddress = new Address(
                "", "", "", "", "New Zealand", "");

        User createdUser = new User(
                firstName, lastName, "", "","", email,
                dateOfBirth, "", homeAddress, password);

        userController.createUser(createdUser);

    }

    @Then("The account with email {string} is created.")
    public void the_account_with_email_is_created(String email) {
        List<User> retrievedUsers = userRepository.findByEmail(email);
        Assertions.assertEquals(1, retrievedUsers.size());
    }

    @Given("There a user who already is registered with the email {string}")
    public void there_a_user_who_already_is_registered_with_the_email(String email) {
        userRepository.deleteAll();
        addressRepository.deleteAll();
        createExistingUser(email);
    }

    @When("I try to create an account with email {string}:")
    public void i_try_to_create_an_account_with_email(String email) {
        String password = "Password123";
        String firstName = "Test";
        String lastName = "User";
        String dateOfBirth = "2001-02-15";

        //TODO: get this to use country from dataTable
        Address homeAddress = new Address(
                "", "", "", "", "New Zealand", "");

        User createdUser = new User(
                firstName, lastName, "", "","", email,
                dateOfBirth, "", homeAddress, password);

        try {
            userController.createUser(createdUser);
        } catch (ExistingRegisteredEmailException e) {
            existingRegisteredEmailExceptionCount += 1;
        }
    }

    @Then("An error message is returned to say the email is already taken.")
    public void an_error_message_is_returned_to_say_the_email_is_already_taken() {
        Assertions.assertEquals(1, existingRegisteredEmailExceptionCount);
    }

    @When("I try to create an account without a password")
    public void i_try_to_create_an_account_without_a_password() {
        String firstName = "Test";
        String lastName = "User";
        String dateOfBirth = "2001-02-15";
        String email = "testemail@gmail.com";

        //TODO: get this to use country from dataTable
        Address homeAddress = new Address(
                "", "", "", "", "New Zealand", "");

        User createdUser = new User(
                firstName, lastName, "", "","", email,
                dateOfBirth, "", homeAddress, "");
        try {
            userController.createUser(createdUser);
        } catch (RequiredFieldsMissingException e) {
            requiredFieldsMissingExceptionCount += 1;
        }
    }

    @Then("An error message is shown saying the password is required.")
    public void an_error_message_is_shown_saying_the_password_is_required() {
        Assertions.assertEquals(1, requiredFieldsMissingExceptionCount);
    }

    @When("I try to create an account with an invalid email")
    public void i_try_to_create_an_account_with_an_invalid_email() {
        String password = "Password123";
        String firstName = "Test";
        String lastName = "User";
        String dateOfBirth = "2001-02-15";
        String email = "testemailgmail.com";

        //TODO: get this to use country from dataTable
        Address homeAddress = new Address(
                "", "", "", "", "New Zealand", "");

        User createdUser = new User(
                firstName, lastName, "", "","", email,
                dateOfBirth, "", homeAddress, password);

        try {
            userController.createUser(createdUser);
        } catch (InvalidEmailException e) {
            invalidEmailExceptionCount += 1;
        }
    }


    @Then("An error message is shown saying the email is invalid.")
    public void an_error_message_is_shown_saying_the_email_is_invalid() {
        Assertions.assertEquals(1, invalidEmailExceptionCount);
    }


}
