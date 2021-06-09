package org.seng302.project.web_layer.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.project.repository_layer.model.User;
import org.seng302.project.repository_layer.repository.UserRepository;
import org.seng302.project.service_layer.exceptions.*;
import org.seng302.project.service_layer.exceptions.register.ExistingRegisteredEmailException;
import org.seng302.project.service_layer.exceptions.register.InvalidEmailException;
import org.seng302.project.service_layer.exceptions.register.InvalidPhoneNumberException;
import org.seng302.project.service_layer.exceptions.register.UserUnderageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Unit tests for UserController
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UserControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private MockMvc mvc;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
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
        JSONObject testUserJson = createTestUserBase();
        testUserJson.put("firstName", "John");
        testUserJson.put("lastName", "Smith");
        testUserJson.put("email", "johnsmith99@gmail.com");
        testUserJson.put("dateOfBirth", "1999-04-27");
        testUserJson.put("phoneNumber", "+64 3 555 0129");

        mvc.perform(MockMvcRequestBuilders
                .post("/users")
                .content(testUserJson.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        User retrievedUser = userRepository.findByEmail("johnsmith99@gmail.com").get(0);

        Assertions.assertNotNull(retrievedUser.getId());
        Assertions.assertEquals("John", retrievedUser.getFirstName());
        Assertions.assertEquals("Smith", retrievedUser.getLastName());
        Assertions.assertEquals("James", retrievedUser.getMiddleName());
        Assertions.assertEquals("Jonny", retrievedUser.getNickname());
        Assertions.assertEquals("Likes long walks on the beach", retrievedUser.getBio());
        Assertions.assertEquals("johnsmith99@gmail.com", retrievedUser.getEmail());
        Assertions.assertEquals("1999-04-27", retrievedUser.getDateOfBirth());
        Assertions.assertEquals("+64 3 555 0129", retrievedUser.getPhoneNumber());
        Assertions.assertEquals("New Zealand", retrievedUser.getHomeAddress().getCountry());
        Assertions.assertTrue(passwordEncoder.matches("1337-H%nt3r2", retrievedUser.getPassword()));
        Assertions.assertEquals("user", retrievedUser.getRole());
        Assertions.assertTrue(retrievedUser.getCreated().isBefore(LocalDateTime.now()));
        Assertions.assertTrue(retrievedUser.getCreated().isAfter(LocalDateTime.now().minusSeconds(5)));
        Assertions.assertTrue(retrievedUser.getBusinessesAdministered().isEmpty());
    }


    /**
     * Tries creating the same user twice. Checks that we receive a 409 response.
     */
    @Test
    public void tryCreatingExistingUser() throws Exception {

        JSONObject newTestUserJson = createTestUserBase();
        newTestUserJson.put("firstName", "John");
        newTestUserJson.put("lastName", "Smith");
        newTestUserJson.put("middleName", "James");
        newTestUserJson.put("nickname", "Jonny");
        newTestUserJson.put("bio", "Likes long walks on the beach");
        newTestUserJson.put("email", "johnsmith99@gmail.com");
        newTestUserJson.put("dateOfBirth", "1999-04-27");
        newTestUserJson.put("phoneNumber", "+64 3 555 0129");
        newTestUserJson.put("password", "1337-H%nt3r2");

        RequestBuilder postUserRequest = MockMvcRequestBuilders
                .post("/users")
                .content(newTestUserJson.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult postUserResponse = this.mvc.perform(postUserRequest)
                .andExpect(MockMvcResultMatchers.status().isConflict()) // We expect a 409 response
                .andReturn();

        String returnedExceptionString = postUserResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new ExistingRegisteredEmailException().getMessage(), returnedExceptionString);
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

        MvcResult postUserResponse = this.mvc.perform(postUserRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postUserResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new InvalidEmailException().getMessage(), returnedExceptionString);

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
        Assertions.assertEquals(new InvalidDateException().getMessage(), returnedExceptionString);
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
        Assertions.assertEquals(new InvalidPhoneNumberException().getMessage(), returnedExceptionString);
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
        Assertions.assertEquals(new UserUnderageException("an account", 13).getMessage(), returnedExceptionString);
    }

    /**
     * Tries creating a user with missing required fields: firstName, lastName, and email.
     * Checks that we receive a 400 response.
     */
    @Test
    public void tryMissingRequiredFields() throws Exception {

        JSONObject testUserJson = createTestUserBase();
        testUserJson.put("firstName", "");
        testUserJson.put("lastName", "");
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
        Assertions.assertEquals(new RequiredFieldsMissingException().getMessage(), returnedExceptionString);
    }

    /**
     * Tries to get a user by calling the /users/{id} endpoint
     * Checks that we retrieve the correct user
     */
    @Test
    public void getUser() throws Exception {

        User testUser = userRepository.findByEmail("johnsmith99@gmail.com").get(0);

        RequestBuilder getUserRequest = MockMvcRequestBuilders
                .get(String.format("/users/%d", testUser.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("johnsmith99@gmail.com", "1337-H%nt3r2"));

        MvcResult getUserResponse = this.mvc.perform(getUserRequest)
                .andExpect(MockMvcResultMatchers.status().isOk()) // We expect a 200 response
                .andReturn();

        String returnedUserString = getUserResponse.getResponse().getContentAsString();
        JSONObject returnedUser = new JSONObject(returnedUserString);

        Assertions.assertNotNull(returnedUser.getString("id"));
        Assertions.assertEquals("John", returnedUser.getString("firstName"));
        Assertions.assertEquals("Smith", returnedUser.getString("lastName"));
        Assertions.assertEquals("James", returnedUser.getString("middleName"));
        Assertions.assertEquals("Jonny", returnedUser.getString("nickname"));
        Assertions.assertEquals("Likes long walks on the beach", returnedUser.getString("bio"));
        Assertions.assertEquals("johnsmith99@gmail.com", returnedUser.getString("email"));
        Assertions.assertEquals("1999-04-27", returnedUser.getString("dateOfBirth"));
        Assertions.assertEquals("+64 3 555 0129", returnedUser.getString("phoneNumber"));
        Assertions.assertEquals("New Zealand", returnedUser.getJSONObject("homeAddress").getString("country"));
        Assertions.assertEquals("user", returnedUser.getString("role"));

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime createdTimestamp = LocalDateTime.parse(returnedUser.getString("created"), formatter);
        Assertions.assertTrue(createdTimestamp.isBefore(LocalDateTime.now()));
        Assertions.assertTrue(createdTimestamp.isAfter(LocalDateTime.now().minusSeconds(5)));
        //TODO: Come back to this when we implement businesses
        //Assertions.assertTrue(returnedUser.getBusinessesAdministered().isEmpty());

    }

}
