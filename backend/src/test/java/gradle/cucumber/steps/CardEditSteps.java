package gradle.cucumber.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.seng302.project.repositoryLayer.model.Address;
import org.seng302.project.repositoryLayer.model.Card;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.AddressRepository;
import org.seng302.project.repositoryLayer.repository.CardRepository;
import org.seng302.project.repositoryLayer.repository.KeywordRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.dto.card.CreateCardDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

public class CardEditSteps {

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
    List<String> keywordNames;

    private ResultActions reqResult;

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AddressRepository addressRepository;
    private final CardRepository cardRepository;
    private final ObjectMapper objectMapper;

    private final KeywordRepository keywordRepository;

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
    @Given("The user has created a card")
    public void the_user_has_created_a_card() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }


    @When("That user tries to edit their card")
    public void that_user_tries_to_edit_their_card() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("The card is successfully edited")
    public void the_card_is_successfully_edited() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    //AC1
    @When("A different user tries to edit their card")
    public void a_different_user_tries_to_edit_their_card() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("A Forbidden exception is thrown")
    public void a_forbidden_exception_is_thrown() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("The card is not edited")
    public void the_card_is_not_edited() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    //AC2
    @Given("The keywords {string} and {string} exists")
    public void the_keywords_and_exists(String keyword1, String keyword2) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @When("The user edits the card and sets the title to {string}, the description to {string} and sets the keywords to {string} and {string}")
    public void the_user_edits_the_card_and_sets_the_title_to_the_description_to_and_sets_the_keywords_to_and(String title, String description, String keyword1, String keyword2) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    //AC4
    @Given("The system administrator exists")
    public void the_system_administrator_exists() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @When("The system administrator tries to edit the card")
    public void the_system_administrator_tries_to_edit_the_card() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
}
