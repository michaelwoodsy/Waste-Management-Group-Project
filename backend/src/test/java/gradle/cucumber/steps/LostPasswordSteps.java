package gradle.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Cucumber steps for the U25 Lost password story
 */
public class LostPasswordSteps {

    //AC3

    @Given("I have requested to reset my password")
    public void i_have_requested_to_reset_my_password() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I receive the password reset email message")
    public void i_receive_the_password_reset_email_message() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("The message includes a URL to the password reset screen")
    public void the_message_includes_a_url_to_the_password_reset_screen() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("The message includes instructions for using the password reset URL")
    public void the_message_includes_instructions_for_using_the_password_reset_url() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    //AC4

    @Given("I have already used the password reset URL")
    public void i_have_already_used_the_password_reset_url() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I try to use the password reset URL")
    public void i_try_to_use_the_password_reset_url() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("I am unable to reuse the URL")
    public void i_am_unable_to_reuse_the_url() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Given("An hour has passed")
    public void an_hour_has_passed() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("The URL has expired and I am unable to use it")
    public void the_url_has_expired_and_i_am_unable_to_use_it() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    //AC5
    @When("I try to reset my password to {string}")
    public void i_try_to_reset_my_password_to(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("Then I am informed it is not a valid password")
    public void then_i_am_informed_it_is_not_a_valid_password() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("My old password is deleted")
    public void my_old_password_is_deleted() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("I can log in with my new password {string}")
    public void i_can_log_in_with_my_new_password(String newPassword) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }


}
