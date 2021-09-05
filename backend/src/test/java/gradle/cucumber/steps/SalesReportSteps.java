package gradle.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Cucumber steps for the U41 Sales Report story
 */
public class SalesReportSteps {

    //AC2
    @Given("I had {int} sales in June, {int} sales in July and {int} sale in August")
    public void i_had_sales_in_june_sales_in_july_and_sale_in_august(Integer juneSalesCount, Integer julySalesCount,
                                                                     Integer augSalesCount) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I request a sales report for July")
    public void i_request_a_sales_report_for_july() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("The {int} sales from July are shown")
    public void the_sales_from_july_are_shown(Integer julySalesCount) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }


    @Given("I had {int} sales in {int}, {int} sales in {int} and {int} sale in {int}")
    public void i_had_sales_in_sales_in_and_sale_in(Integer salesCount1, Integer year1, Integer salesCount2,
                                                    Integer year2, Integer salesCount3, Integer year3) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I request a sales report for {int}") //year = 2020
    public void i_request_a_sales_report_for(Integer year) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("The {int} sales from {int} are shown") //salesCount = 3, year = 2020
    public void the_sales_from_are_shown(Integer salesCount, Integer year) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    //AC3

    @Given("I had sales on {string}, {string}, and {string}")
    public void i_had_sales_on_and(String date1, String date2, String date3) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I select a report from {string} to {string}")
    public void i_select_a_report_from_to(String date1, String date2) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("The {int} sales from that period are shown")
    public void the_sales_from_that_period_are_shown(Integer salesCount) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    //AC4
    @Given("In June I had one sale worth ${double} and one sale worth ${double}")
    public void in_june_i_had_one_sale_worth_$_and_one_sale_worth_$(Double salePrice1, Double salePrice2) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I request a sales report for June")
    public void i_request_a_sales_report_for_june() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("The report shows a total number of {int}")
    public void the_report_shows_a_total_number_of(Integer salesCount) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("The report shows a total value of ${double}")
    public void the_report_shows_a_total_value_of_$(Double totalValue) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    //AC5

    @When("I request a sales report from June to August with monthly granularity")
    public void i_request_a_sales_report_from_june_to_august_with_monthly_granularity() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("The report shows total numbers of {int} for June, {int} for July and {int} for August")
    public void the_report_shows_total_numbers_of_for_june_for_july_and_for_august(Integer juneSalesCount,
                                                                                   Integer julySalesCount,
                                                                                   Integer augSalesCount) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Given("I had ${double} worth of sales in June, ${double} worth of sales in July and ${double} worth of sales in August")
    public void i_had_$_worth_of_sales_in_june_$_worth_of_sales_in_july_and_$_worth_of_sales_in_august(Double juneTotalValue,
                                                                                                       Double julyTotalValue,
                                                                                                       Double augTotalValue) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("The report shows total values of ${double} for June, ${double} for July and ${double} for August")
    public void the_report_shows_total_values_of_$_for_june_$_for_july_and_$_for_august(Double juneTotalValue,
                                                                                        Double julyTotalValue,
                                                                                        Double augTotalValue) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

}
