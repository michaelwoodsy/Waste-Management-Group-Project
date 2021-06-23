package gradle.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.seng302.project.webLayer.controller.CardController;
import org.seng302.project.repositoryLayer.model.Card;
import org.seng302.project.repositoryLayer.repository.CardRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.repositoryLayer.model.User;
import org.springframework.beans.factory.annotation.Autowired;

public class MarketplaceSectionDisplaySteps {

    private final CardController cardController;
    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    private String cardTitle;
    private String cardDescription;
    private String cardSection;
    private Integer cardId;
    private Card retrivedCard;

    @Autowired
    public MarketplaceSectionDisplaySteps(
            CardController cardController,
            UserRepository userRepository,
            CardRepository cardRepository) {
        this.cardController = cardController;
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
    }

    @Given("There exists a card with title {string} and a description {string} and a section {string}")
    public void there_exists_a_card_with_title_and_a_description_and_a_section(String title, String description, String section) {
        cardTitle = title;
        cardDescription = description;
        cardSection = section;

        User user = new User("John", "Smith", "Bob", "Jonny",
                "Likes long walks on the beach", "testEmail@email.com", "1999-04-27",
                "+64 3 555 0129", null, "Password123");
        userRepository.save(user);
        Card newCard = new Card(user, section, title, description);
        cardRepository.save(newCard);
        cardId = newCard.getId();
    }

    @When("A user gets that card")
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
