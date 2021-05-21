package gradle.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.seng302.project.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CardCreationSteps {

    private User testUser;
    private Integer testUserId;
    private Address testAddress;
    private MockMvc mockMvc;

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

    @BeforeEach
    @Autowired
    public void setUp(WebApplicationContext context) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
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
        testUserId = userRepository.save(testUser).getId();
    }

    @When("A user creates a card to be displayed in the {string} section")
    public void a_user_creates_a_card_to_be_displayed_in_the_section(String section) throws Exception {
        // Write code here that turns the phrase above into concrete actions
        JSONObject testCardJson = new JSONObject();
        testCardJson.put("creatorId", testUserId);
        testCardJson.put("section", "ForSale");
        testCardJson.put("title", "1982 Lada Samara");
        testCardJson.put("description",
                "Beige, suitable for a hen house. Fair condition. Some rust. As is, where is. Will swap for budgerigar.");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/cards")
                .content(testCardJson.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic(testUserEmail, testUserPassword)))
                .andExpect(status().isCreated());

    }

    @Then("The card is successfully created")
    public void the_card_is_successfully_created() {
        // Write code here that turns the phrase above into concrete actions

        Card retrievedCard = cardRepository.findAllBySection("ForSale").get(0);

        Assertions.assertNotNull(retrievedCard.getCreator().getId());
        Assertions.assertEquals("Jim", retrievedCard.getCreator().getFirstName());
        Assertions.assertEquals("Smith", retrievedCard.getCreator().getLastName());

        Assertions.assertEquals("ForSale", retrievedCard.getSection());
        Assertions.assertEquals("1982 Lada Samara", retrievedCard.getTitle());
        Assertions.assertEquals("Beige, suitable for a hen house. Fair condition. Some rust. As is, where is. Will swap for budgerigar.",
                retrievedCard.getDescription());
        Assertions.assertTrue(retrievedCard.getCreated().isBefore(LocalDateTime.now()) || retrievedCard.getCreated().isEqual(LocalDateTime.now()));
        Assertions.assertTrue(retrievedCard.getCreated().isAfter(LocalDateTime.now().minusSeconds(5)));
        Assertions.assertTrue(retrievedCard.getDisplayPeriodEnd().isEqual(retrievedCard.getCreated().plusDays(14)));
        throw new io.cucumber.java.PendingException();
    }

    //AC2
    @Given("A card exists")
    public void a_card_exists() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }


    @Then("The card creator's name and location are displayed successfully")
    public void the_card_creator_s_name_and_location_are_displayed_successfully() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    //AC3

    @Then("The card's title is shown")
    public void the_card_s_title_is_shown() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }


    //AC4

    @When("A user views a card in the card display section")
    public void a_user_views_a_card_in_the_card_display_section() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("The card's description is shown")
    public void the_card_s_description_is_shown() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    //AC5

    @When("A user creates a card with keywords: {string}, {string}, {string}, and {string}")
    public void a_user_creates_a_card_with_keywords_and(String string, String string2, String string3, String string4) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("The card's keywords are successfully saved with the card")
    public void the_card_s_keywords_are_successfully_saved_with_the_card() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    //AC6
    @Given("A card exists in the {string} section")
    public void a_card_exists_in_the_section(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("A user views the {string} section")
    public void a_user_views_the_section(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("The card is successfully displayed in this section")
    public void the_card_is_successfully_displayed_in_this_section() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }


}
