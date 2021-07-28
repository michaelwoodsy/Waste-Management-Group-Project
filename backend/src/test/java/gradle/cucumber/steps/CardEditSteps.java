package gradle.cucumber.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.seng302.project.repository_layer.model.Address;
import org.seng302.project.repository_layer.model.Card;
import org.seng302.project.repository_layer.model.Keyword;
import org.seng302.project.repository_layer.model.User;
import org.seng302.project.repository_layer.repository.AddressRepository;
import org.seng302.project.repository_layer.repository.CardRepository;
import org.seng302.project.repository_layer.repository.KeywordRepository;
import org.seng302.project.repository_layer.repository.UserRepository;
import org.seng302.project.service_layer.dto.card.EditCardDTO;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

public class CardEditSteps {

    private User cardCreator;
    private User testUser;
    private Card testCard;
    private EditCardDTO editCardDTO;

    private User testGAA;

    private Integer testCardId;
    private Address testAddress;
    private MockMvc mockMvc;

    private String editedSection;
    private String editedTitle;
    private String editedDescription;

    private ResultActions reqResult;

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AddressRepository addressRepository;
    private final CardRepository cardRepository;
    private final ObjectMapper objectMapper;

    private final KeywordRepository keywordRepository;

    private RequestBuilder editCardRequest;

    @Autowired
    public CardEditSteps(UserRepository userRepository,
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
        cardRepository.deleteAll();
        userRepository.deleteAll();

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        testAddress = new Address("", "", "", "", "New Zealand", "");
        String cardCreatorEmail = "b.beetle@gmail.com";
        String cardCreatorPassword = "B0bLovesB33tles";
        cardCreator = new User("Bob", "Beetle", "", "", "",
                cardCreatorEmail, "2000-05-21", "+64 3 555 0129",
                testAddress, cardCreatorPassword);
        addressRepository.save(testAddress);
        cardCreator.setPassword(passwordEncoder.encode(cardCreator.getPassword()));
        cardCreator.setRole("user");
        userRepository.save(cardCreator);

        String testUserEmail = "test.user@gmail.com";
        String testUserPassword = "1AmAT3stUser";
        testUser = new User("Bob", "Beetle", "", "", "",
                testUserEmail, "2000-05-21", "+64 3 555 0129",
                testAddress, testUserPassword);
        addressRepository.save(testAddress);
        testUser.setPassword(passwordEncoder.encode(testUser.getPassword()));
        testUser.setRole("user");
        userRepository.save(testUser);
    }

    //AC1
    @Given("The user has created a card")
    public void the_user_has_created_a_card() {
        testCard = new Card(cardCreator, "ForSale", "Beetle Juice", "Beetle juice from Bob",
                Collections.emptySet());
        testCardId = cardRepository.save(testCard).getId();
    }
    @When("A different user tries to edit the card")
    public void a_different_user_tries_to_edit_the_card() throws Exception {
        editCardDTO = new EditCardDTO("ForSale", "Edited Title", "This isn't my card", null);
        editCardRequest = MockMvcRequestBuilders
                .put("/cards/{id}", testCardId)
                .content(objectMapper.writeValueAsString(editCardDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));
    }
    @Then("A Forbidden exception is thrown")
    public void a_forbidden_exception_is_thrown() throws Exception {
        this.mockMvc.perform(editCardRequest)
                .andExpect(MockMvcResultMatchers.status().isForbidden()) // We expect a 403 response
                .andReturn();
    }
    @Then("The card is not edited")
    public void the_card_is_not_edited() {
        Optional<Card> editedCardOptions = cardRepository.findById(testCardId);
        Assertions.assertTrue(editedCardOptions.isPresent());
        Card editedCard = editedCardOptions.get();
        Assertions.assertEquals(testCard.getSection(), editedCard.getSection());
        Assertions.assertEquals(testCard.getTitle(), editedCard.getTitle());
        Assertions.assertEquals(testCard.getDescription(), editedCard.getDescription());
        Assertions.assertEquals(testCard.getKeywords(), editedCard.getKeywords());
    }

    //AC2
    @Given("The keywords {string} and {string} exists")
    public void the_keywords_and_exists(String keyword1, String keyword2) {
        if (keywordRepository.findByName(keyword1).isEmpty()) {
            keywordRepository.save(new Keyword(keyword1));
        }
        if (keywordRepository.findByName(keyword2).isEmpty()) {
            keywordRepository.save(new Keyword(keyword2));
        }
        Assertions.assertFalse(keywordRepository.findByName(keyword1).isEmpty());
        Assertions.assertFalse(keywordRepository.findByName(keyword2).isEmpty());
    }
    @When("The user edits the card and sets the section to {string}, the title to {string}, the description to {string} and sets the keywords to {string} and {string}")
    public void the_user_edits_the_card_and_sets_the_section_to_the_title_to_the_description_to_and_sets_the_keywords_to_and(String section, String title, String description, String keyword1, String keyword2) throws Exception {
        editedSection = section;
        editedTitle = title;
        editedDescription = description;
        List<Integer> keywordIds = new ArrayList<>();
        keywordIds.add(keywordRepository.findByName(keyword1).get(0).getId());
        keywordIds.add(keywordRepository.findByName(keyword2).get(0).getId());
        editCardDTO = new EditCardDTO(section, title, description, keywordIds);
        editCardRequest = MockMvcRequestBuilders
                .put("/cards/{id}", testCardId)
                .content(objectMapper.writeValueAsString(editCardDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(cardCreator)));
    }
    @Then("The card is successfully edited")
    public void the_card_is_successfully_edited() throws Exception{
        this.mockMvc.perform(editCardRequest)
                .andExpect(MockMvcResultMatchers.status().isOk()) // We expect a 200 response
                .andReturn();

        //Check if card was edited
        Optional<Card> editedCardOptions = cardRepository.findById(testCardId);
        Assertions.assertTrue(editedCardOptions.isPresent());
        Card editedCard = editedCardOptions.get();
        Assertions.assertEquals(editedSection, editedCard.getSection());
        Assertions.assertEquals(editedTitle, editedCard.getTitle());
        Assertions.assertEquals(editedDescription, editedCard.getDescription());
        Assertions.assertEquals(2, editedCard.getKeywords().size());
    }

    //AC4
    @Given("The system administrator exists")
    public void the_system_administrator_exists() {
        String testGAAEmail = "test.gaa@gmail.com";
        String testGAAPassword = "IAmTestGAA123";
        testGAA = new User("Bob", "Beetle", "", "", "",
                testGAAEmail, "2000-05-21", "+64 3 555 0129",
                testAddress, testGAAPassword);
        testGAA.setRole("globalApplicationAdmin");
        testGAA.setPassword(passwordEncoder.encode(testGAA.getPassword()));
        userRepository.save(testGAA);
    }
    @When("The system administrator tries to edit the card and sets the section to {string}, the title to {string}, the description to {string} and sets the keywords to {string} and {string}")
    public void the_system_administrator_tries_to_edit_the_card_and_sets_the_section_to_the_title_to_the_description_to_and_sets_the_keywords_to_and(String section, String title, String description, String keyword1, String keyword2) throws Exception {
        editedSection = section;
        editedTitle = title;
        editedDescription = description;
        List<Integer> keywordIds = new ArrayList<>();
        keywordIds.add(keywordRepository.findByName(keyword1).get(0).getId());
        keywordIds.add(keywordRepository.findByName(keyword2).get(0).getId());
        editCardDTO = new EditCardDTO(section, title, description, keywordIds);
        editCardRequest = MockMvcRequestBuilders
                .put("/cards/{id}", testCardId)
                .content(objectMapper.writeValueAsString(editCardDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testGAA)));
    }
}
