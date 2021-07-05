package gradle.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONArray;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.seng302.project.repositoryLayer.model.Card;
import org.seng302.project.repositoryLayer.model.Keyword;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.CardRepository;
import org.seng302.project.repositoryLayer.repository.KeywordRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

public class KeywordManagementSteps {

    private final KeywordRepository keywordRepository;

    private final CardRepository cardRepository;

    private final UserRepository userRepository;

    private MockMvc mockMvc;

    private User testUser;

    private User adminUser;

    private Card testCard;

    private RequestBuilder searchKeywordRequest;



    @Autowired
    public KeywordManagementSteps(
            KeywordRepository keywordRepository,
            CardRepository cardRepository,
            UserRepository userRepository) {
        this.keywordRepository = keywordRepository;
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
    }

    /**
     * Set up MockMvc, and test user & businesses
     * @param context Autowired WebApplicationContext for MockMvc
     */
    @BeforeEach
    @Autowired
    public void setup(WebApplicationContext context) {
        cardRepository.deleteAll();
        userRepository.deleteAll();
        keywordRepository.deleteAll();

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        testUser = new User("Test", "User", "", "", "I'm a test user", "testuser@gmail.com",
                "2000-07-28", "123 123 1234", null, "SecurePassword789");

        adminUser = new User("Admin", "User", "", "", "I'm a admin user", "testadminuser@gmail.com",
                "2000-07-28", "123 123 1234", null, "SecurePassword789");
        adminUser.setRole("globalApplicationAdmin");

        // Save the test users
        testUser = userRepository.save(testUser);
        adminUser = userRepository.save(adminUser);
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
        searchKeywordRequest = MockMvcRequestBuilders
                .get("/keywords/search?searchQuery={searchQuery}", partialKeyword)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

    }

    @Then("The keyword {string} is included as a potential match")
    public void the_keyword_is_included_as_a_potential_match(String keyword) throws Exception {
        MvcResult searchResult = this.mockMvc.perform(searchKeywordRequest)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String searchResultString = searchResult.getResponse().getContentAsString();
        JSONArray searchResultArray = new JSONArray(searchResultString);

        Assertions.assertEquals(keyword, searchResultArray.getJSONObject(0).getString("name"));
    }

    //AC4

    @Given("There is no keyword {string}")
    public void there_is_no_keyword(String keyword) {
        List<Keyword> keywordsWithName = keywordRepository.findByName(keyword);

        Assertions.assertEquals(0, keywordsWithName.size());
    }

    @When("There are no matches")
    public void there_are_no_matches() throws Exception {
        MvcResult searchResult = this.mockMvc.perform(searchKeywordRequest)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String searchResultString = searchResult.getResponse().getContentAsString();
        JSONArray searchResultArray = new JSONArray(searchResultString);

        Assertions.assertEquals(0, searchResultArray.length());
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
    //AC6

    @Given("A card with the keyword {string} exists")
    @Transactional
    public void a_card_with_the_keyword_exists(String keyword) {

        // Create the keyword
        Keyword word = new Keyword(keyword);
        word = keywordRepository.save(word);

        // Create the card with the keyword
        testCard = new Card(testUser, "Test", "Test", "test", Set.of(word));
        testCard = cardRepository.save(testCard);
    }

    @When("A system admin deletes the keyword {string}")
    public void a_system_admin_deletes_the_keyword(String keyword) throws Exception {
        int keywordId = keywordRepository.findByName(keyword).get(0).getId();
        searchKeywordRequest = MockMvcRequestBuilders
                .delete("/keywords/{id}", keywordId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(adminUser)));

        this.mockMvc.perform(searchKeywordRequest);
    }

    @Then("The keyword {string} no longer exists")
    public void the_keyword_no_longer_exists(String keyword) {
        List<Keyword> matchingKeywords = keywordRepository.findByName(keyword);
        Assertions.assertEquals(0, matchingKeywords.size());
    }

    @Then("The card no longer has the keyword {string}")
    public void the_card_no_longer_has_the_keyword(String keyword) {
        testCard = cardRepository.findById(testCard.getId()).get();
        // Check all the keywords that card has are not the same as the keyword
        for (Keyword word : testCard.getKeywords()) {
            Assertions.assertNotEquals(keyword, word.getName());
        }
    }

    @When("A regular user tries to delete the keyword {string}")
    public void a_regular_user_tries_to_delete_the_keyword(String keyword) throws Exception {
        int keywordId = keywordRepository.findByName(keyword).get(0).getId();
        searchKeywordRequest = MockMvcRequestBuilders
                .delete("/keywords/{id}", keywordId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        this.mockMvc.perform(searchKeywordRequest);
    }

    @Then("The keyword {string} still exists")
    public void the_keyword_still_exists(String keyword) {
        List<Keyword> matchingKeywords = keywordRepository.findByName(keyword);
        Assertions.assertEquals(1, matchingKeywords.size());
    }

    @Then("The card still has the keyword {string}")
    public void the_card_still_has_the_keyword(String keyword) {
        testCard = cardRepository.findById(testCard.getId()).get();
        Keyword word = keywordRepository.findByName(keyword).get(0);
        Assertions.assertTrue(testCard.getKeywords().contains(word));
    }
}
