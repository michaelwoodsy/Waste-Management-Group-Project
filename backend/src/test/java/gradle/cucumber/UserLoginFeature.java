package gradle.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class UserLoginFeature {
    @Given("An account exists with the email {string}")
    public void an_account_exists_with_the_email(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I try to register with the email {string}")
    public void i_try_to_register_with_the_email(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("The registration is unsuccessful")
    public void the_registration_is_unsuccessful() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }


    @Given("I have created an account with the username {string} and the password {string}")
    public void i_have_created_an_account_with_the_username_and_the_password(String username, String password) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I log back into my profile with the username {string} and the password {string}")
    public void i_log_back_into_my_profile_with_the_username_and_the_password(String username, String password) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("The login is successful")
    public void the_login_is_successful() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

}
