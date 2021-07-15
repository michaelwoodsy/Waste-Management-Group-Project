package gradle.cucumber.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.AddressRepository;
import org.seng302.project.repositoryLayer.repository.CardRepository;
import org.seng302.project.repositoryLayer.repository.KeywordRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.dto.address.AddressDTO;
import org.seng302.project.serviceLayer.dto.user.PutUserDTO;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

public class UserEditSteps extends AbstractInitializer {

    private User testUser;
    private PutUserDTO putUserDTO;

    private RequestBuilder editUserRequest;

    private MockMvc mockMvc;

    private ResultActions reqResult;


    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AddressRepository addressRepository;
    private final CardRepository cardRepository;
    private final KeywordRepository keywordRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserEditSteps(UserRepository userRepository,
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

    @BeforeEach
    @Autowired
    public void setUp(WebApplicationContext context) {
        initialise();
        cardRepository.deleteAll();
        keywordRepository.deleteAll();
        userRepository.deleteAll();

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        testUser = this.getTestUser();
        addressRepository.save(testUser.getHomeAddress());
        testUser.setPassword(passwordEncoder.encode(testUser.getPassword()));
        testUser.setRole("user");
        testUser.setId(null);//Set id to null so userRepository can set it
        userRepository.save(testUser);
    }


    //AC1: As a registered individual, I can update any of my attributes.

    @Given("I am logged in as a user")
    public void iAmLoggedInAsAUser() {
        Assertions.assertEquals(1, userRepository.findByEmail(testUser.getEmail()).size());
    }


    @When("I try to edit my account to the details:")
    public void iTryToEditMyAccountToTheDetails(io.cucumber.datatable.DataTable dataTable) throws Exception {
        List<Map<String, String>> userMap = dataTable.asMaps(String.class, String.class);
        putUserDTO = new PutUserDTO(
                testUser.getId(),
                userMap.get(0).get("firstName"),
                userMap.get(0).get("lastName"),
                userMap.get(0).get("middleName"),
                userMap.get(0).get("nickname"),
                userMap.get(0).get("bio"),
                userMap.get(0).get("email"),
                userMap.get(0).get("dateOfBirth"),
                userMap.get(0).get("phoneNumber"),
                new AddressDTO(testUser.getHomeAddress()),
                userMap.get(0).get("password"));

        this.mockMvc.perform(MockMvcRequestBuilders
                .put("/users/{id}", putUserDTO.getId())
                .content(objectMapper.writeValueAsString(putUserDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser))))
                .andExpect(MockMvcResultMatchers.status().isOk()) // We expect a 200 response
                .andReturn();
    }

    @Then("My details are updated.")
    public void myDetailsAreUpdated() {
        Optional<User> editedUserOptions = userRepository.findById(putUserDTO.getId());

        Assertions.assertTrue(editedUserOptions.isPresent());
        User editedUser = editedUserOptions.get();

        Assertions.assertEquals(putUserDTO.getFirstName(), editedUser.getFirstName());
        Assertions.assertEquals(putUserDTO.getLastName(), editedUser.getLastName());
        Assertions.assertEquals(putUserDTO.getMiddleName(), editedUser.getMiddleName());
        Assertions.assertEquals(putUserDTO.getNickname(), editedUser.getNickname());
        Assertions.assertEquals(putUserDTO.getBio(), editedUser.getBio());
        Assertions.assertEquals(putUserDTO.getEmail(), editedUser.getEmail());
        Assertions.assertEquals(putUserDTO.getDateOfBirth(), editedUser.getDateOfBirth());
        Assertions.assertEquals(putUserDTO.getPhoneNumber(), editedUser.getPhoneNumber());
        Assertions.assertEquals(putUserDTO.getHomeAddress().getCountry(), editedUser.getHomeAddress().getCountry());

        passwordEncoder.matches(putUserDTO.getPassword(), editedUser.getPassword());
    }

    
    //AC2: All validation rules still apply. For example, I can only modify my date of birth if I still remain over the required age.

    @When("I try to edit my date of birth to {int} years ago")
    public void iTryToEditMyDateOfBirthToYearsAgo(int years) throws Exception {
        putUserDTO = new PutUserDTO(
            testUser.getId(),
                testUser.getFirstName(),
                testUser.getLastName(),
                testUser.getMiddleName(),
                testUser.getNickname(),
                testUser.getBio(),
                testUser.getEmail(),
                LocalDateTime.now().minusYears(years).toLocalDate().toString(),
                testUser.getPhoneNumber(),
                new AddressDTO(testUser.getHomeAddress()),
                "Password123"
        );

        editUserRequest = MockMvcRequestBuilders
                .put("/users/{id}", putUserDTO.getId())
                .content(objectMapper.writeValueAsString(putUserDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));
    }

    @Then("An error message is returned to say the date of birth is too young.")
    public void anErrorMessageIsReturnedToSayTheDateOfBirthIsTooYoung() throws Exception {
        MvcResult editUserResponse = this.mockMvc.perform(editUserRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = editUserResponse.getResponse().getContentAsString();
        Assertions.assertEquals("DateOfBirthInvalid: This Date of Birth is not valid.", returnedExceptionString);
    }


    //AC3: Mandatory attributes still remain mandatory.

    @When("I try to edit my account and dont enter in a first name")
    public void iTryToEditMyAccountAndDontEnterInAFirstName() throws Exception {
        putUserDTO = new PutUserDTO(
                testUser.getId(),
                null,
                testUser.getLastName(),
                testUser.getMiddleName(),
                testUser.getNickname(),
                testUser.getBio(),
                testUser.getEmail(),
                testUser.getDateOfBirth(),
                testUser.getPhoneNumber(),
                new AddressDTO(testUser.getHomeAddress()),
                "Password123"
        );

        editUserRequest = MockMvcRequestBuilders
                .put("/users/{id}", putUserDTO.getId())
                .content(objectMapper.writeValueAsString(putUserDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));
    }

    @Then("An error message is shown saying the first name is required.")
    public void anErrorMessageIsShownSayingTheFirstNameIsRequired() throws Exception {
        MvcResult editUserResponse = this.mockMvc.perform(editUserRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = editUserResponse.getResponse().getContentAsString();
        Assertions.assertEquals("MissingData: First Name is a mandatory field", returnedExceptionString);
    }
}
