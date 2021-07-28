package gradle.cucumber.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.seng302.project.repository_layer.model.*;
import org.seng302.project.repository_layer.repository.*;
import org.seng302.project.service_layer.dto.address.AddressDTO;
import org.seng302.project.service_layer.dto.user.PostUserDTO;
import org.seng302.project.web_layer.controller.UserController;
import org.seng302.project.service_layer.exceptions.register.ExistingRegisteredEmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

public class UserRegisterSteps {

    private PostUserDTO testUserDTO;

    private final UserRepository userRepository;
    private final UserController userController;
    private final AddressRepository addressRepository;
    private final CardRepository cardRepository;
    private final MessageRepository messageRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final BusinessRepository businessRepository;
    private Integer existingRegisteredEmailExceptionCount = 0;
    private Integer requiredFieldsMissingExceptionCount = 0;
    private Integer invalidEmailExceptionCount = 0;

    private MockMvc mockMvc;
    private ResultActions reqResult;

    private final ObjectMapper objectMapper;

    @Autowired
    public UserRegisterSteps(UserRepository userRepository,
                             UserController userController,
                             AddressRepository addressRepository,
                             CardRepository cardRepository,
                             MessageRepository messageRepository,
                             UserNotificationRepository userNotificationRepository,
                             BusinessRepository businessRepository,
                             ObjectMapper  objectMapper) {
        this.userRepository = userRepository;
        this.userController = userController;
        this.addressRepository = addressRepository;
        this.cardRepository = cardRepository;
        this.messageRepository = messageRepository;
        this.userNotificationRepository = userNotificationRepository;
        this.businessRepository = businessRepository;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    @Autowired
    public void setUp(WebApplicationContext context) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Given("I am trying to register as a new User")
    public void i_am_trying_to_register_as_a_new_user() {
        userNotificationRepository.deleteAll();
        messageRepository.deleteAll();
        cardRepository.deleteAll();
        userRepository.deleteAll();
        businessRepository.deleteAll();
        addressRepository.deleteAll();

    }

    @When("I try to create the account with details:")
    public void i_try_to_create_the_account_with_details(io.cucumber.datatable.DataTable dataTable) {
        //Based off example with header and single row from:
        // https://javapointers.com/automation/cucumber/cucumber-data-tables-example-in-java/
        List<Map<String, String>> userMap = dataTable.asMaps(String.class, String.class);
        // |email       | password | firstName | lastName | dateOfBirth | homeAddress
        String email = userMap.get(0).get("email");
        String password = userMap.get(0).get("password");
        String firstName = userMap.get(0).get("firstName");
        String lastName = userMap.get(0).get("lastName");

        String dateOfBirth = userMap.get(0).get("dateOfBirth");

        //TODO: get this to use country from dataTable
        AddressDTO homeAddress = new AddressDTO(
                "", "", "", "", "New Zealand", "");

        PostUserDTO postUserDTO = new PostUserDTO(
                firstName, lastName, "", "","", email,
                dateOfBirth, "", homeAddress, password);

        userController.createUser(postUserDTO);

    }

    @Then("The account with email {string} is created.")
    public void the_account_with_email_is_created(String email) {
        List<User> retrievedUsers = userRepository.findByEmail(email);
        Assertions.assertEquals(1, retrievedUsers.size());
    }

    @Given("There a user who already is registered with the email {string}")
    public void there_a_user_who_already_is_registered_with_the_email(String email) {
        userRepository.deleteAll();
        addressRepository.deleteAll();

        Address homeAddress = new Address(
                "", "", "", "", "New Zealand", "");

        addressRepository.save(homeAddress);

        User existingUser = new User(
                "Tom", "Rizzi", "", "","", email,
                "2001-02-15", "", homeAddress, "Ab123456");

        userRepository.save(existingUser);
    }

    @When("I try to create an account with email {string}:")
    public void i_try_to_create_an_account_with_email(String email) {
        String password = "Password123";
        String firstName = "Test";
        String lastName = "User";
        String dateOfBirth = "2001-02-15";

        //TODO: get this to use country from dataTable
        AddressDTO homeAddress = new AddressDTO(
                "", "", "", "", "New Zealand", "");

        testUserDTO = new PostUserDTO(
                firstName, lastName, "", "","", email,
                dateOfBirth, "", homeAddress, password);
    }

    @Then("An error message is returned to say the email is already taken.")
    public void an_error_message_is_returned_to_say_the_email_is_already_taken() {
        Assertions.assertThrows(ExistingRegisteredEmailException.class, () -> userController.createUser(testUserDTO));
    }

    @When("I try to create an account without a password")
    public void i_try_to_create_an_account_without_a_password() throws Exception {
        String firstName = "Test";
        String lastName = "User";
        String dateOfBirth = "2001-02-15";
        String email = "testemail@gmail.com";

        AddressDTO homeAddress = new AddressDTO(
                "", "", "", "", "New Zealand", "");

        testUserDTO = new PostUserDTO(
                firstName, lastName, "", "","", email,
                dateOfBirth, "", homeAddress, "");

        reqResult = mockMvc.perform(MockMvcRequestBuilders
                .post("/users")
                .content(objectMapper.writeValueAsString(testUserDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
    }

    @Then("An error message is shown saying the password is required.")
    public void an_error_message_is_shown_saying_the_password_is_required() {
        //confirm the user was not added
        Assertions.assertTrue(userRepository.findByEmail(testUserDTO.getEmail()).isEmpty());
    }

    @When("I try to create an account with an invalid email")
    public void i_try_to_create_an_account_with_an_invalid_email() throws Exception {
        String password = "Password123";
        String firstName = "Test";
        String lastName = "User";
        String dateOfBirth = "2001-02-15";
        String email = "testemailgmail.com";

        AddressDTO homeAddress = new AddressDTO(
                "", "", "", "", "New Zealand", "");

        testUserDTO = new PostUserDTO(
                firstName, lastName, "", "","", email,
                dateOfBirth, "", homeAddress, password);

        reqResult = mockMvc.perform(MockMvcRequestBuilders
                .post("/users")
                .content(objectMapper.writeValueAsString(testUserDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
    }


    @Then("An error message is shown saying the email is invalid.")
    public void an_error_message_is_shown_saying_the_email_is_invalid() {
        //confirm the user was not added
        Assertions.assertTrue(userRepository.findByEmail(testUserDTO.getEmail()).isEmpty());
    }


}
