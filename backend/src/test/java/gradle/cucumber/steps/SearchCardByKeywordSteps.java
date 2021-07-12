package gradle.cucumber.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.seng302.project.repositoryLayer.model.*;
import org.seng302.project.repositoryLayer.repository.AddressRepository;
import org.seng302.project.repositoryLayer.repository.CardRepository;
import org.seng302.project.repositoryLayer.repository.KeywordRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class SearchCardByKeywordSteps {

    private User testUser;
    private Integer keyword1Id;
    private Integer keyword2Id;

    private ResultActions result;
    private MockMvc mockMvc;

    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final AddressRepository addressRepository;
    private final KeywordRepository keywordRepository;

    @Autowired
    public SearchCardByKeywordSteps(
            UserRepository userRepository,
            CardRepository cardRepository,
            AddressRepository addressRepository,
            KeywordRepository keywordRepository) {
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
        this.addressRepository = addressRepository;
        this.keywordRepository = keywordRepository;
    }
    @BeforeEach
    @Autowired
    public void setup(WebApplicationContext context) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        Address testAddress = new Address("", "", "", "", "New Zealand", "");
        addressRepository.save(testAddress);

        cardRepository.deleteAll();
        keywordRepository.deleteAll();
    }

    /**
     * Creates the user if it's not already created.
     * If it is already created, the user is returned.
     * @return User
     */
    private User createUser(User wantedUser) {
        if (userRepository.findByEmail(wantedUser.getEmail()).size() > 0) {
            // Already exists, return it
            return(userRepository.findByEmail(wantedUser.getEmail()).get(0));
        } else {
            // User doesn't exist, save it to repository
            userRepository.save(wantedUser);
            return wantedUser;
        }
    }

    /**
     * Creates the keyword if it's not already created.
     * If it is already created, the keyword is returned.
     * @return Keyword this is the keyword whether it exists or not
     */
    private Keyword createKeyword(Keyword keyword){
        if(keywordRepository.findByName(keyword.getName()).size() > 0){
            //Already Exists, return it
            return(keywordRepository.findByName(keyword.getName()).get(0));
        } else {
            // Keyword does not exist return it so card can make it
            keywordRepository.save(keyword);
            return keyword;
        }
    }
    @Transactional
    @Given("I am logged in with email {string} with the following 3 cards exist with keywords:")
    public void i_am_logged_in_with_email_with_the_following_3_cards_exist_with_keywords(String email, DataTable cardsTable) {
        // Create the logged in user
        testUser = new User("John", "Smith", "Bob", "Jonny",
                "Likes long walks on the beach", "test@gmail.com", "1999-04-27",
                "+64 3 555 0129", null, "");
        testUser.setEmail(email);
        testUser = createUser(testUser);

        Card newCard;

        // Create the cards
        List<List<String>> rows = cardsTable.asLists(String.class);
        for (List<String> cols : rows) {

            // Creat the card creator user
            User owner = new User("Jane", "Doe", "Bob", "Jonny",
                    "Likes long walks on the beach", "test@gmail.com", "1999-04-27",
                    "+64 3 555 0129", null, "");
            owner.setEmail(cols.get(3));
            owner = createUser(owner);

            //Create Keyword
            Keyword keyword1 = new Keyword(cols.get(4));
            keyword1 = createKeyword(keyword1);

            // Create the card and set values
            newCard = new Card();
            newCard.setTitle(cols.get(0));
            newCard.setDescription(cols.get(1));
            newCard.setSection(cols.get(2));
            newCard.setCreator(owner);
            newCard.setKeywords(Set.of(keyword1));
            // Save the card
            cardRepository.save(newCard);
        }
        //Get Id of keyword
        keyword1Id = keywordRepository.findByName("testKeyword").get(0).getId();
        //Check that the correct number of cards have been added to the repository in the correct section
        assertEquals(2, cardRepository.findAllBySection("Wanted").size());
        //Check that all cards have the keyword
        assertEquals(3, cardRepository.findAllByKeywordsContaining(keywordRepository.findByName("testKeyword").get(0)).size());
    }

    @When("I search for cards in the {string} section")
    public void iSearchForCardsInTheSection(String section) throws Exception {
        RequestBuilder getCardsReq = MockMvcRequestBuilders
                .get("/cards/search?keywordIds={keywordId}&section={section}&union=true", keyword1Id, section)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(testUser.getEmail()));

        result = mockMvc.perform(getCardsReq)
                .andExpect(MockMvcResultMatchers.status().isOk()); // We expect a 200 response
    }

    @Then("I am returned {int} cards")
    public void i_am_returned_cards(Integer cardCount) throws Exception {
        result.andExpect(jsonPath("$", hasSize(cardCount)));
    }


    @And("All returned cards are with section {string}")
    public void allReturnedCardsAreWithSection(String desiredSection) throws Exception {
        // Get the json result
        String contentAsString = result.andReturn().getResponse().getContentAsString();
        JSONArray results = new JSONArray(contentAsString);

        // Iterate over the json results and check the creator email
        for (int i = 0; i < results.length(); i++) {
            // Get json for card
            JSONObject cardJson = results.getJSONObject(i);

            // Get the section on the card
            String section = cardJson.getString("section");

            // Check it is the desired section
            assertEquals(section, desiredSection);
        }
    }

    @Transactional
    @Given("The Following cards exist:")
    public void theFollowingCardsExist(DataTable cardsTable) {
        // Create the logged in user
        testUser = new User("John", "Smith", "Bob", "Jonny",
                "Likes long walks on the beach", "test@gmail.com", "1999-04-27",
                "+64 3 555 0129", null, "");
        testUser = createUser(testUser);

        Card newCard;

        // Create the cards
        List<List<String>> rows = cardsTable.asLists(String.class);
        for (List<String> cols : rows) {

            // Creat the card creator user
            User owner = new User("Jane", "Doe", "Bob", "Jonny",
                    "Likes long walks on the beach", "test@gmail.com", "1999-04-27",
                    "+64 3 555 0129", null, "");
            owner.setEmail(cols.get(3));
            owner = createUser(owner);

            //Create Keyword
            Keyword keyword1 = new Keyword(cols.get(4));
            keyword1 = createKeyword(keyword1);
            Keyword keyword2 = new Keyword(cols.get(5));
            keyword2 = createKeyword(keyword2);


            // Create the card and set values
            newCard = new Card();
            newCard.setTitle(cols.get(0));
            newCard.setDescription(cols.get(1));
            newCard.setSection(cols.get(2));
            newCard.setCreator(owner);
            newCard.setKeywords(Set.of(keyword1, keyword2));
            // Save the card
            cardRepository.save(newCard);
        }
        //Check that the correct number of cards have been added to the repository in the correct section
        assertEquals(3, cardRepository.findAllBySection("Wanted").size());
    }

    @When("I search for cards with the keywords {string} or {string} or {string} in the {string} section")
    public void iSearchForCardsWithTheKeywordsOrOrInTheSection(String keyword1, String keyword2, String keyword3, String section) throws Exception {
        //Get Id of keywords
        keyword1Id = keywordRepository.findByName(keyword1).get(0).getId();
        keyword2Id = keywordRepository.findByName(keyword2).get(0).getId();
        Integer keyword3Id = keywordRepository.findByName(keyword3).get(0).getId();

        RequestBuilder getCardsReq = MockMvcRequestBuilders
                .get("/cards/search?keywordIds={keywordId}&keywordIds={keywordId}&keywordIds={keywordId}&section={section}&union=true",
                        keyword1Id, keyword2Id, keyword3Id, section)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(testUser.getEmail()));

        result = mockMvc.perform(getCardsReq)
                .andExpect(MockMvcResultMatchers.status().isOk()); // We expect a 200 response
    }

    @And("All returned cards have at least one of the keywords {string} or {string} or {string}")
    public void allReturnedCardsHaveAtLeastOneOfTheKeywordsOrOr(String keyword1, String keyword2, String keyword3) throws Exception {
        // Get the json result
        String contentAsString = result.andReturn().getResponse().getContentAsString();
        JSONArray results = new JSONArray(contentAsString);

        // Iterate over the json results and check the creator email
        for (int i = 0; i < results.length(); i++) {
            // Get json for card
            JSONObject cardJson = results.getJSONObject(i);

            // Get the list of keywords on the card
            JSONArray keywordsArray = cardJson.getJSONArray("keywords");
            ArrayList<String> keywordNames = new ArrayList<>();
            for(int j = 0; j < keywordsArray.length(); j++){
                //Add each name of the keyword to a list
                keywordNames.add(keywordsArray.getJSONObject(j).getString("name"));
            }
            // Check that each card has at least one of the desired keywords
            assertTrue(keywordNames.contains(keyword1) || keywordNames.contains(keyword2) || keywordNames.contains(keyword3));
        }
    }
    @Transactional
    @Given("The Following {int} cards exist:")
    public void theFollowingCardsExist(int numberCards, DataTable cardsTable) {
        // Create the logged in user
        testUser = new User("John", "Smith", "Bob", "Jonny",
                "Likes long walks on the beach", "test@gmail.com", "1999-04-27",
                "+64 3 555 0129", null, "");
        testUser = createUser(testUser);

        Card newCard;

        // Create the cards
        List<List<String>> rows = cardsTable.asLists(String.class);
        for (List<String> cols : rows) {

            // Creat the card creator user
            User owner = new User("Jane", "Doe", "Bob", "Jonny",
                    "Likes long walks on the beach", "test@gmail.com", "1999-04-27",
                    "+64 3 555 0129", null, "");
            owner.setEmail(cols.get(3));
            owner = createUser(owner);

            //Create Keyword
            Keyword keyword1 = new Keyword(cols.get(4));
            keyword1 = createKeyword(keyword1);
            Keyword keyword2 = new Keyword(cols.get(5));
            keyword2 = createKeyword(keyword2);


            // Create the card and set values
            newCard = new Card();
            newCard.setTitle(cols.get(0));
            newCard.setDescription(cols.get(1));
            newCard.setSection(cols.get(2));
            newCard.setCreator(owner);
            newCard.setKeywords(Set.of(keyword1, keyword2));
            // Save the card
            cardRepository.save(newCard);
        }
        //Check that the correct number of cards have been added to the repository in the correct section
        assertEquals(numberCards, cardRepository.findAllBySection("Wanted").size());
    }

    @When("I search for cards with the keywords {string} and {string} in the {string} section")
    public void iSearchForCardsWithTheKeywordsAndInTheSection(String keyword1, String keyword2, String section) throws Exception {
        //Get Id of keywords
        keyword1Id = keywordRepository.findByName(keyword1).get(0).getId();
        keyword2Id = keywordRepository.findByName(keyword2).get(0).getId();

        RequestBuilder getCardsReq = MockMvcRequestBuilders
                .get("/cards/search?keywordIds={keywordId}&keywordIds={keywordId}&section={section}&union=false",
                        keyword1Id, keyword2Id, section)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(testUser.getEmail()));

        result = mockMvc.perform(getCardsReq)
                .andExpect(MockMvcResultMatchers.status().isOk()); // We expect a 200 response
    }

    @And("All returned cards have at all of the keywords {string} and {string}")
    public void allReturnedCardsHaveAtAllOfTheKeywordsAnd(String keyword1, String keyword2) throws Exception {
        // Get the json result
        String contentAsString = result.andReturn().getResponse().getContentAsString();
        JSONArray results = new JSONArray(contentAsString);

        // Iterate over the json results and check the creator email
        for (int i = 0; i < results.length(); i++) {
            // Get json for card
            JSONObject cardJson = results.getJSONObject(i);

            // Get the list of keywords on the card
            JSONArray keywordsArray = cardJson.getJSONArray("keywords");
            ArrayList<String> keywordNames = new ArrayList<>();
            for(int j = 0; j < keywordsArray.length(); j++){
                //Add each name of the keyword to a list
                keywordNames.add(keywordsArray.getJSONObject(j).getString("name"));
            }
            // Check that each card has at least one of the desired keywords
            assertTrue(keywordNames.contains(keyword1) && keywordNames.contains(keyword2));
        }
    }
}
