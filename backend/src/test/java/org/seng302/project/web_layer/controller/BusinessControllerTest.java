package org.seng302.project.web_layer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.Business;
import org.seng302.project.repository_layer.model.User;
import org.seng302.project.repository_layer.model.enums.BusinessType;
import org.seng302.project.service_layer.dto.address.AddressDTO;
import org.seng302.project.service_layer.dto.business.PostBusinessDTO;
import org.seng302.project.service_layer.dto.business.PutBusinessAdminDTO;
import org.seng302.project.service_layer.exceptions.ForbiddenException;
import org.seng302.project.service_layer.exceptions.NotAcceptableException;
import org.seng302.project.service_layer.exceptions.business.BusinessNotFoundException;
import org.seng302.project.service_layer.exceptions.businessAdministrator.AdministratorAlreadyExistsException;
import org.seng302.project.service_layer.exceptions.businessAdministrator.CantRemoveAdministratorException;
import org.seng302.project.service_layer.exceptions.businessAdministrator.UserNotAdministratorException;
import org.seng302.project.service_layer.service.BusinessService;
import org.seng302.project.service_layer.service.UserService;
import org.seng302.project.web_layer.authentication.AppUserDetails;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Unit tests for BusinessController
 */


@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
class BusinessControllerTest extends AbstractInitializer {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BusinessService businessService;

    @MockBean
    private UserService userService;

    private User testPrimaryAdmin;
    private User testUser;
    private Business testBusiness;
    private Object JSONObject;

    @BeforeEach
    public void setup() {

        //Mock a test user to be used as business primary admin
        testPrimaryAdmin = this.getTestUserBusinessAdmin();

        //Mock a different test user
        testUser = this.getTestUser();

        //Mock a test business
        //Spy on this business so we can check when methods on the business object are called
        testBusiness = this.getTestBusiness();

        //Mock the userService.checkForbidden method to return
        Mockito.doNothing().when(userService).checkForbidden(any(Integer.class), any(AppUserDetails.class));
    }


    /**
     * Creates the test business from the API by calling the POST '/businesses' endpoint.
     * Expects a 201 response
     */
    @Test
    void createBusiness_validFields_201() throws Exception {
        Mockito.when(businessService.createBusiness(any(PostBusinessDTO.class))).thenReturn(1);

        PostBusinessDTO requestDTO = new PostBusinessDTO(
                testBusiness.getName(),
                testBusiness.getDescription(),
                new AddressDTO(testBusiness.getAddress()),
                testBusiness.getBusinessType(),
                testBusiness.getPrimaryAdministratorId()
        );

        RequestBuilder request = MockMvcRequestBuilders
                .post("/businesses")
                .content(objectMapper.writeValueAsString(requestDTO))
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
        PostBusinessDTO requestDTO = new PostBusinessDTO(
                "",
                testBusiness.getDescription(),
                new AddressDTO(testBusiness.getAddress()),
                testBusiness.getBusinessType(),
                testBusiness.getPrimaryAdministratorId()
        );

        RequestBuilder postBusinessRequest = MockMvcRequestBuilders
                .post("/businesses")
                .content(objectMapper.writeValueAsString(requestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testPrimaryAdmin)));

        MvcResult postBusinessResponse = this.mvc.perform(postBusinessRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postBusinessResponse.getResponse().getContentAsString();
        Assertions.assertEquals("MethodArgumentNotValidException: Business name is a mandatory field", returnedExceptionString);
    }


    /**
     * Tries creating a business with missing required field: address
     * Checks that we receive a 400 response.
     */
    @Test
    void createBusiness_addressFieldEmpty_400() throws Exception {
        PostBusinessDTO requestDTO = new PostBusinessDTO(
                testBusiness.getName(),
                testBusiness.getDescription(),
                new AddressDTO(testBusiness.getAddress()),
                testBusiness.getBusinessType(),
                testBusiness.getPrimaryAdministratorId()
        );
        requestDTO.getAddress().setCountry("");
        RequestBuilder postBusinessRequest = MockMvcRequestBuilders
                .post("/businesses")
                .content(objectMapper.writeValueAsString(requestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testPrimaryAdmin)));

        MvcResult postBusinessResponse = this.mvc.perform(postBusinessRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postBusinessResponse.getResponse().getContentAsString();
        Assertions.assertEquals("MethodArgumentNotValidException: Address format is incorrect. A country must be included." +
                " If a street number is given, a street name must be provided", returnedExceptionString);
    }


    /**
     * Tries creating a business with missing required field: businessType
     * Checks that we receive a 400 response with a message from the DTO
     */
    @Test
    void createBusiness_typeFieldEmpty_400() throws Exception {
        PostBusinessDTO requestDTO = new PostBusinessDTO(
                testBusiness.getName(),
                testBusiness.getDescription(),
                new AddressDTO(testBusiness.getAddress()),
                "",
                testBusiness.getPrimaryAdministratorId()
        );
        RequestBuilder postBusinessRequest = MockMvcRequestBuilders
                .post("/businesses")
                .content(objectMapper.writeValueAsString(requestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testPrimaryAdmin)));

        MvcResult postBusinessResponse = this.mvc.perform(postBusinessRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postBusinessResponse.getResponse().getContentAsString();
        Assertions.assertTrue(returnedExceptionString.equals("MethodArgumentNotValidException: Business type is a mandatory field") ||
                returnedExceptionString.equals("MethodArgumentNotValidException: Invalid business type provided"));

    }


    /**
     * Tries creating a business with all required fields missing: name, address, type
     * Checks that we receive a 400 response with a message from the DTO
     */
    @Test
    void createBusiness_allRequiredFieldsEmpty_400() throws Exception {

        PostBusinessDTO requestDTO = new PostBusinessDTO(
                "",
                testBusiness.getDescription(),
                new AddressDTO(testBusiness.getAddress()),
                "",
                testBusiness.getPrimaryAdministratorId()
        );
        requestDTO.getAddress().setCountry("");

        RequestBuilder postBusinessRequest = MockMvcRequestBuilders
                .post("/businesses")
                .content(objectMapper.writeValueAsString(requestDTO))
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
        PostBusinessDTO requestDTO = new PostBusinessDTO(
                testBusiness.getName(),
                testBusiness.getDescription(),
                new AddressDTO(testBusiness.getAddress()),
                "My New Type",
                testBusiness.getPrimaryAdministratorId()
        );
        RequestBuilder postBusinessRequest = MockMvcRequestBuilders
                .post("/businesses")
                .content(objectMapper.writeValueAsString(requestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testPrimaryAdmin)));

        MvcResult postBusinessResponse = this.mvc.perform(postBusinessRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postBusinessResponse.getResponse().getContentAsString();
        Assertions.assertEquals("MethodArgumentNotValidException: Invalid business type provided", returnedExceptionString);
    }


    /**
     * Tries creating a business where address has a street number but no street name
     * Checks that we receive a 400 response with a message from the DTO
     */
    @Test
    void createBusiness_streetNumberNoStreetName_400() throws Exception {
        AddressDTO testAddress = new AddressDTO();
        testAddress.setStreetNumber("17");
        testAddress.setStreetName("");

        PostBusinessDTO requestDTO = new PostBusinessDTO(
                testBusiness.getName(),
                testBusiness.getDescription(),
                testAddress,
                testBusiness.getBusinessType(),
                testBusiness.getPrimaryAdministratorId()
        );


        RequestBuilder postBusinessRequest = MockMvcRequestBuilders
                .post("/businesses")
                .content(objectMapper.writeValueAsString(requestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testPrimaryAdmin)));

        MvcResult postBusinessResponse = this.mvc.perform(postBusinessRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postBusinessResponse.getResponse().getContentAsString();
        Assertions.assertEquals("MethodArgumentNotValidException: Address format is incorrect. A country must be included." +
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
        Mockito.doThrow(new BusinessNotFoundException(80)).when(businessService)
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
                .addAdministrator(any(PutBusinessAdminDTO.class));

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

        Mockito.doThrow(new ForbiddenException(""))
                .when(businessService)
                .addAdministrator(any(PutBusinessAdminDTO.class));

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
                .removeAdministrator(any(PutBusinessAdminDTO.class));

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
                .removeAdministrator(any(PutBusinessAdminDTO.class));

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

        Mockito.doThrow(new ForbiddenException(""))
                .when(businessService)
                .removeAdministrator(any(PutBusinessAdminDTO.class));

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
        Mockito.when(businessService.searchBusiness(any(String.class), any(BusinessType.class), any(Integer.class), any(String.class)))
                .thenReturn(List.of(testBusiness, 1));

        RequestBuilder searchBusinessRequest = MockMvcRequestBuilders
                .get("/businesses/search?searchQuery=General&pageNumber=0&sortBy=")
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

        Mockito.when(businessService.searchBusiness(any(String.class), any(BusinessType.class), any(Integer.class), any(String.class)))
                .thenReturn(List.of(testBusiness, 1));

        RequestBuilder searchBusinessRequest = MockMvcRequestBuilders
                .get("/businesses/search?searchQuery=General&businessType={businessType}&pageNumber=0&sortBy=", testBusiness.getBusinessType())
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
                .get("/businesses/search?searchQuery=General&businessType=Not%20a%20Type&pageNumber=0&sortBy=")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        MvcResult postBusinessResponse = this.mvc.perform(searchBusinessRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postBusinessResponse.getResponse().getContentAsString();
        Assertions.assertEquals("BadRequestException: Invalid business type provided", returnedExceptionString);
    }

    /**
     * Tests that a valid request results in a 200 status code
     */
    @Test
    void editBusiness_validRequest_200() throws Exception {
        AppUserDetails appUser = new AppUserDetails(testPrimaryAdmin);
        Integer businessId = testBusiness.getId();
        PostBusinessDTO requestDTO = new PostBusinessDTO(
                "New Name",
                "New Description",
                new AddressDTO(testBusiness.getAddress()),
                testBusiness.getBusinessType(),
                testBusiness.getPrimaryAdministratorId()
        );

        RequestBuilder request = MockMvcRequestBuilders
                .put("/businesses/{businessId}", businessId)
                .content(objectMapper.writeValueAsString(requestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(appUser));

        mvc.perform(request).andExpect(status().isOk());
    }

    /**
     * Tests that a request with empty name results in a 400
     */
    @Test
    void editBusiness_noName_400() throws Exception {
        AppUserDetails appUser = new AppUserDetails(testPrimaryAdmin);
        Integer businessId = testBusiness.getId();
        PostBusinessDTO requestDTO = new PostBusinessDTO(
                "",
                "New Description",
                new AddressDTO(testBusiness.getAddress()),
                testBusiness.getBusinessType(),
                testBusiness.getPrimaryAdministratorId()
        );

        RequestBuilder request = MockMvcRequestBuilders
                .put("/businesses/{businessId}", businessId)
                .content(objectMapper.writeValueAsString(requestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(appUser));

        mvc.perform(request).andExpect(status().isBadRequest());
    }

    /**
     * Tests that a request with empty type results in a 400
     */
    @Test
    void editBusiness_noType_400() throws Exception {
        AppUserDetails appUser = new AppUserDetails(testPrimaryAdmin);
        Integer businessId = testBusiness.getId();
        PostBusinessDTO requestDTO = new PostBusinessDTO(
                "New Name",
                "New Description",
                new AddressDTO(testBusiness.getAddress()),
                "",
                testBusiness.getPrimaryAdministratorId()
        );

        RequestBuilder request = MockMvcRequestBuilders
                .put("/businesses/{businessId}", businessId)
                .content(objectMapper.writeValueAsString(requestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(appUser));

        mvc.perform(request).andExpect(status().isBadRequest());
    }

    /**
     * Tests that a request with invalid type results in a 400
     */
    @Test
    void editBusiness_invalidType_400() throws Exception {
        AppUserDetails appUser = new AppUserDetails(testPrimaryAdmin);
        Integer businessId = testBusiness.getId();
        PostBusinessDTO requestDTO = new PostBusinessDTO(
                "New Name",
                "New Description",
                new AddressDTO(testBusiness.getAddress()),
                "My New Type",
                testBusiness.getPrimaryAdministratorId()
        );

        RequestBuilder request = MockMvcRequestBuilders
                .put("/businesses/{businessId}", businessId)
                .content(objectMapper.writeValueAsString(requestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(appUser));

        mvc.perform(request).andExpect(status().isBadRequest());
    }

    /**
     * Tests that a request with invalid address results in a 400
     */
    @Test
    void editBusiness_invalidAddress_400() throws Exception {
        AppUserDetails appUser = new AppUserDetails(testPrimaryAdmin);
        Integer businessId = testBusiness.getId();
        PostBusinessDTO requestDTO = new PostBusinessDTO(
                "New Name",
                "New Description",
                new AddressDTO(testBusiness.getAddress()),
                testBusiness.getBusinessType(),
                testBusiness.getPrimaryAdministratorId()
        );

        requestDTO.getAddress().setCountry("");

        RequestBuilder request = MockMvcRequestBuilders
                .put("/businesses/{businessId}", businessId)
                .content(objectMapper.writeValueAsString(requestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(appUser));

        mvc.perform(request).andExpect(status().isBadRequest());
    }

    /**
     * Tests that a request returns 401 when not logged in
     */
    @Test
    void editBusiness_notLoggedIn_401() throws Exception {
        Integer businessId = testBusiness.getId();
        PostBusinessDTO requestDTO = new PostBusinessDTO(
                "New Name",
                "New Description",
                new AddressDTO(testBusiness.getAddress()),
                testBusiness.getBusinessType(),
                testBusiness.getPrimaryAdministratorId()
        );

        RequestBuilder request = MockMvcRequestBuilders
                .put("/businesses/{businessId}", businessId)
                .content(objectMapper.writeValueAsString(requestDTO))
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(request).andExpect(status().isUnauthorized());
    }

    /**
     * Tests that a request returns 403 when user making request is not an admin of the business
     */
    @Test
    void editBusiness_notAdmin_403() throws Exception {
        Mockito.doThrow(new ForbiddenException("Test exception"))
                .when(businessService)
                .editBusiness(
                        any(PostBusinessDTO.class),
                        any(Integer.class),
                        any(AppUserDetails.class),
                        any()
                );

        AppUserDetails appUser = new AppUserDetails(testUser);
        Integer businessId = testBusiness.getId();
        PostBusinessDTO requestDTO = new PostBusinessDTO(
                "New Name",
                "New Description",
                new AddressDTO(testBusiness.getAddress()),
                testBusiness.getBusinessType(),
                testBusiness.getPrimaryAdministratorId()
        );

        RequestBuilder request = MockMvcRequestBuilders
                .put("/businesses/{businessId}", businessId)
                .content(objectMapper.writeValueAsString(requestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(appUser));

        mvc.perform(request).andExpect(status().isForbidden());
    }

    /**
     * Tests that a request returns 406 status when a business that doesn't exist is edited
     */
    @Test
    void editBusiness_nonExistentBusiness_406() throws Exception {
        Mockito.doThrow(new NotAcceptableException("Test exception"))
                .when(businessService)
                .editBusiness(
                        any(PostBusinessDTO.class),
                        any(Integer.class),
                        any(AppUserDetails.class),
                        any()
                );

        AppUserDetails appUser = new AppUserDetails(testPrimaryAdmin);
        Integer businessId = 500;
        PostBusinessDTO requestDTO = new PostBusinessDTO(
                "New Name",
                "New Description",
                new AddressDTO(testBusiness.getAddress()),
                testBusiness.getBusinessType(),
                testBusiness.getPrimaryAdministratorId()
        );

        RequestBuilder request = MockMvcRequestBuilders
                .put("/businesses/{businessId}", businessId)
                .content(objectMapper.writeValueAsString(requestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(appUser));

        mvc.perform(request).andExpect(status().isNotAcceptable());
    }

    /**
     * Tests that successfully getting a business' notifications
     * gives a 200 response
     */
    @Test
    void getBusinessNotifications_success_200() throws Exception {
        RequestBuilder getBusinessNotificationsRequest = MockMvcRequestBuilders
                .get("/businesses/{businessId}/notifications", 1)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testPrimaryAdmin)));

        mvc.perform(getBusinessNotificationsRequest)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     * Tests that trying to get a business' notifications
     * when not logged in
     * gives a 401 response
     */
    @Test
    void getBusinessNotifications_notLoggedIn_401() throws Exception {
        RequestBuilder getBusinessNotificationsRequest = MockMvcRequestBuilders
                .get("/businesses/{businessId}/notifications", 1);

        mvc.perform(getBusinessNotificationsRequest)
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    /**
     * Tests that trying to get a business' notifications
     * when not an admin of the business
     * gives a 403 response
     */
    @Test
    void getBusinessNotifications_notAdmin_403() throws Exception {
        Mockito.doThrow(new ForbiddenException("message")).when(businessService)
                .getBusinessNotifications(any(Integer.class), any(AppUserDetails.class));

        RequestBuilder getBusinessNotificationsRequest = MockMvcRequestBuilders
                .get("/businesses/{businessId}/notifications", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        this.mvc.perform(getBusinessNotificationsRequest)
                .andExpect(MockMvcResultMatchers.status().isForbidden());

    }

    /**
     * Tests that trying to get a business' notifications
     * for a non-existent business
     * gives a 406 response
     */
    @Test
    void getBusinessNotifications_nonExistentBusiness_406() throws Exception {
        Mockito.doThrow(new BusinessNotFoundException(80)).when(businessService)
                .getBusinessNotifications(any(Integer.class), any(AppUserDetails.class));

        RequestBuilder getBusinessNotificationsRequest = MockMvcRequestBuilders
                .get("/businesses/{businessId}/notifications", 80)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testPrimaryAdmin)));

        this.mvc.perform(getBusinessNotificationsRequest)
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable()); // We expect a 406 response

    }


    /**
     * Tests that successfully deleting a business' notification
     * gives a 200 response
     */
    @Test
    void deleteBusinessNotification_success_200() throws Exception {

        RequestBuilder deleteBusinessNotificationRequest = MockMvcRequestBuilders
                .delete("/businesses/{businessId}/notifications/{notificationId}", 1, 1)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testPrimaryAdmin)));

        this.mvc.perform(deleteBusinessNotificationRequest)
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    /**
     * Tests that trying to delete a business' notification
     * when not logged in
     * gives a 401 response
     */
    @Test
    void deleteBusinessNotification_notLoggedIn_401() throws Exception {

        RequestBuilder deleteBusinessNotificationRequest = MockMvcRequestBuilders
                .delete("/businesses/{businessId}/notifications/{notificationId}", 1, 1)
                .accept(MediaType.APPLICATION_JSON);

        this.mvc.perform(deleteBusinessNotificationRequest)
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

    }

    /**
     * Tests that trying to delete a business' notification
     * when not an admin of the business (or GAA)
     * gives a 403 response
     */
    @Test
    void deleteBusinessNotification_notAdmin_403() throws Exception {
        Mockito.doThrow(new ForbiddenException("message")).when(businessService)
                .deleteBusinessNotification(any(Integer.class), any(Integer.class), any(AppUserDetails.class));

        RequestBuilder deleteBusinessNotificationRequest = MockMvcRequestBuilders
                .delete("/businesses/{businessId}/notifications/{notificationId}", 1, 1)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        this.mvc.perform(deleteBusinessNotificationRequest)
                .andExpect(MockMvcResultMatchers.status().isForbidden());

    }

    /**
     * Tests that trying to delete a business' notification
     * that doesn't exist
     * gives a 406 response
     */
    @Test
    void deleteBusinessNotification_nonExistentNotification_406() throws Exception {
        Mockito.doThrow(new NotAcceptableException("message")).when(businessService)
                .deleteBusinessNotification(any(Integer.class), any(Integer.class), any(AppUserDetails.class));

        RequestBuilder deleteBusinessNotificationRequest = MockMvcRequestBuilders
                .delete("/businesses/{businessId}/notifications/{notificationId}", 1, 80)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testPrimaryAdmin)));

        this.mvc.perform(deleteBusinessNotificationRequest)
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable());

    }


    //TODO: tests for business notifications:
    //PATCH businesses/{businessId}/notifications/{notificationId}/read
        //200
        //400
        //401
        //403
        //406

}