package org.seng302.project.web_layer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.*;
import org.seng302.project.repository_layer.repository.AddressRepository;
import org.seng302.project.repository_layer.repository.UserRepository;
import org.seng302.project.service_layer.dto.address.AddressDTO;
import org.seng302.project.service_layer.dto.sales_report.GetSaleDTO;
import org.seng302.project.service_layer.dto.user.LoginCredentialsDTO;
import org.seng302.project.service_layer.dto.user.PostUserDTO;
import org.seng302.project.service_layer.dto.user.PutUserDTO;
import org.seng302.project.service_layer.exceptions.BadRequestException;
import org.seng302.project.service_layer.exceptions.ForbiddenException;
import org.seng302.project.service_layer.exceptions.NotAcceptableException;
import org.seng302.project.service_layer.service.SaleListingService;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Unit tests for UserController
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UserControllerTest extends AbstractInitializer {

    private User testUser;
    private User otherUser;
    private Business business;
    private Product product;
    private InventoryItem inventoryItem;

    private PutUserDTO testPutUserDTO;

    private LoginCredentialsDTO invalidLoginCredentials;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AddressRepository addressRepository;

    @MockBean
    private SaleListingService saleListingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @BeforeEach
    void setup() {
        invalidLoginCredentials = new LoginCredentialsDTO("notRegistered@gmail.com", "1357-H%nt3r4");
        Mockito.when(userRepository.findByEmail(invalidLoginCredentials.getEmail())).thenReturn(Collections.emptyList());

        testUser = this.getTestUser();
        otherUser = this.getTestOtherUser();
        business = this.getTestBusiness();
        product = this.getTestProduct();
        // Create inventory item
        inventoryItem = new InventoryItem(product, 20,
                10.99, 219.8, "2021-04-25",
                "2021-04-25", "2021-04-25", "2021-04-25");


        testPutUserDTO = new PutUserDTO(
                null, //ID is set in the controller
                otherUser.getFirstName(),
                otherUser.getLastName(),
                otherUser.getMiddleName(),
                otherUser.getNickname(),
                otherUser.getBio(),
                "new.email@newemail.com",
                otherUser.getDateOfBirth(),
                otherUser.getPhoneNumber(),
                new AddressDTO(otherUser.getHomeAddress()),
                "NewSecurePassword123",
                "password");

        mocks();
    }

    /**
     * Sets up mocks used for some tests
     */
    void mocks() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(List.of(testUser));
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
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
    void createAndRetrieveTestUser() throws Exception {
        JSONObject testUserJson = createTestUserBase();
        testUserJson.put("firstName", "John");
        testUserJson.put("lastName", "Smith");
        testUserJson.put("email", "johnsmith99@gmail.com");
        testUserJson.put("dateOfBirth", "1999-04-27");
        testUserJson.put("phoneNumber", "+64 3 555 0129");

        when(userService.createUser(any(PostUserDTO.class))).thenReturn(
                new LoginCredentialsDTO("johnsmith99@gmail.com", testUserJson.getString("password")));

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
    void tryInvalidEmailAddress() throws Exception {

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
        Assertions.assertEquals("MethodArgumentNotValidException: EmailInvalid: This Email is not valid.", returnedExceptionString);

    }

    /**
     * Tries creating a user with an invalid user date as Date of Birth.
     * Checks that we receive a 400 response.
     */
    @Test
    void tryInvalidDate() throws Exception {

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
        Assertions.assertEquals("MethodArgumentNotValidException: DateOfBirthInvalid: This Date of Birth is not valid.", returnedExceptionString);
    }

    /**
     * Tries creating a user with an invalid user phone number.
     * Checks that we receive a 400 response.
     */
    @Test
    void tryInvalidPhoneNumber() throws Exception {

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
        Assertions.assertEquals(
                "MethodArgumentNotValidException: InvalidPhoneNumber: This Phone Number is not valid.",
                returnedExceptionString);
    }

    /**
     * Tries creating a user with an invalid user phone number.
     * Checks that we receive a 400 response.
     */
    @Test
    void tryCreateUnderageUser() throws Exception {

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
        Assertions.assertEquals("MethodArgumentNotValidException: DateOfBirthInvalid: This Date of Birth is not valid.", returnedExceptionString);
    }

    /**
     * Tries creating a user with missing required fields: firstName, lastName, and email.
     * Checks that we receive a 400 response.
     */
    @Test
    void tryMissingRequiredFields() throws Exception {

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
        Assertions.assertEquals("MethodArgumentNotValidException: MissingData: First Name is a mandatory field", returnedExceptionString);
    }

    /**
     * Tries to get a user by calling the /users/{id} endpoint
     * Checks that we retrieve the correct user
     */
    @Test
    void getUser() throws Exception {
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
                        .param("searchQuery", "")
                        .param("pageNumber", String.valueOf(1)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    /**
     * Check a 200 is returned for a valid search
     */
    @Test
    void userSearch_blueSky_200() throws Exception {
        // Mock the searchUsers method to return an empty list
        when(userService.searchUsers(any(String.class), any(Integer.class), any(String.class))).thenReturn(List.of());

        // Make the request, and check it is 200
        mvc.perform(MockMvcRequestBuilders
                        .get("/users/search")
                        .param("searchQuery", "testquery string")
                        .param("pageNumber", String.valueOf(1))
                        .param("sortBy", "")
                        .with(user(new AppUserDetails(getTestUser()))))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     * Tests editing user success
     * Tests the validation on the DTO, not the user Service editUser method
     */
    @Test
    void editUser_success200() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .put("/users/{id}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPutUserDTO))
                        .with(user(new AppUserDetails(testUser))))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     * Tests editing user returning a forbidden response
     */
    @Test
    void editUser_forbidden403() throws Exception {
        doThrow(ForbiddenException.class).when(userService).checkForbidden(any(Integer.class), any(AppUserDetails.class));

        mvc.perform(MockMvcRequestBuilders
                        .put("/users/{id}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPutUserDTO))
                        .with(user(new AppUserDetails(otherUser))))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    /**
     * Tests editing user throws 400 bad request with invalid email
     * Tests the validation on the DTO, not the user Service editUser method
     */
    @Test
    void editUser_InvalidEmail400() throws Exception {
        testPutUserDTO.setEmail("invalid@email");

        RequestBuilder postUserRequest = MockMvcRequestBuilders
                .put("/users/{id}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPutUserDTO))
                .with(user(new AppUserDetails(testUser)));

        MvcResult postUserResponse = this.mvc.perform(postUserRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        String returnedExceptionString = postUserResponse.getResponse().getContentAsString();
        //Exception string from the validation class
        Assertions.assertEquals("MethodArgumentNotValidException: EmailInvalid: This Email is not valid.", returnedExceptionString);
    }

    /**
     * Tests editing user throws 400 bad request with invalid password
     * Tests the validation on the DTO, not the user Service editUser method
     */
    @Test
    void editUser_InvalidPassword400() throws Exception {
        testPutUserDTO.setNewPassword("weakPassword");

        RequestBuilder postUserRequest = MockMvcRequestBuilders
                .put("/users/{id}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPutUserDTO))
                .with(user(new AppUserDetails(testUser)));

        MvcResult postUserResponse = this.mvc.perform(postUserRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        String returnedExceptionString = postUserResponse.getResponse().getContentAsString();
        //Exception string from the validation class
        Assertions.assertEquals("MethodArgumentNotValidException: This Password is not valid.", returnedExceptionString);
    }

    /**
     * Tests editing user throws 400 bad request with missing firstName
     * Tests the validation on the DTO, not the user Service editUser method
     */
    @Test
    void editUser_MissingFirstName400() throws Exception {
        testPutUserDTO.setFirstName(null);

        RequestBuilder postUserRequest = MockMvcRequestBuilders
                .put("/users/{id}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPutUserDTO))
                .with(user(new AppUserDetails(testUser)));

        MvcResult postUserResponse = this.mvc.perform(postUserRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        String returnedExceptionString = postUserResponse.getResponse().getContentAsString();
        //Exception string from the validation class
        Assertions.assertEquals("MethodArgumentNotValidException: MissingData: First Name is a mandatory field", returnedExceptionString);
    }

    /**
     * Tests editing user throws 400 bad request with missing lastName
     * Tests the validation on the DTO, not the user Service editUser method
     */
    @Test
    void editUser_MissingLastName400() throws Exception {
        testPutUserDTO.setLastName(null);

        RequestBuilder postUserRequest = MockMvcRequestBuilders
                .put("/users/{id}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPutUserDTO))
                .with(user(new AppUserDetails(testUser)));

        MvcResult postUserResponse = this.mvc.perform(postUserRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        String returnedExceptionString = postUserResponse.getResponse().getContentAsString();
        //Exception string from the validation class
        Assertions.assertEquals("MethodArgumentNotValidException: MissingData: Last Name is a mandatory field", returnedExceptionString);
    }

    /**
     * Tests editing user throws 400 bad request with invalid phone number
     * Tests the validation on the DTO, not the user Service editUser method
     */
    @Test
    void editUser_InvalidPhoneNumber400() throws Exception {
        testPutUserDTO.setPhoneNumber("1234");

        RequestBuilder postUserRequest = MockMvcRequestBuilders
                .put("/users/{id}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPutUserDTO))
                .with(user(new AppUserDetails(testUser)));

        MvcResult postUserResponse = this.mvc.perform(postUserRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        String returnedExceptionString = postUserResponse.getResponse().getContentAsString();
        //Exception string from the validation class
        Assertions.assertEquals("MethodArgumentNotValidException: InvalidPhoneNumber: This Phone Number is not valid.", returnedExceptionString);
    }

    /**
     * Tests that requesting purchases with a valid request results in a 200 status code
     */
    @Test
    void getPurchaseHistory_validRequest_status200() throws Exception {
        SaleListing saleListing = this.getSaleListings().get(0);
        Sale sale = new Sale(saleListing);
        sale.setBuyerId(testUser.getId());
        GetSaleDTO dto = new GetSaleDTO(sale);

        Mockito.when(userService.getPurchaseHistory(testUser.getId(), 1, ""))
                .thenReturn(List.of(dto));

        RequestBuilder request = MockMvcRequestBuilders
                .get("/users/{userId}/purchases", testUser.getId())
                .param("pageNumber", String.valueOf(0))
                .param("sortBy", "")
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        mvc.perform(request).andExpect(status().isOk());
    }

    /**
     * Tests that requesting purchases from a different user results in a 403 status code
     */
    @Test
    void getPurchaseHistory_notUserMakingRequest_status403() throws Exception {
        Mockito.doThrow(ForbiddenException.class)
                .when(userService).checkForbidden(any(Integer.class), any(AppUserDetails.class));

        RequestBuilder request = MockMvcRequestBuilders
                .get("/users/{userId}/purchases", testUser.getId())
                .param("pageNumber", String.valueOf(0))
                .param("sortBy", "")
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        mvc.perform(request).andExpect(status().isForbidden());
    }

    /**
     * Tests that requesting purchases from a non-existent user results in a 406 status code
     */
    @Test
    void getPurchaseHistory_nonExistentUser_status406() throws Exception {
        Mockito.when(userService.checkUser(testUser.getId()))
                .thenThrow(NotAcceptableException.class);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/users/{userId}/purchases", testUser.getId())
                .param("pageNumber", String.valueOf(1))
                .param("sortBy", "")
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));

        mvc.perform(request).andExpect(status().isNotAcceptable());
    }
}
