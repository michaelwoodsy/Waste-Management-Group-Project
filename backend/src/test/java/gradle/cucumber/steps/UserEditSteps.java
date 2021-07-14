package gradle.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class UserEditSteps {



    //AC1: As a registered individual, I can update any of my attributes.

    @Given("I am logged in as a user")
    public void iAmLoggedInAsAUser() {

    }


    @When("I try to edit my account to the details:")
    public void iTryToEditMyAccountToTheDetails(io.cucumber.datatable.DataTable dataTable) {

    }

    @Then("My details are updated.")
    public void myDetailsAreUpdated() {
    }

    
    //AC2: All validation rules still apply. For example, I can only modify my date of birth if I still remain over the required age.

    @When("I try to edit my date of birth to {int} years ago")
    public void iTryToEditMyDateOfBirthToYearsAgo(int arg0) {
    }

    @Then("An error message is returned to say the date of birth is too young.")
    public void anErrorMessageIsReturnedToSayTheDateOfBirthIsTooYoung() {
    }


    //AC3: Mandatory attributes still remain mandatory.

    @When("I try to edit my account and dont enter in a first name")
    public void iTryToEditMyAccountAndDontEnterInAFirstName() {
    }

    @Then("An error message is shown saying the first name is required.")
    public void anErrorMessageIsShownSayingTheFirstNameIsRequired() {
    }
}
