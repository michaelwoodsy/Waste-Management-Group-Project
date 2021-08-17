package gradle.cucumber.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.ConformationToken;
import org.seng302.project.repository_layer.model.User;
import org.seng302.project.repository_layer.repository.*;
import org.seng302.project.service_layer.dto.user.ChangePasswordDTO;
import org.seng302.project.service_layer.dto.user.LoginCredentialsDTO;
import org.seng302.project.service_layer.service.LostPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Cucumber steps for the U25 Lost password story
 */
@AutoConfigureTestDatabase
public class LostPasswordSteps extends AbstractInitializer {

    private MockMvc mockMvc;
    private final UserRepository userRepository;
    private final ConformationTokenRepository conformationTokenRepository;
    private final AddressRepository addressRepository;
    private final CardRepository cardRepository;
    private final BusinessRepository businessRepository;
    private final SaleListingRepository saleListingRepository;

    @Autowired
    private LostPasswordService lostPasswordService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private String token;
    private RequestBuilder request;


    @Autowired
    public LostPasswordSteps(UserRepository userRepository,
                             ConformationTokenRepository conformationTokenRepository,
                             AddressRepository addressRepository,
                             CardRepository cardRepository,
                             BusinessRepository businessRepository,
                             SaleListingRepository saleListingRepository){
        this.userRepository = userRepository;
        this.conformationTokenRepository = conformationTokenRepository;
        this.addressRepository = addressRepository;
        this.cardRepository = cardRepository;
        this.businessRepository = businessRepository;
        this.saleListingRepository = saleListingRepository;
    }

    /**
     * Before each test
     */
    @BeforeEach
    @Autowired
    void setup(WebApplicationContext context) {
        cardRepository.deleteAll();
        conformationTokenRepository.deleteAll();
        saleListingRepository.deleteAll();
        businessRepository.deleteAll();
        userRepository.deleteAll();
        addressRepository.deleteAll();


        this.initialise();
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        testUser = this.getTestUser();
        addressRepository.save(testUser.getHomeAddress());
        testUser.setId(null);
        userRepository.save(testUser);

    }

    @AfterEach
    void teardown() {
        conformationTokenRepository.deleteAll();
        userRepository.deleteAll();
        addressRepository.deleteAll();
    }


    //AC4
    @Given("I have requested to reset my password")
    public void i_have_requested_to_reset_my_password(){
        ConformationToken token = new ConformationToken(testUser);
        conformationTokenRepository.save(token);
    }

    @Given("I have already used the password reset URL")
    public void i_have_already_used_the_password_reset_url() throws Exception {
        var tokenOptional = conformationTokenRepository.findByUser(testUser);
        Assertions.assertTrue(tokenOptional.isPresent());
        token = tokenOptional.get().getToken();
        ChangePasswordDTO requestDTO = new ChangePasswordDTO(token, "ValidPassword123");

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/lostpassword/edit")
                .content(objectMapper.writeValueAsString(requestDTO))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request).andExpect(status().isOk());
    }

    @When("I try to use the password reset URL")
    public void i_try_to_use_the_password_reset_url() throws Exception {
        ChangePasswordDTO requestDTO = new ChangePasswordDTO(token, "ValidPassword123");

        request = MockMvcRequestBuilders
                .patch("/lostpassword/edit")
                .content(objectMapper.writeValueAsString(requestDTO))
                .contentType(MediaType.APPLICATION_JSON);
    }

    @Then("I am unable to reuse the URL")
    public void i_am_unable_to_reuse_the_url() throws Exception {
        this.mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()); // We expect a 400 response
    }

    @Given("An hour has passed")
    public void an_hour_has_passed() {
        var tokenOptional = conformationTokenRepository.findByUser(testUser);
        Assertions.assertTrue(tokenOptional.isPresent());
        var tokenObject = tokenOptional.get();
        tokenObject.setCreated(LocalDateTime.now().minusHours(1));
        lostPasswordService.removeConfirmationTokenAfter1Hr();
    }

    @Then("The URL has expired and I am unable to use it")
    public void the_url_has_expired_and_i_am_unable_to_use_it() throws Exception {
        this.mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()); // We expect a 400 response
    }

    //AC5
    @When("I try to reset my password to {string}")
    public void i_try_to_reset_my_password_to(String newPassword) throws JsonProcessingException {
        var tokenOptional = conformationTokenRepository.findByUser(testUser);
        Assertions.assertTrue(tokenOptional.isPresent());
        token = tokenOptional.get().getToken();
        ChangePasswordDTO requestDTO = new ChangePasswordDTO(token, newPassword);

        request = MockMvcRequestBuilders
                .patch("/lostpassword/edit")
                .content(objectMapper.writeValueAsString(requestDTO))
                .contentType(MediaType.APPLICATION_JSON);

    }

    @Then("Then I am informed it is not a valid password")
    public void then_i_am_informed_it_is_not_a_valid_password() throws Exception {
        var result = this.mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        String returnedExceptionString = result.getResponse().getContentAsString();
        Assertions.assertEquals("MethodArgumentNotValidException: This Password is not valid.", returnedExceptionString);
    }

    @Then("I can log in with my new password {string}")
    public void i_can_log_in_with_my_new_password(String newPassword) throws Exception {
        this.mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk());

        LoginCredentialsDTO loginCredentials = new LoginCredentialsDTO(testUser.getEmail(), newPassword);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/login")
                .content(objectMapper.writeValueAsString(loginCredentials))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }


}
