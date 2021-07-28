package gradle.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.seng302.project.repository_layer.model.Address;
import org.seng302.project.repository_layer.model.Card;
import org.seng302.project.repository_layer.model.User;
import org.seng302.project.repository_layer.repository.AddressRepository;
import org.seng302.project.repository_layer.repository.CardRepository;
import org.seng302.project.repository_layer.repository.UserRepository;
import org.seng302.project.service_layer.dto.card.GetCardResponseDTO;
import org.seng302.project.web_layer.controller.CardController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

public class MarketplaceSectionDisplaySteps {

    private final CardController cardController;
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    private String cardTitle;
    private String cardDescription;
    private String cardSection;
    private Integer cardId;
    private GetCardResponseDTO retrivedCard;

    @Autowired
    public MarketplaceSectionDisplaySteps(
            CardController cardController,
            UserRepository userRepository,
            CardRepository cardRepository,
            AddressRepository addressRepository
    ) {
        this.cardController = cardController;
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
        this.addressRepository = addressRepository;
    }

    @Given("There exists a card with title {string} and a description {string} and a section {string}")
    public void there_exists_a_card_with_title_and_a_description_and_a_section(String title, String description, String section) {
        cardTitle = title;
        cardDescription = description;
        cardSection = section;

        Address address = new Address();
        address.setCountry("New Zealand");
        User user = new User("John", "Smith", "Bob", "Jonny",
                "Likes long walks on the beach", "testEmail@email.com", "1999-04-27",
                "+64 3 555 0129", address, "Password123");
        addressRepository.save(user.getHomeAddress());
        userRepository.save(user);
        Card newCard = new Card(user, section, title, description, Collections.emptySet());
        cardRepository.save(newCard);
        cardId = newCard.getId();
    }

    @When("A user gets that card")
    @Transactional
    public void a_user_gets_that_card() {
        retrivedCard = cardController.getCard(cardId);
    }

    @Then("The card information is provided")
    public void the_card_information_is_provided() {
        Assertions.assertEquals(cardTitle, retrivedCard.getTitle());
        Assertions.assertEquals(cardDescription, retrivedCard.getDescription());
        Assertions.assertEquals(cardSection, retrivedCard.getSection());
    }
}
