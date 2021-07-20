package gradle.cucumber.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ModifyingBusinessesSteps {

    @Given("I am the administrator of a business account")
    public void i_am_the_administrator_of_a_business_account() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I try to edit the business details to:")
    public void i_try_to_edit_the_business_details_to(DataTable dataTable) {
        // Write code here that turns the phrase above into concrete actions
        // For automatic transformation, change DataTable to one of
        // E, List<E>, List<List<E>>, List<Map<K,V>>, Map<K,V> or
        // Map<K, List<V>>. E,K,V must be a String, Integer, Float,
        // Double, Byte, Short, Long, BigInteger or BigDecimal.
        //
        // For other transformations you can register a DataTableType.
        throw new io.cucumber.java.PendingException();
    }

    @Then("The business details are updated")
    public void the_business_details_are_updated() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("A {int} status is returned to the user")
    public void a_status_is_returned_to_the_user(Integer statusCode) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I upload a new image for the business")
    public void i_upload_a_new_image_for_the_business() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("The image is saved to the repository for the business")
    public void the_image_is_saved_to_the_repository_for_the_business() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Given("The business has at least {int} images, the first being the primary image")
    public void the_business_has_at_least_images_the_first_being_the_primary_image(Integer numImages) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I change the primary image to be the second image")
    public void i_change_the_primary_image_to_be_the_second_image() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("The business' primary image is the second image")
    public void the_business_primary_image_is_the_second_image() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("A thumbnail of the new primary image is created")
    public void a_thumbnail_of_the_new_primary_image_is_created() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

}