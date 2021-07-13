package gradle.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Cucumber steps for U28 Product Search
 */
public class ProductSearchSteps {

    //AC1

    @Given("A product catalogue has products with the names {string} and {string}")
    public void a_product_catalogue_has_products_with_the_names_and(String productName1, String productName2) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("The business admin searches with the term {string} to match a product name")
    public void the_business_admin_searches_with_the_term_to_match_a_product_name(String searchTerm) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("Only the product with the name {string} is included in the results")
    public void only_the_product_with_the_name_is_included_in_the_results(String productName) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    //AC2

    @When("A user who is not the business admin tries searching the catalogue")
    public void a_user_who_is_not_the_business_admin_tries_searching_the_catalogue() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("The user is prevented from doing so")
    public void the_user_is_prevented_from_doing_so() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    //AC3
    @Then("Both products with the names {string} and {string} are included in the results")
    public void both_products_with_the_names_and_are_included_in_the_results(String productName1, String productName2) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    //Search term is quoted here: "Baked Beans" vs Baked Beans
    @When("The business admin searches with the term \"\"Baked Beans\"\" to match a product name")
    public void the_business_admin_searches_with_the_term_baked_beans_to_match_a_product_name() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    //AC4

    @Given("A product catalogue has a product with the name {string} and a different product with the id {string}")
    public void a_product_catalogue_has_a_product_with_the_name_and_a_different_product_with_the_id(String product1Name,
                                                                                                    String product2Id) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("The business admin searches with the term {string} to match a product id")
    public void the_business_admin_searches_with_the_term_to_match_a_product_id(String searchTerm) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("Only the product with the id {string} is included in the results")
    public void only_the_product_with_the_id_is_included_in_the_results(String productId) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }



}
