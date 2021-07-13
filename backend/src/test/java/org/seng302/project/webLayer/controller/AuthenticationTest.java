package org.seng302.project.webLayer.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.seng302.project.repositoryLayer.model.Address;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.AddressRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.dto.user.LoginCredentialsDTO;
import org.seng302.project.serviceLayer.exceptions.InvalidLoginException;
import org.seng302.project.serviceLayer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests authentication.
 */
@WebMvcTest(UserController.class)
class AuthenticationTest {

    private LoginCredentialsDTO testNonexistentLogin;
    private final JSONObject testAddress = new JSONObject();
    private final JSONObject newUserJson = new JSONObject();
    private final JSONObject testIncorrectLogin = new JSONObject();
    private final JSONObject testCorrectLogin = new JSONObject();
    private User registeredUser;
    
    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserRepository userRepository;

    //All tests in this class fail (even the ones that don't explicitly reference addressRepository)
    // when addressRepository bean is removed
    @MockBean
    private AddressRepository addressRepository;

    @Autowired
    private UserController userController;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    private UserService userService;

    @BeforeEach
    public void initialise() throws JSONException {

        testNonexistentLogin = new LoginCredentialsDTO("notRegistered@gmail.com", "1357-H%nt3r4");
        given(userRepository.findByEmail("notRegistered@gmail.com")).willReturn(List.of());

        testAddress.put("country", "New Zealand");
        newUserJson.put("firstName", "New");
        newUserJson.put("lastName", "User");
        newUserJson.put("email", "IWantToRegister@icloud.com");
        newUserJson.put("dateOfBirth", "2000-07-27");
        newUserJson.put("phoneNumber", "+64 3 545 2896");
        newUserJson.put("homeAddress", testAddress);
        newUserJson.put("password", "1357-H%nt762");


        //Mock a registered user
        registeredUser = new User("Jane", "Doe", "", "", "",
                "janedoe95@gmail.com", "1995-06-20", "+64 3 545 2896",
                null, "1357-H%nt3r2");

        registeredUser.setId(1);
        registeredUser.setPassword(passwordEncoder.encode(registeredUser.getPassword()));
        given(userRepository.findByEmail("janedoe95@gmail.com")).willReturn(List.of(registeredUser));
        given(userRepository.findById(1)).willReturn(Optional.of(registeredUser));


        testIncorrectLogin.put("email", "janedoe95@gmail.com");
        testIncorrectLogin.put("password", "1357-H%nt3r4");
        testCorrectLogin.put("email", "janedoe95@gmail.com");
        testCorrectLogin.put("password", "1357-H%nt3r2");


    }


    /**
     * Tests that:
     * Login fails for a non-registered user
     * and an InvalidLoginException is thrown
     */
    @Test
    void loginFailsBeforeRegistering() {
        Assertions.assertThrows(InvalidLoginException.class, () -> {
            userController.authenticate(testNonexistentLogin);
        });
    }

    /**
     * Tests that:
     * Registering a new user succeeds
     * and the user (and their address) are added.
     * Note: there is actually a 400 response here because
     * the registration is successful but the call to authenticate() isn't.
     * This is because the repository has been mocked so that no user
     * with the email IWantToRegister@icloud.com exists, meaning we can
     * add them successfully. But then the authenticate() function
     * tries to find them by email to check their password and log
     * them in, which fails because they don't exist in the repository.
     * Testing authenticate() with an existing user is done in
     * correctLogin() below.
     * @throws Exception exception thrown.
     */
    @Test
    void registerUser() throws Exception {

        mvc.perform(MockMvcRequestBuilders
                .post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newUserJson.toString())
                .accept(MediaType.APPLICATION_JSON));

        // This captures the arguments given to the mocked repository
        ArgumentCaptor<Address> addressArgumentCaptor = ArgumentCaptor.forClass(Address.class);
        verify(addressRepository).save(addressArgumentCaptor.capture());

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());

    }


    /**
     * Tests that:
     * Authentication with wrong password fails
     * and a 400 response is given
     * @throws Exception exception thrown.
     */
    @Test
    void incorrectLogin() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(testIncorrectLogin.toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    /**
     * Tests that:
     * authentication with correct login credentials succeeds
     * and that a 200 response is given
     * @throws Exception exception thrown.
     */
    @Test
    void correctLogin() throws Exception {

        mvc.perform(MockMvcRequestBuilders
                .post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(testCorrectLogin.toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

}
