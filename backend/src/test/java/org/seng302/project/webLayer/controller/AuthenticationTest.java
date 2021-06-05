package org.seng302.project.webLayer.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests authentication. DISABLE_AUTHENTICATION must be set to false to run these tests.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class AuthenticationTest {

    private final JSONObject testAddress = new JSONObject();
    private final JSONObject testUserJson = new JSONObject();
    private final JSONObject testIncorrectLogin = new JSONObject();
    private final JSONObject testCorrectLogin = new JSONObject();
    private final JSONObject testNonexistentLogin = new JSONObject();
    private User testUser;

    @Autowired
    private WebApplicationContext context;
    private MockMvc mvc;

    @MockBean
    private UserRepository userRepository;

    @BeforeEach
    public void initialise() throws JSONException {
        testAddress.put("country", "New Zealand");


        //Mock a test user
        testUser = new User("Jane", "Doe", "", "", "",
                "janedoe95@gmail.com", "1995-06-20", "+64 3 545 2896",
                null, "1357-H%nt3r2");

        testUser.setId(1);
        given(userRepository.findByEmail("janedoe95@gmail.com")).willReturn(List.of(testUser));
        given(userRepository.findById(1)).willReturn(Optional.of(testUser));

        testUserJson.put("firstName", testUser.getFirstName());
        testUserJson.put("lastName", testUser.getLastName());
        testUserJson.put("email", testUser.getEmail());
        testUserJson.put("dateOfBirth", testUser.getDateOfBirth());
        testUserJson.put("phoneNumber", testUser.getPhoneNumber());
        testUserJson.put("homeAddress", testAddress);
        testUserJson.put("password", testUser.getPassword());
        testNonexistentLogin.put("email", "notRegistered@gmail.com");
        testNonexistentLogin.put("password", "1357-H%nt3r4");
        testIncorrectLogin.put("email", "janedoe95@gmail.com");
        testIncorrectLogin.put("password", "1357-H%nt3r4");
        testCorrectLogin.put("email", "janedoe95@gmail.com");
        testCorrectLogin.put("password", "1357-H%nt3r2");

        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }


    /**
     * Tests that:
     * Login fails for a non-registered user
     * and a 400 response is given
     * @throws Exception exception thrown.
     */
    @Test
    void loginFailsBeforeRegistering() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(testNonexistentLogin.toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    /**
     * Tests that:
     * Registering a new user is successful,
     * then authentication with wrong password fails,
     * then authentication with correct login credentials succeeds
     * then we can get the new user
     *
     * @throws Exception exception thrown.
     */
    @Test
    void testRegisterAndLogin() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(testUserJson.toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mvc.perform(MockMvcRequestBuilders
                .post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(testIncorrectLogin.toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mvc.perform(MockMvcRequestBuilders
                .post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(testCorrectLogin.toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Integer id = testUser.getId();

        mvc.perform(MockMvcRequestBuilders
                .get(String.format("/users/%d", id))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic(testCorrectLogin.getString("email"),
                        testCorrectLogin.getString("password"))))
                .andExpect(status().isOk());
    }

}
