package gradle.cucumber.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.seng302.project.repositoryLayer.model.*;
import org.seng302.project.repositoryLayer.repository.AddressRepository;
import org.seng302.project.repositoryLayer.repository.CardRepository;
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

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class FindMyCardsSteps {

    private User testUser;
    private Address testAddress;
    private ResultActions result;
    private JSONArray results;
    private MockMvc mockMvc;

    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final AddressRepository addressRepository;

    @Autowired
    public FindMyCardsSteps(
            UserRepository userRepository,
            CardRepository cardRepository,
            AddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
        this.addressRepository = addressRepository;
    }

    @BeforeEach
    @Autowired
    public void setup(WebApplicationContext context) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        testAddress = new Address("", "", "", "", "New Zealand","");
        addressRepository.save(testAddress);
        cardRepository.deleteAll();
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

    @Given("I am logged in with email {string} and the following cards exist:")
    public void i_am_logged_in_with_email_and_the_following_cards_exist(String email, DataTable cardsTable) {
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
            User owner = new User("John", "Smith", "Bob", "Jonny",
                    "Likes long walks on the beach", "test@gmail.com", "1999-04-27",
                    "+64 3 555 0129", null, "");
            owner.setEmail(cols.get(3));
            owner = createUser(owner);

            // Create the card and set values
            newCard = new Card();
            newCard.setTitle(cols.get(0));
            newCard.setDescription(cols.get(1));
            newCard.setSection(cols.get(2));
            newCard.setCreator(owner);

            // Save the card
            cardRepository.save(newCard);
        }
    }

    @When("I search for cards created by user with email {string}")
    public void i_search_for_cards_created_by_user_with_email(String email) throws Exception {
        // Find user we are trying to get cards for
        User userToGetCards = userRepository.findByEmail(email).get(0);

        RequestBuilder getCardsReq = MockMvcRequestBuilders
                .get("/users/{id}/cards", userToGetCards.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(testUser.getEmail()));

        result = mockMvc.perform(getCardsReq)
                .andExpect(MockMvcResultMatchers.status().isOk()); // We expect a 200 response

    }
    @Then("I find {int} cards")
    public void i_find_cards(Integer cardCount) throws Exception {
       result.andExpect(jsonPath("$", hasSize(cardCount)));
    }

    @Then("All returned cards are by user with email {string}")
    public void all_returned_cards_are_by_user_with_email(String desiredEmail) throws Exception {
        // Get the json result
        String contentAsString = result.andReturn().getResponse().getContentAsString();
        JSONArray results = new JSONArray(contentAsString);

        // Iterate over the json results and check the creator email
        for (int i = 0; i < results.length(); i++) {
            // Get json for card
            JSONObject cardJson = results.getJSONObject(i);

            // Get the creator email on the card
            String email = cardJson.getJSONObject("creator").getString("email");

            // Check it is the desired email
            assertEquals(email, desiredEmail);
        }
    }



}
