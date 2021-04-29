package gradle.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.seng302.project.controller.UserController;
import org.seng302.project.model.Address;
import org.seng302.project.model.AddressRepository;
import org.seng302.project.model.User;
import org.seng302.project.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class UserRegisterSteps {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserController userController;

    @Autowired
    private AddressRepository addressRepository;

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
        userRepository.deleteAll();
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

        //TODO: this does work and creates new user but need to assert/test this somehow,
        // maybe in next function
        userController.createUser(createdUser);

    }
    @Then("The account with email {string} is created.")
    public void the_account_with_email_is_created(String email) {
        //TODO: this returns empty list
        List<User> retrievedUsers = userRepository.findByEmail(email);
        Assertions.assertEquals(1, retrievedUsers.size());
    }


//TODO: maybe rewrite this scenario to:
// Given  A user already exists with the email "tom@gmail.com"
// When  I try to register as a new user with the existing email
// Then  An error message is returned...

    @Given("I am trying to register as a new user with an already taken email:")
    public void i_am_trying_to_register_as_a_new_user_with_an_already_taken_email(io.cucumber.datatable.DataTable dataTable) {
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


        createExistingUser(email);

        //TODO: this does throw
        // ExistingRegisteredEmailException: email address is already registered.
        // Need to catch this/assert this is thrown
        userController.createUser(createdUser);

    }
}
