package org.seng302.project.webLayer.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.seng302.project.repositoryLayer.model.Business;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.serviceLayer.dto.business.AddOrRemoveBusinessAdminDTO;
import org.seng302.project.serviceLayer.dto.business.SearchBusinessDTO;
import org.seng302.project.serviceLayer.exceptions.*;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.AdministratorAlreadyExistsException;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.CantRemoveAdministratorException;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.ForbiddenPrimaryAdministratorActionException;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.UserNotAdministratorException;
import org.seng302.project.serviceLayer.service.BusinessService;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Unit tests for BusinessController
 */


@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
class BusinessControllerTest {

    @Autowired
    private MockMvc mvc;

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

        //Mock a different test user
        testUser = new User("Dave", "Sims", "", "", "",
                "DaveSims@gmail.com", "1998-04-27", "",
                null, "1337-H%nt3r2");

        testUser.setId(2);

        //Mock a test business
        //Spy on this business so we can check when methods on the business object are called
        testBusiness = new Business("Lumbridge General Store", "A one-stop shop for all your adventuring needs",
                null, "Accommodation and Food Services", 1);
        testBusiness.setId(1);
    }

    private JSONObject getTestBusiness() throws JSONException {
        JSONObject testAddress = new JSONObject();
        testAddress.put("country", "New Zealand");
        JSONObject testBusinessJson = new JSONObject();
        testBusinessJson.put("primaryAdministratorId", 1);
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

        RequestBuilder request = MockMvcRequestBuilders
                .post("/businesses")
                .content(testBusinessJson.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testPrimaryAdmin)));

        mvc.perform(request).andExpect(status().isCreated());
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
                .with(user(new AppUserDetails(testPrimaryAdmin)));

        MvcResult postBusinessResponse = this.mvc.perform(postBusinessRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postBusinessResponse.getResponse().getContentAsString();
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
                .with(user(new AppUserDetails(testPrimaryAdmin)));

        MvcResult postBusinessResponse = this.mvc.perform(postBusinessRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postBusinessResponse.getResponse().getContentAsString();
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
                .with(user(new AppUserDetails(testPrimaryAdmin)));

        MvcResult postBusinessResponse = this.mvc.perform(postBusinessRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postBusinessResponse.getResponse().getContentAsString();
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
                .with(user(new AppUserDetails(testPrimaryAdmin)));

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
                .with(user(new AppUserDetails(testPrimaryAdmin)));

        MvcResult postBusinessResponse = this.mvc.perform(postBusinessRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postBusinessResponse.getResponse().getContentAsString();
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
                .with(user(new AppUserDetails(testPrimaryAdmin)));

        MvcResult postBusinessResponse = this.mvc.perform(postBusinessRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postBusinessResponse.getResponse().getContentAsString();
        Assertions.assertEquals("Address format is incorrect. A country must be included." +
                " If a street number is given, a street name must be provided", returnedExceptionString);
    }

    /**
     * Tries to get an existing business by calling the /businesses/{id} endpoint
     * Checks that 200 response given
     */
    @Test
    void getBusiness_200() throws Exception {

        RequestBuilder getBusinessRequest = MockMvcRequestBuilders
                .get("/businesses/{id}", 1)
                .content(testBusiness.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

                mvc.perform(getBusinessRequest)
                .andExpect(MockMvcResultMatchers.status().isOk()); // We expect a 200 response

    }

    /**
     * Tries to get a non-existing business by calling the /businesses/{id} endpoint
     * Checks that 406 response given
     */
    @Test
    void getNonExistentBusiness_406() throws Exception {
        Mockito.doThrow(new NoBusinessExistsException(80)).when(businessService)
                .getBusiness(any(Integer.class));

        RequestBuilder getBusinessRequest = MockMvcRequestBuilders
                .get("/businesses/{id}", 80)
                .content(testBusiness.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testPrimaryAdmin)));

        this.mvc.perform(getBusinessRequest)
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable()); // We expect a 406 response
    }


    /**
     * Tries to add an administrator to a business by calling the /businesses/{id}/makeAdministrator endpoint
     * Expects 200 response
     */
    @Test
    void addAdministrator_200() throws Exception {

        JSONObject testUserJson = new JSONObject();
        testUserJson.put("userId", testUser.getId());

        RequestBuilder request = MockMvcRequestBuilders
                .put(String.format("/businesses/%d/makeAdministrator", testBusiness.getId()))
                .content(testUserJson.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testPrimaryAdmin)));

        this.mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    /**
     * Tries to add an administrator to a business when they are already an administrator
     * Expects a 400 response
     */
    @Test
    void addSameAdministrator_400() throws Exception {

        Mockito.doThrow(new AdministratorAlreadyExistsException(testUser.getId(), testBusiness.getId()))
                .when(businessService)
                .addAdministrator(any(AddOrRemoveBusinessAdminDTO.class));

        JSONObject newAdmin = new JSONObject();
        newAdmin.put("userId", testUser.getId());

        //Trying to add the same admin again
        RequestBuilder addAdminRequest = MockMvcRequestBuilders
                .put(String.format("/businesses/%d/makeAdministrator", testBusiness.getId()))
                .content(newAdmin.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testPrimaryAdmin)));

        this.mvc.perform(addAdminRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()); // We expect a 400 response

    }


    /**
     * Tries to add an administrator to a business when the user issuing the request is not a primary administrator
     * Expects a 403 response
     */
    @Test
    void addAdministratorWhenNotPrimaryAdmin_403() throws Exception {

        Mockito.doThrow(new ForbiddenPrimaryAdministratorActionException(testBusiness.getId()))
                .when(businessService)
                .addAdministrator(any(AddOrRemoveBusinessAdminDTO.class));

        JSONObject primaryAdmin = new JSONObject();
        primaryAdmin.put("userId", testPrimaryAdmin.getId());

        RequestBuilder addAdminRequest = MockMvcRequestBuilders
                .put(String.format("/businesses/%d/makeAdministrator", testBusiness.getId()))
                .content(primaryAdmin.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        this.mvc.perform(addAdminRequest)
                .andExpect(MockMvcResultMatchers.status().isForbidden()); // We expect a 403 response
    }


    /**
     * Tries to remove the primary administrator of a business by calling the /businesses/{id}/removeAdministrator endpoint
     * Expects a 400 response
     */
    @Test
    void removePrimaryAdministrator_400() throws Exception {

        Mockito.doThrow(new CantRemoveAdministratorException(testPrimaryAdmin.getId(), testBusiness.getId()))
                .when(businessService)
                .removeAdministrator(any(AddOrRemoveBusinessAdminDTO.class));

        //Trying to remove the primary administrator
        JSONObject primaryAdmin = new JSONObject();
        primaryAdmin.put("userId", testPrimaryAdmin.getId());

        RequestBuilder removeAdminRequest = MockMvcRequestBuilders
                .put(String.format("/businesses/%d/removeAdministrator", testBusiness.getId()))
                .content(primaryAdmin.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testPrimaryAdmin)));

        this.mvc.perform(removeAdminRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()); // We expect a 400 response
    }


    /**
     * Tries to remove an administrator of a business by calling the /businesses/{id}/removeAdministrator endpoint
     * Expects 200 response
     */
    @Test
    void removeAdministrator_200() throws Exception {

        JSONObject admin = new JSONObject();
        admin.put("userId", testUser.getId());

        RequestBuilder removeAdminRequest = MockMvcRequestBuilders
                .put(String.format("/businesses/%d/removeAdministrator", testBusiness.getId()))
                .content(admin.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testPrimaryAdmin)));

        this.mvc.perform(removeAdminRequest)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    /**
     * Tries to remove a user as administrator to a business when they are not an administrator
     * Expects a 400 response
     */
    @Test
    void removeNonExistentAdministrator_400() throws Exception {

        Mockito.doThrow(new UserNotAdministratorException(testUser.getId(), testBusiness.getId()))
                .when(businessService)
                .removeAdministrator(any(AddOrRemoveBusinessAdminDTO.class));

        //Trying to remove a user who is not an admin (The user that was just removed as an admin in the previous test)
        JSONObject user = new JSONObject();
        user.put("userId", testUser.getId());

        RequestBuilder removeAdminRequest = MockMvcRequestBuilders
                .put(String.format("/businesses/%d/removeAdministrator", testBusiness.getId()))
                .content(user.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testPrimaryAdmin)));

        this.mvc.perform(removeAdminRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()); // We expect a 400 response
    }


    /**
     * Tries to remove an administrator of a business when the logged in user is not the primary administrator
     * Expects a 403 response
     */
    @Test
    void removeAdministratorWhenNotPrimaryAdmin_403() throws Exception {

        Mockito.doThrow(new ForbiddenPrimaryAdministratorActionException(testBusiness.getId()))
                .when(businessService)
                .removeAdministrator(any(AddOrRemoveBusinessAdminDTO.class));

        JSONObject primaryAdmin = new JSONObject();
        primaryAdmin.put("userId", testPrimaryAdmin.getId());

        RequestBuilder removeAdminRequest = MockMvcRequestBuilders
                .put(String.format("/businesses/%d/removeAdministrator", testBusiness.getId()))
                .content(primaryAdmin.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        this.mvc.perform(removeAdminRequest)
                .andExpect(MockMvcResultMatchers.status().isForbidden()); // We expect a 403 response
    }

    /**
     * Tries to search for a business with a valid query and no type
     * Expects a 200 response
     */
    @Test
    void searchBusiness_validQueryNoType_200() throws Exception {
        given(businessService.searchBusiness(any(SearchBusinessDTO.class)))
                .willReturn(List.of(testBusiness));

        RequestBuilder searchBusinessRequest = MockMvcRequestBuilders
                .get("/businesses/search?searchQuery=General")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        this.mvc.perform(searchBusinessRequest)
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    /**
     * Tries to search for a business with a valid query and valid type
     * Expects a 200 response
     */
    @Test
    void searchBusiness_validQueryValidType_200() throws Exception {
        given(businessService.searchBusiness(any(SearchBusinessDTO.class)))
                .willReturn(List.of(testBusiness));

        //TODO: this test fails
        RequestBuilder searchBusinessRequest = MockMvcRequestBuilders
                .get("/businesses/search?searchQuery=General&businessType={businessType}", testBusiness.getBusinessType())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        this.mvc.perform(searchBusinessRequest)
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    /**
     * Tries to search for a business with a valid query and invalid type
     * Expects a 400 response
     */
    @Test
    void searchBusiness_invalidType_400() throws Exception {

        RequestBuilder searchBusinessRequest = MockMvcRequestBuilders
                // %20 encodes a space character in a URL
                .get("/businesses/search?searchQuery=General&businessType=Not%20a%20Type")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        MvcResult postBusinessResponse = this.mvc.perform(searchBusinessRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postBusinessResponse.getResponse().getContentAsString();
        Assertions.assertEquals("Invalid business type provided", returnedExceptionString);

    }

}