package gradle.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.SneakyThrows;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.seng302.project.controller.authentication.AppUserDetails;
import org.seng302.project.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CardCreationSteps {

    private User testUser;
    private Card testCard;
    private JSONObject testCardJson = new JSONObject();

    private String testUserEmail;
    private String testUserPassword;
    private Integer testUserId;
    private Integer testCardId;
    private String testCardSection;
    private Address testAddress;
    private MockMvc mockMvc;

    private ResultActions reqResult;


    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AddressRepository addressRepository;
    private final CardRepository cardRepository;

    @Autowired
    public CardCreationSteps(UserRepository userRepository,
                             BCryptPasswordEncoder passwordEncoder,
                             AddressRepository addressRepository,
                             CardRepository cardRepository) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.addressRepository = addressRepository;
        this.cardRepository = cardRepository;
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
        testAddress = new Address("", "", "", "", "New Zealand","");
        testUser = new User("Admin", "Admin", "", "", "", testUserEmail,
                "2000-05-21", "+64 3 555 0129", testAddress, testUserPassword);
        testUser.setPassword(passwordEncoder.encode(testUser.getPassword()));
        addressRepository.save(testAddress);
        testUser = createUser(testUser);
        testUserId = testUser.getId();
    }

    @When("A user creates a card to be displayed in the {string} section")
    public void a_user_creates_a_card_to_be_displayed_in_the_section(String section) throws Exception {
        // Write code here that turns the phrase above into concrete actions
        testCardJson.put("creatorId", testUserId);
        testCardJson.put("section", section);
        testCardJson.put("title", "1982 Lada Samara");
        testCardJson.put("description",
                "Beige, suitable for a hen house. Fair condition. Some rust. As is, where is. Will swap for budgerigar.");

        System.out.println(testUserId);
        System.out.println(testUser.getId());

        // Make the actual request
        reqResult = mockMvc.perform(MockMvcRequestBuilders
                .post("/cards")
                .content(testCardJson.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser))));
    }

    @Then("The card is successfully created")
    public void the_card_is_successfully_created() throws Exception {
        // Write code here that turns the phrase above into concrete actions
        MvcResult result = reqResult
                .andExpect(status().isCreated())
                .andReturn();

        JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
        Integer testCardId = jsonObject.getInt("cardId");
        Integer retrievedCardId = cardRepository.findById(testCardId).get().getId();
        Assertions.assertEquals(testCardId, retrievedCardId);

    }

    //AC2
    @Given("A card exists")
    public void a_card_exists() {
        // Write code here that turns the phrase above into concrete actions
        a_user_exists();
        testCard = new Card(testUser, "ForSale", "Beetle Juice", "Beetle juice from Bob", "");
        testCardId = cardRepository.save(testCard).getId();
    }


    @Then("The card creator's name and location are displayed successfully")
    public void the_card_creator_s_name_and_location_are_displayed_successfully() throws Exception {
        // Write code here that turns the phrase above into concrete actions

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/cards")
                .content(testCardJson.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser))))
                .andExpect(status().isCreated())
                .andReturn();

        JSONArray retrievedCards = new JSONArray(result.getResponse().getContentAsString());
        for (int i = 0; i < retrievedCards.length(); i++) {
            JSONObject retrievedCard = retrievedCards.getJSONObject(i);
            JSONObject retrievedCreator = retrievedCard.getJSONObject("creator");
            String retrievedFirstName = retrievedCreator.getString("firstName");
            String retrievedLastName = retrievedCreator.getString("lastName");

            String retrievedLocation = retrievedCreator.getJSONObject("homeAddress").getString("country");

            Assertions.assertEquals(retrievedFirstName, testUser.getFirstName());
            Assertions.assertEquals(retrievedLastName, testUser.getLastName());
            Assertions.assertEquals(retrievedLocation, testUser.getHomeAddress().getCountry());
        }
    }

    //AC3

    @Then("The card's title is shown")
    public void the_card_s_title_is_shown() throws Exception {
        // Write code here that turns the phrase above into concrete actions

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/cards")
                .content(testCardJson.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser))))
                .andExpect(status().isCreated())
                .andReturn();

        JSONArray retrievedCards = new JSONArray(result.getResponse().getContentAsString());
        for (int i = 0; i < retrievedCards.length(); i++) {
            JSONObject retrievedCard = retrievedCards.getJSONObject(i);
            String retrievedTitle = retrievedCard.getString("title");

            Assertions.assertEquals(retrievedTitle, testCard.getTitle());
        }
    }


    //AC4

//    @When("A user views a card in the card display section")
//    public void a_user_views_a_card_in_the_card_display_section() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    } // nothing to test

    @Then("The card's description is shown")
    public void the_card_s_description_is_shown() throws Exception{
        // Write code here that turns the phrase above into concrete actions
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/cards")
                .content(testCardJson.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser))))
                .andExpect(status().isCreated())
                .andReturn();

        JSONArray retrievedCards = new JSONArray(result.getResponse().getContentAsString());
        for (int i = 0; i < retrievedCards.length(); i++) {
            JSONObject retrievedCard = retrievedCards.getJSONObject(i);
            String retrievedDescription = retrievedCard.getString("description");

            Assertions.assertEquals(retrievedDescription, testCard.getDescription());
        }
    }

    //AC5

    @When("A user creates a card with keywords: {string}, {string}, {string}, and {string}")
    public void a_user_creates_a_card_with_keywords_and(String string, String string2, String string3, String string4) throws Exception {
        // Write code here that turns the phrase above into concrete actions
        String keywords = string + string2 + string3 + string4;
        testCard.setKeywords(keywords);
        testCardJson.put("keywords", keywords);

        // Make the actual request
        reqResult = mockMvc.perform(MockMvcRequestBuilders
                .post("/cards")
                .content(testCardJson.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser))));
    }

    @Then("The card's keywords are successfully saved with the card")
    public void the_card_s_keywords_are_successfully_saved_with_the_card() throws Exception {
        // Write code here that turns the phrase above into concrete actions
        MvcResult result = reqResult
                .andExpect(status().isCreated())
                .andReturn();


        JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
        Integer testCardId = jsonObject.getInt("cardId");

        String retrievedKeywords = cardRepository.findById(testCardId).get().getKeywords();
        Assertions.assertEquals(retrievedKeywords, testCard.getKeywords());
    }

    //AC6
    @Given("A card exists in the {string} section")
    public void a_card_exists_in_the_section(String string) {
        // Write code here that turns the phrase above into concrete actions
        testCardSection = string;
        a_user_exists();
        testCard = new Card(testUser, testCardSection, "Beetle Juice", "Beetle juice from Bob", "");
        testCardId = cardRepository.save(testCard).getId();
    }

//    @When("A user views the {string} section")
//    public void a_user_views_the_section(String string) {
//        // Write code here that turns the phrase above into concrete actions
//
//    } No action to perform

    @Then("The card is successfully displayed in this section")
    public void the_card_is_successfully_displayed_in_this_section() throws Exception {
        // Write code here that turns the phrase above into concrete actions
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/cards")
                .param("section", testCardSection)
                .content(testCardJson.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser))))
                .andExpect(status().isCreated())
                .andReturn();

        JSONArray retrievedCards = new JSONArray(result.getResponse().getContentAsString());
        for (int i = 0; i < retrievedCards.length(); i++) {
            JSONObject retrievedCard = retrievedCards.getJSONObject(i);
            String retrievedSection = retrievedCard.getString("section");

            Assertions.assertEquals(retrievedSection, testCardSection);
        }
    }
}
