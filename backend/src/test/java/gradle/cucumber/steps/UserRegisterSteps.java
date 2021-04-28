package gradle.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.seng302.project.controller.UserController;
import org.seng302.project.model.Address;
import org.seng302.project.model.User;
import org.seng302.project.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class UserRegisterSteps {

//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private UserController userController;
//
//    @Given("I am user trying to register a new User")
//    public void i_am_user_trying_to_register_a_new_user() {
//        userRepository.deleteAll();
//    }
//
//    @When("I try to create the account with details:")
//    public void i_try_to_create_the_account_with_details(io.cucumber.datatable.DataTable dataTable) {
//        Map userMap = dataTable.asMap();
//        // |username       | password | firstName | lastName | dateOfBirth | homeAddress
//        String username = dataTable.cell(1, 0);
//        String password = dataTable.cell(1, 1);
//        String firstName = dataTable.cell(1, 2);
//        String lastName = dataTable.cell(1, 3);
//        String dateOfBirth = dataTable.cell(1, 4);
//        Address homeAddress = new Address();
//
//        userController.createUser(new User(username, password))
//    }
//    @Then("The account is created.")
//    public void the_account_is_created() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//    @Then("The userId is returned.")
//    public void the_user_id_is_returned() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
}
