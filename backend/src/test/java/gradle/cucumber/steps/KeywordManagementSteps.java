package gradle.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.BeforeEach;
import org.seng302.project.repositoryLayer.model.Keyword;
import org.seng302.project.repositoryLayer.repository.KeywordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

public class KeywordManagementSteps {

    private final KeywordRepository keywordRepository;

    private MockMvc mockMvc;

    @Autowired
    public KeywordManagementSteps(
            KeywordRepository keywordRepository) {
        this.keywordRepository = keywordRepository;
    }

    /**
     * Set up MockMvc, and test user & businesses
     * @param context Autowired WebApplicationContext for MockMvc
     */
    @BeforeEach
    @Autowired
    public void setup(WebApplicationContext context) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        keywordRepository.deleteAll();
    }


    //AC1/AC2

    @Given("The keyword {string} exists")
    public void the_keyword_exists(String keyword) {
        Keyword existingKeyword = new Keyword(keyword);
        keywordRepository.save(existingKeyword);
    }
//
//    @When("A user creates a card and adds the keyword {string}")
//    public void a_user_creates_a_card_and_adds_the_keyword(String keyword) {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    @Then("The card is created with the keyword {string}")
//    public void the_card_is_created_with_the_keyword(String keyword) {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    @Given("The keyword {string} and the keyword {string} exists")
//    public void the_keyword_and_the_keyword_exists(String keyword1, String keyword2) {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    @When("A user creates a card and adds the keywords {string} and {string}")
//    public void a_user_creates_a_card_and_adds_the_keywords_and(String keyword1, String keyword2) {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    @Then("The card is created with the keywords {string} and {string}")
//    public void the_card_is_created_with_the_keywords_and(String keyword1, String keyword2) {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }

    //AC3

    @When("The user enters {string} as a partial keyword")
    public void the_user_enters_as_a_partial_keyword(String partialKeyword) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("The keyword {string} is included as a potential match")
    public void the_keyword_is_included_as_a_potential_match(String keyword) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    //AC4

    @Given("There is no keyword {string}")
    public void there_is_no_keyword(String keyword) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("There are no matches")
    public void there_are_no_matches() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

//    @Then("It is possible to add {string} as a new keyword")
//    public void it_is_possible_to_add_as_a_new_keyword(String keyword) {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    //AC5
//
//    @When("The new keyword {string} is created")
//    public void the_new_keyword_is_created(String keyword) {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    @Then("A system admin notification is created for the new keyword {string}")
//    public void a_system_admin_notification_is_created_for_the_new_keyword(String keyword) {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    //AC6
//
//    @Given("A card with the keyword {string} exists")
//    public void a_card_with_the_keyword_exists(String keyword) {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    @When("A system admin deletes the keyword {string}")
//    public void a_system_admin_deletes_the_keyword(String keyword) {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    @Then("The keyword {string} no longer exists")
//    public void the_keyword_no_longer_exists(String keyword) {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    @Then("The card no longer has the keyword {string}")
//    public void the_card_no_longer_has_the_keyword(String keyword) {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    @When("A regular user tries to delete the keyword {string}")
//    public void a_regular_user_tries_to_delete_the_keyword(String keyword) {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    @Then("The keyword {string} still exists")
//    public void the_keyword_still_exists(String keyword) {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    @Then("The card still has the keyword {string}")
//    public void the_card_still_has_the_keyword(String keyword) {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
}
