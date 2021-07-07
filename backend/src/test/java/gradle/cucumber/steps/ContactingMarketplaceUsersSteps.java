package gradle.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repositoryLayer.model.Card;
import org.seng302.project.repositoryLayer.model.Message;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.AddressRepository;
import org.seng302.project.repositoryLayer.repository.CardRepository;
import org.seng302.project.repositoryLayer.repository.MessageRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Cucumber steps for UCM8
 */
public class ContactingMarketplaceUsersSteps extends AbstractInitializer {

    private MockMvc mockMvc;
    private User testSender;
    private User testReceiver;
    private Card testCard;
    private Message testMessage;

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final MessageRepository messageRepository;

    @Autowired
    public ContactingMarketplaceUsersSteps(AddressRepository addressRepository,
                                            UserRepository userRepository,
                                           CardRepository cardRepository,
                                           MessageRepository messageRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
        this.messageRepository = messageRepository;
    }


    /**
     * Initialises entities from AbstractInitializer
     */
    @BeforeEach
    @Autowired
    void setup(WebApplicationContext context) {
        this.initialise();

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        testSender = this.getTestUser();
        addressRepository.save(testSender.getHomeAddress());
        testSender = userRepository.save(testSender);

        testReceiver = this.getTestUserBusinessAdmin();

        testCard = new Card(testReceiver, "Exchange", "Apples for your bananas",
                "Will exchange my apples for bananas", Collections.emptySet());

        testMessage = new Message("Is this still available?", testReceiver, testCard, testSender);

    }


    //AC1

    @Given("A card has been created by a user with the email {string}")
    public void a_card_has_been_created_by_a_user_with_the_email(String cardCreatorEmail) {
        testReceiver.setEmail(cardCreatorEmail);

        addressRepository.save(testReceiver.getHomeAddress());
        testReceiver = userRepository.save(testReceiver);

        testCard.setCreator(testReceiver);
        testCard = cardRepository.save(testCard);
    }

    @When("Another user messages about the card")
    public void another_user_messages_about_the_card() throws Exception {

        JSONObject requestBody = new JSONObject();
        requestBody.put("text", testMessage.getText());

        RequestBuilder createMessageRequest = MockMvcRequestBuilders
                .post("/users/{userId}/cards/{cardId}/messages",
                        testReceiver.getId(), testCard.getId())
                .content(requestBody.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testSender)));

        mockMvc.perform(createMessageRequest).andExpect(status().isCreated());
    }

    @Then("The message is sent to the user with the email {string}")
    public void the_message_is_sent_to_the_user_with_the_email(String cardCreatorEmail) {
        testMessage = messageRepository.findAllByReceiver(testReceiver).get(0);
        Assertions.assertEquals(cardCreatorEmail, testMessage.getReceiver().getEmail());
    }

    //AC2

    @When("Another user messages about the card with the message {string}")
    public void another_user_messages_about_the_card_with_the_message(String message) throws Exception {

        JSONObject requestBody = new JSONObject();
        requestBody.put("text", message);

        RequestBuilder createMessageRequest = MockMvcRequestBuilders
                .post("/users/{userId}/cards/{cardId}/messages",
                        testReceiver.getId(), testCard.getId())
                .content(requestBody.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testSender)));

        mockMvc.perform(createMessageRequest).andExpect(status().isCreated());
    }

    @Then("The card creator receives a message with the text {string}")
    public void the_card_creator_receives_a_message_with_the_text(String message) {
        testMessage = messageRepository.findAllByReceiver(testReceiver).get(0);
        Assertions.assertEquals(message, testMessage.getText());
    }

    //AC3

    @Then("The sender's name and card title are included in the message")
    public void the_sender_s_name_and_card_title_are_included_in_the_message() {
        testMessage = messageRepository.findAllByReceiver(testReceiver).get(0);
        Assertions.assertEquals(testSender.getFirstName(), testMessage.getSender().getFirstName());
        Assertions.assertEquals(testSender.getLastName(), testMessage.getSender().getLastName());
        Assertions.assertEquals(testCard.getTitle(), testMessage.getCard().getTitle());
    }

    //AC5
    @Given("A user with the email {string} messages about the card")
    public void a_user_with_the_email_messages_about_the_card(String customerEmail) throws Exception {
        testSender.setEmail(customerEmail);
        testSender = userRepository.save(testSender);

        JSONObject requestBody = new JSONObject();
        requestBody.put("text", testMessage.getText());

        RequestBuilder createMessageRequest = MockMvcRequestBuilders
                .post("/users/{userId}/cards/{cardId}/messages",
                        testReceiver.getId(), testCard.getId())
                .content(requestBody.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testSender)));

        mockMvc.perform(createMessageRequest).andExpect(status().isCreated());
    }

    @When("The card creator replies to the message")
    public void the_card_creator_replies_to_the_message() throws Exception {

        JSONObject requestBody = new JSONObject();
        requestBody.put("text","Yes it is still available");

        //The roles of sender/receiver have swapped here
        RequestBuilder createMessageRequest = MockMvcRequestBuilders
                .post("/users/{userId}/cards/{cardId}/messages",
                        testSender.getId(), testCard.getId())
                .content(requestBody.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testReceiver)));

        mockMvc.perform(createMessageRequest).andExpect(status().isCreated());
    }

    @Then("The user with the email {string} receives the reply")
    public void the_user_with_the_email_receives_the_reply(String customerEmail) {
        testMessage = messageRepository.findAllByReceiver(testSender).get(0);
        Assertions.assertEquals(customerEmail, testMessage.getReceiver().getEmail());
    }

    //AC6

//    @When("The card creator deletes the message")
//    public void the_card_creator_deletes_the_message() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//
//    @Then("The message is successfully removed")
//    public void the_message_is_successfully_removed() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
}
