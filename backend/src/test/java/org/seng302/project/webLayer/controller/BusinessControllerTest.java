package org.seng302.project.webLayer.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.seng302.project.repositoryLayer.model.Address;
import org.seng302.project.repositoryLayer.model.Business;
import org.seng302.project.repositoryLayer.repository.AddressRepository;
import org.seng302.project.repositoryLayer.repository.BusinessRepository;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.exceptions.*;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.AdministratorAlreadyExistsException;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.CantRemoveAdministratorException;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.ForbiddenPrimaryAdministratorActionException;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.UserNotAdministratorException;
import org.seng302.project.serviceLayer.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Unit tests for BusinessController
 */


@WebMvcTest(BusinessController.class)
class BusinessControllerTest {

    @MockBean
    private AddressRepository addressRepository;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    private BusinessRepository businessRepository;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private BusinessController businessController;

    @MockBean
    private BusinessService businessService;

    private User testPrimaryAdmin;
    private User testUser;
    private Business testBusiness;

    @BeforeEach
    public void setup() {

        //Mock a test user to be used as business primary admin
        testPrimaryAdmin = new User("Jim", "Smith", "", "", "",
                "jimsmith@gmail.com", "1999-04-27", "",
                null, "1337-H%nt3r2");

        testPrimaryAdmin.setId(1);
        testPrimaryAdmin.setPassword(passwordEncoder.encode(testPrimaryAdmin.getPassword()));
        given(userRepository.findByEmail("jimsmith@gmail.com")).willReturn(List.of(testPrimaryAdmin));
        given(userRepository.findById(1)).willReturn(Optional.of(testPrimaryAdmin));

        //Mock a different test user
        testUser = new User("Dave", "Sims", "", "", "",
                "DaveSims@gmail.com", "1998-04-27", "",
                null, "1337-H%nt3r2");

        testUser.setId(2);
        testUser.setPassword(passwordEncoder.encode(testUser.getPassword()));
        given(userRepository.findByEmail("DaveSims@gmail.com")).willReturn(List.of(testUser));
        given(userRepository.findById(2)).willReturn(Optional.of(testUser));

        //Mock a test business
        //Spy on this business so we can check when methods on the business object are called
        testBusiness = Mockito.spy(new Business("Lumbridge General Store", "A one-stop shop for all your adventuring needs",
                null, "Accommodation and Food Services", 1));
        testBusiness.setId(1);
        given(businessRepository.findByName("Lumbridge General Store")).willReturn(List.of(testBusiness));
        given(businessRepository.findById(1)).willReturn(Optional.of(testBusiness));

    }

    private JSONObject getTestBusiness() throws JSONException {
        JSONObject testAddress = new JSONObject();
        testAddress.put("country", "New Zealand");
        JSONObject testBusinessJson = new JSONObject();
        testBusinessJson.put("primaryAdministratorId", 20);
        testBusinessJson.put("name", "Lumbridge General Store");
        testBusinessJson.put("description", "A one-stop shop for all your adventuring needs");
        testBusinessJson.put("address", testAddress);
        testBusinessJson.put("businessType", "Accommodation and Food Services");

        return testBusinessJson;
    }


    /**
     * Creates the test business from the API by calling the POST '/businesses' endpoint.
     * Expects a 201 response
     */
    @Test
    void createBusiness_validFields_201() throws Exception {

        JSONObject testBusinessJson = getTestBusiness();

        testBusinessJson.put("primaryAdministratorId", testPrimaryAdmin.getId());

        mvc.perform(MockMvcRequestBuilders
                .post("/businesses")
                .content(testBusinessJson.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("jimsmith@gmail.com", "1337-H%nt3r2")))
                .andExpect(status().isCreated());

    }


    /**
     * Tries creating a business with missing required field: name
     * Checks that we receive a 400 response with message from DTO
     */
    @Test
    void createBusiness_nameFieldEmpty_400() throws Exception {

        JSONObject testBusiness = getTestBusiness();
        testBusiness.put("primaryAdministratorId", testPrimaryAdmin.getId());

        //Name field empty
        testBusiness.put("name", "");
        RequestBuilder postBusinessRequest = MockMvcRequestBuilders
                .post("/businesses")
                .content(testBusiness.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("jimsmith@gmail.com", "1337-H%nt3r2"));

        MvcResult postUserResponse = this.mvc.perform(postBusinessRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postUserResponse.getResponse().getContentAsString();
        Assertions.assertEquals("Business name is a mandatory field", returnedExceptionString);
    }


    /**
     * Tries creating a business with missing required field: address
     * Checks that we receive a 400 response.
     */
    @Test
    void createBusiness_addressFieldEmpty_400() throws Exception {

        JSONObject testAddress = new JSONObject();
        testAddress.put("country", "");
        JSONObject testBusiness = getTestBusiness();
        testBusiness.put("primaryAdministratorId", testPrimaryAdmin.getId());
        testBusiness.put("address", testAddress);
        RequestBuilder postBusinessRequest = MockMvcRequestBuilders
                .post("/businesses")
                .content(testBusiness.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("jimsmith@gmail.com", "1337-H%nt3r2"));

        MvcResult postUserResponse = this.mvc.perform(postBusinessRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postUserResponse.getResponse().getContentAsString();
        Assertions.assertEquals("Address format is incorrect. A country must be included." +
                " If a street number is given, a street name must be provided", returnedExceptionString);
    }


    /**
     * Tries creating a business with missing required field: businessType
     * Checks that we receive a 400 response with a message from the DTO
     */
    @Test
    void createBusiness_typeFieldEmpty_400() throws Exception {

        JSONObject testBusiness = getTestBusiness();
        testBusiness.put("primaryAdministratorId", testPrimaryAdmin.getId());

        testBusiness.put("businessType", "");
        RequestBuilder postBusinessRequest = MockMvcRequestBuilders
                .post("/businesses")
                .content(testBusiness.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("jimsmith@gmail.com", "1337-H%nt3r2"));

        MvcResult postUserResponse = this.mvc.perform(postBusinessRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postUserResponse.getResponse().getContentAsString();
        Assertions.assertTrue(returnedExceptionString.equals("Business type is a mandatory field") ||
                returnedExceptionString.equals("Invalid business type provided"));
    }


    /**
     * Tries creating a business with all required fields missing: name, address, type
     * Checks that we receive a 400 response with a message from the DTO
     */
    @Test
    void createBusiness_allRequiredFieldsEmpty_400() throws Exception {

        JSONObject testAddress = new JSONObject();
        testAddress.put("country", "");
        JSONObject testBusiness = getTestBusiness();
        testBusiness.put("primaryAdministratorId", testPrimaryAdmin.getId());
        testBusiness.put("name", "");
        testBusiness.put("address", testAddress);
        testBusiness.put("businessType", "");

        RequestBuilder postBusinessRequest = MockMvcRequestBuilders
                .post("/businesses")
                .content(testBusiness.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("jimsmith@gmail.com", "1337-H%nt3r2"));

        this.mvc.perform(postBusinessRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();
    }

    /**
     * Tries creating a business with an invalid business type
     * Checks that we receive a 400 response with a message from the DTO
     */
    @Test
    void createBusiness_invalidType_400() throws Exception {

        JSONObject testBusiness = getTestBusiness();
        testBusiness.put("primaryAdministratorId", testPrimaryAdmin.getId());

        testBusiness.put("businessType", "My New Type");
        RequestBuilder postBusinessRequest = MockMvcRequestBuilders
                .post("/businesses")
                .content(testBusiness.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("jimsmith@gmail.com", "1337-H%nt3r2"));

        MvcResult postUserResponse = this.mvc.perform(postBusinessRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postUserResponse.getResponse().getContentAsString();
        Assertions.assertEquals("Invalid business type provided", returnedExceptionString);
    }



    /**
     * Tries creating a business where address has a street number but no street name
     * Checks that we receive a 400 response with a message from the DTO
     */
    @Test
    void createBusiness_streetNumberNoStreetName_400() throws Exception {

        JSONObject testBusiness = getTestBusiness();
        testBusiness.put("primaryAdministratorId", testPrimaryAdmin.getId());

        JSONObject testAddress = new JSONObject();
        testAddress.put("streetNumber", "17");
        testAddress.put("streetName", "");

        testBusiness.put("address", testAddress);

        RequestBuilder postBusinessRequest = MockMvcRequestBuilders
                .post("/businesses")
                .content(testBusiness.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("jimsmith@gmail.com", "1337-H%nt3r2"));

        MvcResult postUserResponse = this.mvc.perform(postBusinessRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postUserResponse.getResponse().getContentAsString();
        Assertions.assertEquals("Address format is incorrect. A country must be included." +
                " If a street number is given, a street name must be provided", returnedExceptionString);
    }



    /**
     * Tries to get a business by calling the /businesses/{id} endpoint
     * Checks that we retrieve the correct business
     */
    @Test
    @Order(6)
    void getBusiness() {

        Business returnedBusiness = businessController.getBusiness(1);

        Assertions.assertEquals(1, returnedBusiness.getId());
        Assertions.assertEquals("Lumbridge General Store", returnedBusiness.getName());
        Assertions.assertEquals(testPrimaryAdmin.getId(), returnedBusiness.getPrimaryAdministratorId());
        Assertions.assertEquals("A one-stop shop for all your adventuring needs", returnedBusiness.getDescription());
        Assertions.assertEquals("Accommodation and Food Services", returnedBusiness.getBusinessType());
    }




    /**
     * Tries to add an administrator to a business by calling the /businesses/{id}/makeAdministrator endpoint
     * Checks that the user is added as an administrator
     */
    @Test
    @Order(7)
    void addAdministrator() throws Exception {

        JSONObject testUserJson = new JSONObject();
        testUserJson.put("userId", testUser.getId());

        mvc.perform(MockMvcRequestBuilders
                .put(String.format("/businesses/%d/makeAdministrator", testBusiness.getId()))
                .content(testUserJson.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("jimsmith@gmail.com", "1337-H%nt3r2")))
                .andExpect(status().isOk());

        //Check that add administrator method called with user 2
        ArgumentCaptor<User> addedAdministratorCaptor = ArgumentCaptor.forClass(User.class);
        verify(testBusiness).addAdministrator(addedAdministratorCaptor.capture());

        User addedAdmin = addedAdministratorCaptor.getValue();

        Assertions.assertEquals(testUser.getEmail(), addedAdmin.getEmail());

    }


    /**
     * Tries to add an administrator to a business when they are already an administrator
     * Checks that a AdministratorAlreadyExistsException is sent, a 400 response
     */
    @Test
    @Order(8)
    void addSameAdministrator() throws Exception {

        given(testBusiness.getAdministrators()).willReturn(List.of(testUser));

        JSONObject newAdmin = new JSONObject();
        newAdmin.put("userId", testUser.getId());

        //Trying to add the same admin again
        RequestBuilder addAdminRequest = MockMvcRequestBuilders
                .put(String.format("/businesses/%d/makeAdministrator", testBusiness.getId()))
                .content(newAdmin.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("jimsmith@gmail.com", "1337-H%nt3r2"));

        MvcResult addAdminResponse = this.mvc.perform(addAdminRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = addAdminResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new AdministratorAlreadyExistsException(testUser.getId(), testBusiness.getId()).getMessage(), returnedExceptionString);
    }


    /**
     * Tries to add an administrator to a business when the user issuing the request is not a primary administrator
     * Checks that a ForbiddenPrimaryAdministratorActionException is sent, a 403 response
     */
    @Test
    @Order(9)
    void addAdministratorWhenNotPrimaryAdmin() throws Exception {

        JSONObject primaryAdmin = new JSONObject();
        primaryAdmin.put("userId", testPrimaryAdmin.getId());

        RequestBuilder addAdminRequest = MockMvcRequestBuilders
                .put(String.format("/businesses/%d/makeAdministrator", testBusiness.getId()))
                .content(primaryAdmin.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("DaveSims@gmail.com", "1337-H%nt3r2"));

        MvcResult addAdminResponse = this.mvc.perform(addAdminRequest)
                .andExpect(MockMvcResultMatchers.status().isForbidden()) // We expect a 403 response
                .andReturn();

        String returnedExceptionString = addAdminResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new ForbiddenPrimaryAdministratorActionException(testBusiness.getId()).getMessage(), returnedExceptionString);
    }


    /**
     * Tries to remove the primary administrator of a business by calling the /businesses/{id}/removeAdministrator endpoint
     * Checks that a CantRemoveAdministratorException is sent, a 400 response
     */
    @Test
    @Order(10)
    void removePrimaryAdministrator() throws Exception {

        //Trying to remove the primary administrator
        JSONObject primaryAdmin = new JSONObject();
        primaryAdmin.put("userId", testPrimaryAdmin.getId());

        RequestBuilder removeAdminRequest = MockMvcRequestBuilders
                .put(String.format("/businesses/%d/removeAdministrator", testBusiness.getId()))
                .content(primaryAdmin.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("jimsmith@gmail.com", "1337-H%nt3r2"));

        MvcResult removeAdminResponse = this.mvc.perform(removeAdminRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = removeAdminResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new CantRemoveAdministratorException(testPrimaryAdmin.getId(), testBusiness.getId()).getMessage(), returnedExceptionString);
    }


    /**
     * Tries to remove an administrator of a business by calling the /businesses/{id}/removeAdministrator endpoint
     * Checks that the user is removed as an administrator
     */
    @Test
    @Order(11)
    void removeAdministrator() throws Exception {

        given(testBusiness.getAdministrators()).willReturn(List.of(testUser));

        JSONObject admin = new JSONObject();
        admin.put("userId", testUser.getId());

        mvc.perform(MockMvcRequestBuilders
                .put(String.format("/businesses/%d/removeAdministrator", testBusiness.getId()))
                .content(admin.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("jimsmith@gmail.com", "1337-H%nt3r2")));

        //Check that remove administrator method called with user 2
        //This actually throws NoUserExistsException when run
        ArgumentCaptor<User> removedAdministratorCaptor = ArgumentCaptor.forClass(User.class);
        verify(testBusiness).removeAdministrator(removedAdministratorCaptor.capture());

        User removedAdmin = removedAdministratorCaptor.getValue();

        Assertions.assertEquals(testUser.getEmail(), removedAdmin.getEmail());

    }


    /**
     * Tries to remove a user as administrator to a business when they are not an administrator
     * Checks that a CantRemoveAdministratorException is sent, a 400 response
     */
    @Test
    @Order(12)
    void removeNonExistentAdministrator() throws Exception {

        //Trying to remove a user who is not an admin (The user that was just removed as an admin in the previous test)
        JSONObject user = new JSONObject();
        user.put("userId", testUser.getId());

        RequestBuilder removeAdminRequest = MockMvcRequestBuilders
                .put(String.format("/businesses/%d/removeAdministrator", testBusiness.getId()))
                .content(user.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("jimsmith@gmail.com", "1337-H%nt3r2"));

        MvcResult removeAdminResponse = this.mvc.perform(removeAdminRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = removeAdminResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new UserNotAdministratorException(testUser.getId(), testBusiness.getId()).getMessage(), returnedExceptionString);
    }


    /**
     * Tries to remove an administrator of a business when the logged in user is not the primary administrator
     * Checks that a CantRemoveAdministratorException is sent, a 403 response
     */
    @Test
    @Order(13)
    void removeAdministratorWhenNotPrimaryAdmin() throws Exception {

        JSONObject primaryAdmin = new JSONObject();
        primaryAdmin.put("userId", testPrimaryAdmin.getId());

        RequestBuilder removeAdminRequest = MockMvcRequestBuilders
                .put(String.format("/businesses/%d/removeAdministrator", testBusiness.getId()))
                .content(primaryAdmin.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("DaveSims@gmail.com", "1337-H%nt3r2"));

        MvcResult removeAdminResponse = this.mvc.perform(removeAdminRequest)
                .andExpect(MockMvcResultMatchers.status().isForbidden()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = removeAdminResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new ForbiddenPrimaryAdministratorActionException(testBusiness.getId()).getMessage(), returnedExceptionString);
    }
}