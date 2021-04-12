package org.seng302.project.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.jupiter.api.*;
import org.seng302.project.exceptions.*;
import org.seng302.project.model.Business;
import org.seng302.project.model.BusinessRepository;
import org.seng302.project.model.User;
import org.seng302.project.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
 * Unit tests for BusinessController
 */

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class BusinessControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BusinessRepository businessRepository;

    private MockMvc mvc;

    @BeforeEach
    public void setup() {
        //Had to remove security because needed to create a business with no one logged in
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    private JSONObject createTestBusiness() throws JSONException {
        JSONObject testBusinessJson = new JSONObject();
        testBusinessJson.put("primaryAdministratorId", 20);
        testBusinessJson.put("name", "Lumbridge General Store");
        testBusinessJson.put("description", "A one-stop shop for all your adventuring needs");
        testBusinessJson.put("address", "3/24 Ilam Road, Christchurch, Canterbury, New Zealand, 90210");
        testBusinessJson.put("businessType", "Accommodation and Food Services");

        return testBusinessJson;
    }

    private void createLoggedInUser() throws Exception {
        JSONObject testUserJson = new JSONObject();
        testUserJson.put("firstName", "Jim");
        testUserJson.put("lastName", "Smith");
        testUserJson.put("email", "jimsmith@gmail.com");
        testUserJson.put("dateOfBirth", "1999-04-27");
        testUserJson.put("homeAddress", "4 Rountree Street, Upper Riccarton");
        testUserJson.put("password", "1337-H%nt3r2");

        testUserJson.put("middleName", "");
        testUserJson.put("nickname", "");
        testUserJson.put("bio", "");
        testUserJson.put("phoneNumber", "");

        mvc.perform(MockMvcRequestBuilders
                .post("/users")
                .content(testUserJson.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }


    /**
     * Creates the test business from the API by calling the POST '/businesses' endpoint.
     * Then retrieves the new Business from the repository and checks all of its attributes.
     */
    @Test
    @Order(1)
    public void createAndRetrieveTestBusiness() throws Exception {
        //Create a user and log them in
        createLoggedInUser();

        JSONObject testBusinessJson = createTestBusiness();

        User loggedInUser = userRepository.findByEmail("jimsmith@gmail.com").get(0);

        testBusinessJson.put("primaryAdministratorId", loggedInUser.getId());

        mvc.perform(MockMvcRequestBuilders
                        .post("/businesses")
                        .content(testBusinessJson.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(httpBasic("jimsmith@gmail.com", "1337-H%nt3r2")))
                        .andExpect(status().isCreated());



        Business retrievedBusiness = businessRepository.findByName("Lumbridge General Store").get(0);

        Assertions.assertNotNull(retrievedBusiness.getId());
        Assertions.assertEquals(loggedInUser.getId(), retrievedBusiness.getPrimaryAdministratorId());
        Assertions.assertEquals(loggedInUser.getId(), retrievedBusiness.getAdministrators().get(0).getId());
        Assertions.assertEquals("Lumbridge General Store", retrievedBusiness.getName());
        Assertions.assertEquals("A one-stop shop for all your adventuring needs", retrievedBusiness.getDescription());
        Assertions.assertEquals("3/24 Ilam Road, Christchurch, Canterbury, New Zealand, 90210", retrievedBusiness.getAddress());
        Assertions.assertEquals("Accommodation and Food Services", retrievedBusiness.getBusinessType());


        Assertions.assertTrue(retrievedBusiness.getCreated().isBefore(LocalDateTime.now()));
        Assertions.assertTrue(retrievedBusiness.getCreated().isAfter(LocalDateTime.now().minusSeconds(5)));
    }


    /**
     * Tries creating a business with missing required fields: name, address, and businessType.
     * Checks that we receive a 400 response.
     */
    @Test
    @Order(2)
    public void tryMissingRequiredFields() throws Exception {

        JSONObject testBusiness = createTestBusiness();
        User loggedInUser = userRepository.findByEmail("jimsmith@gmail.com").get(0);
        testBusiness.put("primaryAdministratorId", loggedInUser.getId());

        //Name field empty
        testBusiness.put("name", "");
        RequestBuilder postUserRequest = MockMvcRequestBuilders
                .post("/businesses")
                .content(testBusiness.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("jimsmith@gmail.com", "1337-H%nt3r2"));

        MvcResult postUserResponse = this.mvc.perform(postUserRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postUserResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new RequiredFieldsMissingException().getMessage(), returnedExceptionString);

        //address field empty
        testBusiness = createTestBusiness();
        testBusiness.put("primaryAdministratorId", loggedInUser.getId());

        testBusiness.put("address", "");
        postUserRequest = MockMvcRequestBuilders
                .post("/businesses")
                .content(testBusiness.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("jimsmith@gmail.com", "1337-H%nt3r2"));

        postUserResponse = this.mvc.perform(postUserRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        returnedExceptionString = postUserResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new RequiredFieldsMissingException().getMessage(), returnedExceptionString);

        //businessType field empty
        testBusiness = createTestBusiness();
        testBusiness.put("primaryAdministratorId", loggedInUser.getId());

        testBusiness.put("businessType", "");
        postUserRequest = MockMvcRequestBuilders
                .post("/businesses")
                .content(testBusiness.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("jimsmith@gmail.com", "1337-H%nt3r2"));

        postUserResponse = this.mvc.perform(postUserRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        returnedExceptionString = postUserResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new RequiredFieldsMissingException().getMessage(), returnedExceptionString);

        //All required fields empty (businessType is tested after)
        testBusiness = createTestBusiness();
        testBusiness.put("primaryAdministratorId", loggedInUser.getId());

        testBusiness.put("name", "");
        testBusiness.put("address", "");
        testBusiness.put("bussinessType", "");
        postUserRequest = MockMvcRequestBuilders
                .post("/businesses")
                .content(testBusiness.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("jimsmith@gmail.com", "1337-H%nt3r2"));

        postUserResponse = this.mvc.perform(postUserRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        returnedExceptionString = postUserResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new RequiredFieldsMissingException().getMessage(), returnedExceptionString);
    }

    /**
     * Tries to get a business by calling the /businesses/{id} endpoint
     * Checks that we retrieve the correct user
     */
    @Test
    @Order(3)
    public void getBusiness() throws Exception {

        Business testBusiness = businessRepository.findByName("Lumbridge General Store").get(0);

        RequestBuilder getBusinessRequest = MockMvcRequestBuilders
                .get(String.format("/businesses/%d", testBusiness.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("jimsmith@gmail.com", "1337-H%nt3r2"));

        MvcResult getBusinessResponse = this.mvc.perform(getBusinessRequest)
                .andExpect(MockMvcResultMatchers.status().isOk()) // We expect a 200 response
                .andReturn();

        String returnedBusinessString = getBusinessResponse.getResponse().getContentAsString();
        JSONObject returnedBusiness = new JSONObject(returnedBusinessString);

        User loggedInUser = userRepository.findByEmail("jimsmith@gmail.com").get(0);

        Assertions.assertNotNull(returnedBusiness.getString("id"));
        Assertions.assertEquals("Lumbridge General Store", returnedBusiness.getString("name"));
        Assertions.assertEquals(loggedInUser.getId().toString(), returnedBusiness.getString("primaryAdministratorId"));
        Assertions.assertEquals("A one-stop shop for all your adventuring needs", returnedBusiness.getString("description"));
        Assertions.assertEquals("3/24 Ilam Road, Christchurch, Canterbury, New Zealand, 90210", returnedBusiness.getString("address"));
        Assertions.assertEquals("Accommodation and Food Services", returnedBusiness.getString("businessType"));


        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime createdTimestamp = LocalDateTime.parse(returnedBusiness.getString("created"), formatter);
        Assertions.assertTrue(createdTimestamp.isBefore(LocalDateTime.now()));
        Assertions.assertTrue(createdTimestamp.isAfter(LocalDateTime.now().minusSeconds(5)));
    }


    /**
     * Tries to add an administrator to a business by calling the /businesses/{id}/makeAdministrator endpoint
     * Checks that the user is added as an administrator
     */
    @Test
    @Order(4)
    public void addAdministrator() throws Exception {
        //Create Test user to add as administrator
        JSONObject testUserJson = new JSONObject();
        testUserJson.put("firstName", "Dave");
        testUserJson.put("lastName", "Sims");
        testUserJson.put("email", "DaveSims@gmail.com");
        testUserJson.put("dateOfBirth", "1998-04-27");
        testUserJson.put("homeAddress", "6 Rountree Street, Upper Riccarton");
        testUserJson.put("password", "1337-H%nt3r2");

        testUserJson.put("middleName", "");
        testUserJson.put("nickname", "");
        testUserJson.put("bio", "");
        testUserJson.put("phoneNumber", "");

        mvc.perform(MockMvcRequestBuilders
                .post("/users")
                .content(testUserJson.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("jimsmith@gmail.com", "1337-H%nt3r2")))
                .andExpect(status().isCreated());

        Business retrievedBusiness = businessRepository.findByName("Lumbridge General Store").get(0);

        Assertions.assertEquals(1, retrievedBusiness.getAdministrators().size());
        Assertions.assertEquals("jimsmith@gmail.com", retrievedBusiness.getAdministrators().get(0).getEmail());


        User retrievedUser = userRepository.findByEmail("DaveSims@gmail.com").get(0);

        JSONObject loginCredentials = new JSONObject();
        loginCredentials.put("email", "jimsmith@gmail.com");
        loginCredentials.put("password", "1337-H%nt3r2");

        //Log back into the main admin account
        mvc.perform(MockMvcRequestBuilders
                .post("/login")
                .content(loginCredentials.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        JSONObject testAdmin = new JSONObject();
        testAdmin.put("userId", retrievedUser.getId());

        mvc.perform(MockMvcRequestBuilders
                .put(String.format("/businesses/%d/makeAdministrator", retrievedBusiness.getId()))
                .content(testAdmin.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("jimsmith@gmail.com", "1337-H%nt3r2")))
                .andExpect(status().isOk());

        retrievedBusiness = businessRepository.findByName("Lumbridge General Store").get(0);

        Assertions.assertEquals(2, retrievedBusiness.getAdministrators().size());
        Assertions.assertEquals("jimsmith@gmail.com", retrievedBusiness.getAdministrators().get(0).getEmail());
        Assertions.assertEquals("DaveSims@gmail.com", retrievedBusiness.getAdministrators().get(1).getEmail());


        //Trying to add the same admin again
        RequestBuilder addAdminRequest = MockMvcRequestBuilders
                .put(String.format("/businesses/%d/makeAdministrator", retrievedBusiness.getId()))
                .content(testAdmin.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("jimsmith@gmail.com", "1337-H%nt3r2"));

        MvcResult addAdminResponse = this.mvc.perform(addAdminRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = addAdminResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new AdministratorAlreadyExistsException(retrievedUser.getId(), retrievedBusiness.getId()).getMessage(), returnedExceptionString);
    }

    /**
     * Tries to remove an administrator of a business by calling the /businesses/{id}/removeAdministrator endpoint
     * Checks that the user is removed as an administrator
     */
    @Test
    @Order(5)
    public void removeAdministrator() throws Exception {
        Business retrievedBusiness = businessRepository.findByName("Lumbridge General Store").get(0);
        User retrievedUser = userRepository.findByEmail("DaveSims@gmail.com").get(0);
        User primaryAdmin = userRepository.findByEmail("jimsmith@gmail.com").get(0);

        //Login to Primary admin account
        JSONObject loginCredentials = new JSONObject();
        loginCredentials.put("email", "jimsmith@gmail.com");
        loginCredentials.put("password", "1337-H%nt3r2");

        //Log back into the main admin account
        mvc.perform(MockMvcRequestBuilders
                .post("/login")
                .content(loginCredentials.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());



        //Trying to remove the primary administrator
        JSONObject testAdmin = new JSONObject();
        testAdmin.put("userId", primaryAdmin.getId());

        RequestBuilder removeAdminRequest = MockMvcRequestBuilders
                .put(String.format("/businesses/%d/removeAdministrator", retrievedBusiness.getId()))
                .content(testAdmin.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("jimsmith@gmail.com", "1337-H%nt3r2"));

        MvcResult removeAdminResponse = this.mvc.perform(removeAdminRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = removeAdminResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new CantRemoveAdministratorException(primaryAdmin.getId(), retrievedBusiness.getId()).getMessage(), returnedExceptionString);


        //Removing an actual admin
        testAdmin = new JSONObject();
        testAdmin.put("userId", retrievedUser.getId());

        mvc.perform(MockMvcRequestBuilders
                .put(String.format("/businesses/%d/removeAdministrator", retrievedBusiness.getId()))
                .content(testAdmin.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("jimsmith@gmail.com", "1337-H%nt3r2")))
                .andExpect(status().isOk());

        //Get the business after it has been updated
        retrievedBusiness = businessRepository.findByName("Lumbridge General Store").get(0);

        Assertions.assertEquals(1, retrievedBusiness.getAdministrators().size());
        Assertions.assertEquals("jimsmith@gmail.com", retrievedBusiness.getAdministrators().get(0).getEmail());



        //Trying to remove a user who is not an admin (The user that was just removed as an admin)
        testAdmin = new JSONObject();
        testAdmin.put("userId", retrievedUser.getId());

        removeAdminRequest = MockMvcRequestBuilders
                .put(String.format("/businesses/%d/removeAdministrator", retrievedBusiness.getId()))
                .content(testAdmin.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("jimsmith@gmail.com", "1337-H%nt3r2"));

        removeAdminResponse = this.mvc.perform(removeAdminRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        returnedExceptionString = removeAdminResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new UserNotAdministratorException(retrievedUser.getId(), retrievedBusiness.getId()).getMessage(), returnedExceptionString);
    }
}