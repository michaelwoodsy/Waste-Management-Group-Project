package gradle.cucumber.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONArray;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repositoryLayer.model.Card;
import org.seng302.project.repositoryLayer.model.Keyword;
import org.seng302.project.repositoryLayer.model.NewKeywordNotification;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.CardRepository;
import org.seng302.project.repositoryLayer.repository.KeywordRepository;
import org.seng302.project.repositoryLayer.repository.NewKeywordNotificationRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.dto.card.CreateCardDTO;
import org.seng302.project.serviceLayer.dto.card.CreateCardResponseDTO;
import org.seng302.project.serviceLayer.dto.keyword.AddKeywordDTO;
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
import java.util.Optional;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

public class KeywordManagementSteps extends AbstractInitializer {

    private final UserRepository userRepository;
    private final KeywordRepository keywordRepository;
    private final CardRepository cardRepository;
    private final NewKeywordNotificationRepository newKeywordNotificationRepository;
    private final ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private User testUser;

    private User adminUser;

    private Card testCard;

    private RequestBuilder searchKeywordRequest;
    private RequestBuilder newKeywordRequest;
    private RequestBuilder newCardRequest;

    private CreateCardDTO createCardDTO;

    private Keyword retrievedKeyword;
    private Card retrievedCard;


    @Autowired
    public KeywordManagementSteps(UserRepository userRepository,
                                  KeywordRepository keywordRepository,
                                  CardRepository cardRepository,
                                  NewKeywordNotificationRepository newKeywordNotificationRepository,
                                  ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.keywordRepository = keywordRepository;
        this.cardRepository = cardRepository;
        this.newKeywordNotificationRepository = newKeywordNotificationRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Set up MockMvc, and test user & businesses
     *
     * @param context Autowired WebApplicationContext for MockMvc
     */
    @BeforeEach
    @Autowired
    public void setup(WebApplicationContext context) {
        this.initialise();
        cardRepository.deleteAll();
        userRepository.deleteAll();

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        testUser = this.getTestUser();
        testUser.setId(null);
        testUser.setHomeAddress(null);

        adminUser = this.getTestSystemAdmin();
        adminUser.setId(null);
        adminUser.setHomeAddress(null);

        // Save the test users
        testUser = userRepository.save(testUser);
        adminUser = userRepository.save(adminUser);
    }

    //AC1/AC2

    @Given("The keyword {string} exists")
    public void the_keyword_exists(String keyword) {
        if (keywordRepository.findByName(keyword).isEmpty()) {
            keywordRepository.save(new Keyword(keyword));
        }
        Assertions.assertFalse(keywordRepository.findByName(keyword).isEmpty());
    }

    @When("A user creates a card and adds the keyword {string}")
    public void a_user_creates_a_card_and_adds_the_keyword(String keyword) {
        retrievedKeyword = keywordRepository.findByName(keyword).get(0);

        createCardDTO = new CreateCardDTO();
        createCardDTO.setCreatorId(testUser.getId());
        createCardDTO.setTitle("New Card");
        createCardDTO.setDescription("Some Description");
        createCardDTO.setSection("ForSale");
        createCardDTO.setKeywordIds(List.of(retrievedKeyword.getId()));
    }

    @Then("The card is created with the keyword {string}")
    public void the_card_is_created_with_the_keyword(String keyword) throws Exception {
        newCardRequest = MockMvcRequestBuilders
                .post("/cards")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createCardDTO))
                .with(user(new AppUserDetails(testUser)));

        MvcResult result = mockMvc.perform(newCardRequest)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();
        CreateCardResponseDTO response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CreateCardResponseDTO.class);

        retrievedCard = cardRepository.findById(response.getCardId()).orElseThrow();
        retrievedKeyword = keywordRepository.findByName(keyword).get(0);
        Assertions.assertTrue(retrievedCard.getKeywords().contains(retrievedKeyword));
    }

    @Given("The keyword {string} and the keyword {string} exists")
    public void the_keyword_and_the_keyword_exists(String firstKeyword, String secondKeyword) {
        if (keywordRepository.findByName(firstKeyword).isEmpty()) {
            keywordRepository.save(new Keyword(firstKeyword));
        }
        if (keywordRepository.findByName(secondKeyword).isEmpty()) {
            keywordRepository.save(new Keyword(secondKeyword));
        }
        Assertions.assertFalse(keywordRepository.findByName(firstKeyword).isEmpty());
        Assertions.assertFalse(keywordRepository.findByName(secondKeyword).isEmpty());
    }

    @When("A user creates a card and adds the keywords {string} and {string}")
    public void a_user_creates_a_card_and_adds_the_keywords_and(String firstKeywordString, String secondKeywordString) {
        Keyword firstKeyword = keywordRepository.findByName(firstKeywordString).get(0);
        Keyword secondKeyword = keywordRepository.findByName(secondKeywordString).get(0);

        createCardDTO = new CreateCardDTO();
        createCardDTO.setCreatorId(testUser.getId());
        createCardDTO.setTitle("Another New Card");
        createCardDTO.setDescription("Some Description");
        createCardDTO.setSection("ForSale");
        createCardDTO.setKeywordIds(List.of(firstKeyword.getId(), secondKeyword.getId()));
    }

    @Then("The card is created with the keywords {string} and {string}")
    public void the_card_is_created_with_the_keywords_and(String firstKeywordString,
                                                          String secondKeywordString) throws Exception {
        newCardRequest = MockMvcRequestBuilders
                .post("/cards")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createCardDTO))
                .with(user(new AppUserDetails(testUser)));
        System.out.println(createCardDTO);
        System.out.println(testUser);

        MvcResult result = mockMvc.perform(newCardRequest)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();
        System.out.println(result.getResponse().getContentAsString());
        System.out.println("Hello");
        CreateCardResponseDTO response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CreateCardResponseDTO.class);

        retrievedCard = cardRepository.findById(response.getCardId()).orElseThrow();
        Keyword firstKeyword = keywordRepository.findByName(firstKeywordString).get(0);
        Keyword secondKeyword = keywordRepository.findByName(secondKeywordString).get(0);
        Assertions.assertTrue(retrievedCard.getKeywords().contains(firstKeyword));
        Assertions.assertTrue(retrievedCard.getKeywords().contains(secondKeyword));
    }

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

    @Then("It is possible to add {string} as a new keyword")
    public void it_is_possible_to_add_as_a_new_keyword(String keyword) throws Exception {
        newKeywordRequest = MockMvcRequestBuilders
                .post("/keywords")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new AddKeywordDTO(keyword)))
                .with(user(new AppUserDetails(testUser)));
        mockMvc.perform(newKeywordRequest).andExpect(MockMvcResultMatchers.status().isCreated());
    }

    //AC5

    @When("The new keyword {string} is created")
    public void the_new_keyword_is_created(String keyword) throws Exception {
        newKeywordRequest = MockMvcRequestBuilders
                .post("/keywords")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new AddKeywordDTO(keyword)))
                .with(user(new AppUserDetails(testUser)));
        mockMvc.perform(newKeywordRequest).andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Then("A system admin notification is created for the new keyword {string}")
    public void a_system_admin_notification_is_created_for_the_new_keyword(String keyword) {
        List<NewKeywordNotification> adminNotifications =  newKeywordNotificationRepository.findAll();

        //There is also a NewKeywordNotification for "Bananas" at this point in the Cucumber tests.
        Assertions.assertEquals(2, adminNotifications.size());
        //"Bananas" is first, then "Cabbages"
        Assertions.assertEquals(keyword, adminNotifications.get(1).getKeyword().getName());
    }

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
        Optional<Card> cardOptional = cardRepository.findById(testCard.getId());
        Assertions.assertTrue(cardOptional.isPresent());
        testCard = cardOptional.get();
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
        Optional<Card> cardOptional = cardRepository.findById(testCard.getId());
        Assertions.assertTrue(cardOptional.isPresent());
        testCard = cardOptional.get();
        Keyword word = keywordRepository.findByName(keyword).get(0);
        Assertions.assertTrue(testCard.getKeywords().contains(word));
    }
}
