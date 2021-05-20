package gradle.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.seng302.project.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

/**
 * Cucumber steps for UCM4 - Card expiry
 */
public class CardExpirySteps {

    private User cardCreator;
    private String cardCreatorEmail;
    private String cardCreatorPassword;

    private User testUser;
    private String testUserEmail;
    private String testUserPassword;

    private MockMvc mockMvc;

    private Integer testCardId;

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AddressRepository addressRepository;
    private final CardRepository cardRepository;

    private RequestBuilder deleteCardRequest;


    @Autowired
    public CardExpirySteps(UserRepository userRepository,
                     AddressRepository addressRepository,
                     BCryptPasswordEncoder passwordEncoder,
                           CardRepository cardRepository) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.passwordEncoder = passwordEncoder;
        this.cardRepository = cardRepository;
    }

    /**
     * Initialise MockMvc and some test users
     */
    @BeforeEach
    @Autowired
    public void setup(WebApplicationContext context) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        Address testAddress = new Address("", "", "", "", "New Zealand", "");
        cardCreatorEmail = "b.beetle@gmail.com";
        cardCreatorPassword = "B0bLovesB33tles";
        User cardCreator = new User("Bob", "Beetle", "", "", "",
                cardCreatorEmail, "2000-05-21", "+64 3 555 0129",
                testAddress, cardCreatorPassword);
        addressRepository.save(testAddress);
        cardCreator.setPassword(passwordEncoder.encode(cardCreator.getPassword()));
        cardCreator.setRole("user");
        userRepository.save(cardCreator);

        testUserEmail = "test.user@gmail.com";
        testUserPassword = "1AmAT3stUser";
        User testUser = new User("Bob", "Beetle", "", "", "",
                testUserEmail, "2000-05-21", "+64 3 555 0129",
                testAddress, testUserPassword);
        addressRepository.save(testAddress);
        testUser.setPassword(passwordEncoder.encode(testUser.getPassword()));
        testUser.setRole("user");
        userRepository.save(testUser);
    }

    //AC1

    @Given("A user has created a card")
    public void a_user_has_created_a_card() {
        Card testCard = new Card(cardCreator, "ForSale", "Beetle Juice", "Beetle juice from Bob");
        testCardId = cardRepository.save(testCard).getId();
    }

    @When("That user tries to delete their card")
    public void that_user_tries_to_delete_their_card() {
        deleteCardRequest = MockMvcRequestBuilders
                .delete("/cards/{id}", testCardId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic(cardCreatorEmail, cardCreatorPassword));
    }

    @Then("The card is successfully deleted")
    public void the_card_is_successfully_deleted() throws Exception {
        this.mockMvc.perform(deleteCardRequest)
                .andExpect(MockMvcResultMatchers.status().isOk()) // We expect a 200 response
                .andReturn();

        Optional<Card> retrievedCardOptions = cardRepository.findById(testCardId);
        Assertions.assertTrue(retrievedCardOptions.isEmpty());
    }

    @When("A different user tries to delete their card")
    public void a_different_user_tries_to_delete_their_card() {
        deleteCardRequest = MockMvcRequestBuilders
                .delete("/cards/{id}", testCardId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic(testUserEmail, testUserPassword));
    }

    @Then("An appropriate exception is thrown")
    public void an_appropriate_exception_is_thrown() throws Exception {
        this.mockMvc.perform(deleteCardRequest)
                .andExpect(MockMvcResultMatchers.status().isForbidden()) // We expect a 403 response
                .andReturn();
    }

    @Then("The card is not deleted")
    public void the_card_is_not_deleted() {
        Optional<Card> retrievedCardOptions = cardRepository.findById(testCardId);
        Assertions.assertTrue(retrievedCardOptions.isPresent());
    }

    //AC2

    @Given("A system administrator exists")
    public void a_system_administrator_exists() {
        testUser.setRole("globalApplicationAdmin");
        userRepository.save(testUser);
    }

    @When("The system administrator tries to delete the card")
    public void the_system_administrator_tries_to_delete_the_card() {
        deleteCardRequest = MockMvcRequestBuilders
                .delete("/cards/{id}", testCardId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic(testUserEmail, testUserPassword));
    }
//
//    //AC4
//
//    @When("The card has an expiry date of more than {int} hours ago")
//    public void the_card_has_an_expiry_date_of_more_than_hours_ago(Integer noActionLimit) {
//        //noActionLimit = 24 (hours)
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    @Then("The card is automatically deleted")
//    public void the_card_is_automatically_deleted() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
}
