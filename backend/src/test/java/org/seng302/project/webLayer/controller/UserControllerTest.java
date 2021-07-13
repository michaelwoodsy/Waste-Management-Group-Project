package org.seng302.project.webLayer.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repositoryLayer.model.Address;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.dto.card.CreateCardDTO;
import org.seng302.project.serviceLayer.dto.user.CreateUserDTO;
import org.seng302.project.serviceLayer.dto.user.UserLoginResponseDTO;
import org.seng302.project.serviceLayer.exceptions.*;
import org.seng302.project.serviceLayer.exceptions.register.ExistingRegisteredEmailException;
import org.seng302.project.serviceLayer.exceptions.register.InvalidEmailException;
import org.seng302.project.serviceLayer.exceptions.register.InvalidPhoneNumberException;
import org.seng302.project.serviceLayer.exceptions.register.UserUnderageException;
import org.seng302.project.serviceLayer.service.UserService;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Unit tests for UserController
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UserControllerTest extends AbstractInitializer {

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    private UserService userService;

    @Autowired
    private UserController userController;

    @BeforeEach
    void setup() {
        initialise();
    }

    private JSONObject createTestUserBase() throws JSONException {
        JSONObject testUserAddress = new JSONObject();
        testUserAddress.put("country", "New Zealand");
        JSONObject testUserJson = new JSONObject();
        testUserJson.put("middleName", "James");
        testUserJson.put("nickname", "Jonny");
        testUserJson.put("bio", "Likes long walks on the beach");
        testUserJson.put("homeAddress", testUserAddress);
        testUserJson.put("password", "1337-H%nt3r2");

        return testUserJson;
    }


    /**
     * Creates the test user from the API by calling the POST '/users' endpoint.
     * Then retrieves the new User from the repository and checks all of their attributes.
     * (Except the businessesAdministered attribute)
     */
    @Test
    public void createAndRetrieveTestUser() throws Exception {
        when(userService.createUser(any(CreateUserDTO.class))).thenReturn(new UserLoginResponseDTO(1));

        JSONObject testUserJson = createTestUserBase();
        testUserJson.put("firstName", "John");
        testUserJson.put("lastName", "Smith");
        testUserJson.put("email", "johnsmith99@gmail.com");
        testUserJson.put("dateOfBirth", "1999-04-27");
        testUserJson.put("phoneNumber", "+64 3 555 0129");

        this.mvc.perform(MockMvcRequestBuilders
                .post("/users")
                .content(testUserJson.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

    }

    /**
     * Tries creating a user with an invalid email address.
     * Checks that we receive a 400 response.
     */
    @Test
    public void tryInvalidEmailAddress() throws Exception {

        JSONObject testUserJson = createTestUserBase();
        testUserJson.put("firstName", "John");
        testUserJson.put("lastName", "Smith");
        testUserJson.put("email", "johngmail.com");
        testUserJson.put("dateOfBirth", "1999-04-27");
        testUserJson.put("phoneNumber", "+64 3 555 0129");

        RequestBuilder postUserRequest = MockMvcRequestBuilders
                .post("/users")
                .content(testUserJson.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        this.mvc.perform(postUserRequest).andReturn();

        MvcResult postUserResponse = this.mvc.perform(postUserRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postUserResponse.getResponse().getContentAsString();
        Assertions.assertEquals("EmailInvalid: This Email is not valid.", returnedExceptionString);

    }

    /**
     * Tries creating a user with an invalid user date as Date of Birth.
     * Checks that we receive a 400 response.
     */
    @Test
    public void tryInvalidDate() throws Exception {

        JSONObject testUserJson = createTestUserBase();
        testUserJson.put("firstName", "John");
        testUserJson.put("lastName", "Smith");
        testUserJson.put("email", "john@gmail.com");
        testUserJson.put("dateOfBirth", "invalid-date");
        testUserJson.put("phoneNumber", "+64 3 555 0129");

        RequestBuilder postUserRequest = MockMvcRequestBuilders
                .post("/users")
                .content(testUserJson.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult postUserResponse = this.mvc.perform(postUserRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postUserResponse.getResponse().getContentAsString();
        Assertions.assertEquals("DateOfBirthInvalid: This Date of Birth is not valid.", returnedExceptionString);
    }

    /**
     * Tries creating a user with an invalid user phone number.
     * Checks that we receive a 400 response.
     */
    @Test
    public void tryInvalidPhoneNumber() throws Exception {

        JSONObject testUserJson = createTestUserBase();
        testUserJson.put("firstName", "John");
        testUserJson.put("lastName", "Smith");
        testUserJson.put("email", "john@gmail.com");
        testUserJson.put("dateOfBirth", "1999-04-27");
        testUserJson.put("phoneNumber", "hello");

        RequestBuilder postUserRequest = MockMvcRequestBuilders
                .post("/users")
                .content(testUserJson.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult postUserResponse = this.mvc.perform(postUserRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postUserResponse.getResponse().getContentAsString();
        Assertions.assertEquals("InvalidPhoneNumber: This Phone Number is not valid.", returnedExceptionString);
    }

    /**
     * Tries creating a user with an invalid user phone number.
     * Checks that we receive a 400 response.
     */
    @Test
    public void tryCreateUnderageUser() throws Exception {

        JSONObject testUserJson = createTestUserBase();
        testUserJson.put("firstName", "John");
        testUserJson.put("lastName", "Smith");
        testUserJson.put("email", "john@gmail.com");
        testUserJson.put("dateOfBirth", "2015-04-27");
        testUserJson.put("phoneNumber", "+64 3 555 0129");

        RequestBuilder postUserRequest = MockMvcRequestBuilders
                .post("/users")
                .content(testUserJson.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult postUserResponse = this.mvc.perform(postUserRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postUserResponse.getResponse().getContentAsString();
        Assertions.assertEquals("DateOfBirthInvalid: This Date of Birth is not valid.", returnedExceptionString);
    }

    /**
     * Tries creating a user with missing required fields: firstName, lastName, and email.
     * Checks that we receive a 400 response.
     */
    @Test
    public void tryMissingRequiredFields() throws Exception {

        JSONObject testUserJson = createTestUserBase();
        testUserJson.put("firstName", "");
        testUserJson.put("lastName", "Smith");
        testUserJson.put("email", "john@gmail.com");
        testUserJson.put("dateOfBirth", "1999-04-27");
        testUserJson.put("phoneNumber", "+64 3 555 0129");

        RequestBuilder postUserRequest = MockMvcRequestBuilders
                .post("/users")
                .content(testUserJson.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult postUserResponse = this.mvc.perform(postUserRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postUserResponse.getResponse().getContentAsString();
        Assertions.assertEquals("MissingData: First Name is a mandatory field", returnedExceptionString);
    }

    /**
     * Tries to get a user by calling the /users/{id} endpoint
     * Checks that we retrieve the correct user
     */
    @Test
    public void getUser() throws Exception {
        when(userRepository.findByEmail("johnsmith99@gmail.com")).thenReturn(List.of(this.getTestUser()));

        User testUser = userRepository.findByEmail("johnsmith99@gmail.com").get(0);

        RequestBuilder getUserRequest = MockMvcRequestBuilders
                .get(String.format("/users/%d", testUser.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        mvc.perform(getUserRequest).andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     * Test the user must be authorised
     */
    @Test
    void userSearch_notLoggedIn_401() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .get("/users/search")
                .param("searchQuery", ""))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    /**
     * Checks the search query is passed to the UserService.
     */
    @Test
    void userSearch_withQueryParam_queryPassedToService() throws Exception {
        // Mock the searchUsers method to return an empty list
        when(userService.searchUsers(any(String.class))).thenReturn(List.of());

        // Make the request
        mvc.perform(MockMvcRequestBuilders
                .get("/users/search")
                .param("searchQuery", "testquery string")
                .with(user(new AppUserDetails(getTestUser()))));

        // Capture query string passed to UserService method
        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        verify(userService).searchUsers(queryCaptor.capture());

        // Check the query string is correct
        Assertions.assertEquals("testquery string", queryCaptor.getValue());
    }

    /**
     * Check a 200 is returned for a valid search
     */
    @Test
    void userSearch_blueSky_200() throws Exception {
        // Mock the searchUsers method to return an empty list
        when(userService.searchUsers(any(String.class))).thenReturn(List.of());

        // Make the request, and check it is 200
        mvc.perform(MockMvcRequestBuilders
                .get("/users/search")
                .param("searchQuery", "testquery string")
                .with(user(new AppUserDetails(getTestUser()))))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
