package gradle.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.BeforeEach;
import org.seng302.project.repository_layer.model.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration
public class LandingPageSteps {

    private MockMvc mockMvc;
    private RequestBuilder request;


    @BeforeEach
    @Autowired
    public void setup(WebApplicationContext context) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Given("I am not logged in")
    public void iAmNotLoggedIn() {}

    @When("I navigate to the landing page")
    public void iNavigateToTheLandingPage() {
        request = MockMvcRequestBuilders
                .get("/statistics");
    }

    @Then("I am able to see the specified statistics about resale")
    public void iAmAbleToSeeTheSpecifiedStatisticsAboutResale() throws Exception {
        mockMvc.perform(request).andExpect(status().isOk());
    }

    @When("I try to contact resale with email {string} and message {string}")
    public void iTryToContactResaleWithEmailAndMessage(String email, String message) {
        String body = String.format("{\"email\": \"%s\", \"message\": \"%s\"}", email, message);
        request = MockMvcRequestBuilders
                .post("/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);
    }

    @Then("The request is successful")
    public void theRequestIsSuccessful() throws Exception {
        mockMvc.perform(request).andExpect(status().isCreated());
    }

    @When("I try to contact resale without an email or message")
    public void iTryToContactResaleWithoutAnEmailOrMessage() {
        String body = "{\"email\": \"\", \"message\": \"\"}";
        request = MockMvcRequestBuilders
                .post("/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);
    }

    @Then("The request is not successful")
    public void theRequestIsNotSuccessful() throws Exception {
        mockMvc.perform(request).andExpect(status().isBadRequest());
    }
}
