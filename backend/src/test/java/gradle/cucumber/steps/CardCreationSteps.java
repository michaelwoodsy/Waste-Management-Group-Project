package gradle.cucumber.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.seng302.project.repositoryLayer.model.Address;
import org.seng302.project.repositoryLayer.model.Card;
import org.seng302.project.repositoryLayer.model.Keyword;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.AddressRepository;
import org.seng302.project.repositoryLayer.repository.CardRepository;
import org.seng302.project.repositoryLayer.repository.KeywordRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.dto.card.CreateCardDTO;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CardCreationSteps {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AddressRepository addressRepository;
    private final CardRepository cardRepository;
    private final ObjectMapper objectMapper;
    private final KeywordRepository keywordRepository;
    List<String> keywordNames;
    private User testUser;
    private Card testCard;
    private CreateCardDTO createCardDTO;
    private String testUserEmail;
    private String testUserPassword;
    private Integer testUserId;
    private Integer testCardId;
    private String testCardSection;
    private Address testAddress;
    private MockMvc mockMvc;
    private ResultActions reqResult;

    @Autowired
    public CardCreationSteps(UserRepository userRepository,
                             BCryptPasswordEncoder passwordEncoder,
                             AddressRepository addressRepository,
                             CardRepository cardRepository,
                             KeywordRepository keywordRepository,
                             ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.addressRepository = addressRepository;
        this.cardRepository = cardRepository;
        this.keywordRepository = keywordRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Creates the user if it's not already created.
     * If it is already created, the user is returned.
     *
     * @return User
     */
    private User createUser(User wantedUser) {
        if (userRepository.findByEmail(wantedUser.getEmail()).size() > 0) {
            // Already exists, return it
            return (userRepository.findByEmail(wantedUser.getEmail()).get(0));
        } else {
            // User doesn't exist, save it to repository
            userRepository.save(wantedUser);
            return wantedUser;
        }
    }


    @BeforeEach
    @Autowired
    public void setUp(WebApplicationContext context) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @AfterEach
    public void tearDown() {
        List<User> possibleUser = userRepository.findByEmail("test.user@gmail.com");
        if (possibleUser.size() > 0) {
            userRepository.delete(possibleUser.get(0));
        }
    }

    //AC1
    @Given("A user exists")
    public void a_user_exists() {
        testUserEmail = "test.user@gmail.com";
        testUserPassword = "I'mT3sting";
        testAddress = new Address("", "", "", "", "New Zealand", "");
        testUser = new User("Admin", "Admin", "", "", "", testUserEmail,
                "2000-05-21", "+64 3 555 0129", testAddress, testUserPassword);
        testUser.setPassword(passwordEncoder.encode(testUser.getPassword()));
        addressRepository.save(testAddress);
        testUser = createUser(testUser);
        testUserId = testUser.getId();
    }

    @When("A user creates a card to be displayed in the {string} section")
    public void a_user_creates_a_card_to_be_displayed_in_the_section(String section) throws Exception {

        createCardDTO = new CreateCardDTO(testUserId, section, "1982 Lada Samara", "Beige, suitable for a hen house. " +
                "Fair condition. Some rust. As is, where is. Will swap for budgerigar.", Collections.emptyList());

        reqResult = mockMvc.perform(MockMvcRequestBuilders
                .post("/cards")
                .content(objectMapper.writeValueAsString(createCardDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser))));
    }

    @Then("The card is successfully created")
    public void the_card_is_successfully_created() throws Exception {
        MvcResult result = reqResult
                .andReturn();

        JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
        Integer testCardId = jsonObject.getInt("cardId");
        Optional<Card> retrievedCard = cardRepository.findById(testCardId);
        Assertions.assertTrue(retrievedCard.isPresent());

        Integer retrievedCardId = retrievedCard.get().getId();
        Assertions.assertEquals(testCardId, retrievedCardId);

    }

    //AC2
    @Given("A card exists")
    public void a_card_exists() {
        a_user_exists();
        testCard = new Card(testUser, "ForSale", "Beetle Juice", "Beetle juice from Bob", null);
        testCardId = cardRepository.save(testCard).getId();
    }


    @Then("The card creator's name and location are displayed successfully")
    public void the_card_creator_s_name_and_location_are_displayed_successfully() throws Exception {

        MvcResult result = reqResult
                .andExpect(status().isOk())
                .andReturn();

        JSONObject response = new JSONObject(result.getResponse().getContentAsString());
        JSONArray retrievedCards = new JSONArray(response.getString("cards"));
        for (int i = 0; i < retrievedCards.length(); i++) {
            JSONObject retrievedCard = retrievedCards.getJSONObject(i);
            JSONObject retrievedCreator = retrievedCard.getJSONObject("creator");
            String retrievedFirstName = retrievedCreator.getString("firstName");
            String retrievedLastName = retrievedCreator.getString("lastName");

            String retrievedLocation = retrievedCreator.getJSONObject("homeAddress").getString("country");

            assertNotNull(retrievedFirstName);
            assertNotNull(retrievedLastName);
            assertNotNull(retrievedLocation);
        }
    }

    //AC3

    @Then("The card's title is shown")
    public void the_card_s_title_is_shown() throws Exception {

        MvcResult result = reqResult
                .andExpect(status().isOk())
                .andReturn();

        JSONObject response = new JSONObject(result.getResponse().getContentAsString());
        JSONArray retrievedCards = new JSONArray(response.getString("cards"));
        for (int i = 0; i < retrievedCards.length(); i++) {
            JSONObject retrievedCard = retrievedCards.getJSONObject(i);
            String retrievedTitle = retrievedCard.getString("title");

            Assertions.assertNotNull(retrievedTitle);
        }
    }


    //AC4

    @When("A user views a card in the card display section")
    public void a_user_views_a_card_in_the_card_display_section() throws Exception {
        reqResult = mockMvc.perform(MockMvcRequestBuilders
                .get("/cards")
                .param("section", "ForSale")
                .with(user(new AppUserDetails(testUser))));
    }

    @Then("The card's description is shown")
    public void the_card_s_description_is_shown() throws Exception {
        MvcResult result = reqResult
                .andExpect(status().isOk())
                .andReturn();

        JSONObject response = new JSONObject(result.getResponse().getContentAsString());
        JSONArray retrievedCards = new JSONArray(response.getString("cards"));
        for (int i = 0; i < retrievedCards.length(); i++) {
            JSONObject retrievedCard = retrievedCards.getJSONObject(i);
            String retrievedDescription = retrievedCard.getString("description");

            Assertions.assertNotNull(retrievedDescription);
        }
    }

    //AC5

    @When("A user creates a card with keywords: {string}, {string}, {string}, and {string}")
    public void a_user_creates_a_card_with_keywords_and(String keywordName1, String keywordName2, String keywordName3,
                                                        String keywordName4) throws Exception {
        keywordNames = List.of(keywordName1, keywordName2, keywordName3, keywordName4);

        Integer keywordId1 = keywordRepository.save(new Keyword(keywordName1)).getId();
        Integer keywordId2 = keywordRepository.save(new Keyword(keywordName2)).getId();
        Integer keywordId3 = keywordRepository.save(new Keyword(keywordName3)).getId();
        Integer keywordId4 = keywordRepository.save(new Keyword(keywordName4)).getId();

        createCardDTO = new CreateCardDTO(testUserId, "ForSale", "1982 Lada Samara",
                "Beige, suitable for a hen house. Fair condition. Some rust. As is, where is. " +
                        "Will swap for budgerigar.",
                List.of(keywordId1, keywordId2, keywordId3, keywordId4));

        reqResult = mockMvc.perform(MockMvcRequestBuilders
                .post("/cards")
                .content(objectMapper.writeValueAsString(createCardDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser))));
    }

    @Then("The card's keywords are successfully saved with the card")
    public void the_card_s_keywords_are_successfully_saved_with_the_card() throws Exception {
        MvcResult result = reqResult
                .andExpect(status().isCreated())
                .andReturn();

        JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
        Integer testCardId = jsonObject.getInt("cardId");

        Optional<Card> retrievedCard = cardRepository.findById(testCardId);
        Assertions.assertTrue(retrievedCard.isPresent());

        Set<Keyword> retrievedKeywords = retrievedCard.get().getKeywords();
        List<String> retrievedKeywordNames = new ArrayList<>();
        retrievedKeywords.forEach(keyword -> retrievedKeywordNames.add(keyword.getName()));

        Assertions.assertTrue(retrievedKeywordNames.contains(keywordNames.get(0)));
        Assertions.assertTrue(retrievedKeywordNames.contains(keywordNames.get(1)));
        Assertions.assertTrue(retrievedKeywordNames.contains(keywordNames.get(2)));
        Assertions.assertTrue(retrievedKeywordNames.contains(keywordNames.get(3)));
    }

    //AC6
    @Given("A card exists in the {string} section")
    public void a_card_exists_in_the_section(String string) {
        testCardSection = string;
        a_user_exists();
        testCard = new Card(testUser, testCardSection, "Beetle Juice", "Beetle juice from Bob", null);
        testCardId = cardRepository.save(testCard).getId();
    }

    @When("A user views the {string} section")
    public void a_user_views_the_section(String section) throws Exception {
        reqResult = mockMvc.perform(MockMvcRequestBuilders
                .get("/cards")
                .param("section", section)
                .with(user(new AppUserDetails(testUser))));
    }

    @Then("The card is successfully displayed in this section")
    public void the_card_is_successfully_displayed_in_this_section() throws Exception {
        MvcResult result = reqResult
                .andExpect(status().isOk())
                .andReturn();

        // Iterate over retrieved cards and check the created card is there
        boolean cardIsInSection = false;
        JSONObject response = new JSONObject(result.getResponse().getContentAsString());
        JSONArray retrievedCards = new JSONArray(response.getString("cards"));
        for (int i = 0; i < retrievedCards.length(); i++) {
            JSONObject retrievedCard = retrievedCards.getJSONObject(i);
            int cardId = retrievedCard.getInt("id");

            if (cardId == testCard.getId()) {
                cardIsInSection = true;
            }
        }

        assertTrue(cardIsInSection);
    }
}
