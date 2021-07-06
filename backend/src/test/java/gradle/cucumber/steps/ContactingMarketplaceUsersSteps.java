package gradle.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ContactingMarketplaceUsersSteps {

    //AC1

    @Given("A card has been created by a user with the email {string}")
    public void a_card_has_been_created_by_a_user_with_the_email(String cardCreatorEmail) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("Another user messages about the card")
    public void another_user_messages_about_the_card() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("The message is sent to the user with the email {string}")
    public void the_message_is_sent_to_the_user_with_the_email(String cardCreatorEmail) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    //AC2

    @When("Another user messages about the card with the message {string}")
    public void another_user_messages_about_the_card_with_the_message(String message) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("The card creator receives a message with the text {string}")
    public void the_card_creator_receives_a_message_with_the_text(String message) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    //AC3

    @Then("The sender's name and card title are included in the message")
    public void the_sender_s_name_and_card_title_are_included_in_the_message() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    //AC5
    @Given("A user with the email {string} messages about the card")
    public void a_user_with_the_email_messages_about_the_card(String customerEmail) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("The card creator replies to the message")
    public void the_card_creator_replies_to_the_message() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("The user with the email {string} receives the reply")
    public void the_user_with_the_email_receives_the_reply(String customerEmail) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    //AC6

//    @When("The card creator deletes the message")
//    public void the_card_creator_deletes_the_message() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    @Then("The message is successfully removed")
//    public void the_message_is_successfully_removed() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
}
