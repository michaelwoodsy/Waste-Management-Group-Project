package gradle.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
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

        userController.createUser(createdUser);
    }
    @Then("The account is created.")
    public void the_account_is_created() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("The userId is returned.")
    public void the_user_id_is_returned() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
}
