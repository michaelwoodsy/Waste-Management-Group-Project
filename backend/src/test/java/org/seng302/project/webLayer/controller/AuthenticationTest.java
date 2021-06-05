package org.seng302.project.webLayer.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.project.repositoryLayer.repository.CardRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests authentication. DISABLE_AUTHENTICATION must be set to false to run these tests.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AuthenticationTest {

    private final JSONObject testAddress = new JSONObject();
    private final JSONObject testUser = new JSONObject();
    private final JSONObject testIncorrectLogin = new JSONObject();
    private final JSONObject testCorrectLogin = new JSONObject();
    @Autowired
    private WebApplicationContext context;
    private MockMvc mvc;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CardRepository cardRepository;

    @BeforeEach
    public void initialise() throws JSONException {
        cardRepository.deleteAll();
        userRepository.deleteAll();
        testAddress.put("country", "New Zealand");
        testUser.put("firstName", "Jane");
        testUser.put("lastName", "Doe");
        testUser.put("email", "janedoe95@gmail.com");
        testUser.put("dateOfBirth", "1995-06-20");
        testUser.put("phoneNumber", "+64 3 545 2896");
        testUser.put("homeAddress", testAddress);
        testUser.put("password", "1357-H%nt3r2");
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
     * authentication fails before registering,
     * then registration is successful,
     * then authentication with wrong password fails,
     * then authentication with correct login credentials succeeds,
     * then we can GET the new user.
     *
     * @throws Exception exception thrown.
     */
    @Test
    public void testRegisterAndLogin() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(testCorrectLogin.toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mvc.perform(MockMvcRequestBuilders
                .post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(testUser.toString())
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

        Integer id = userRepository.findByEmail(testUser.getString("email")).get(0).getId();

        mvc.perform(MockMvcRequestBuilders
                .get(String.format("/users/%d", id))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic(testCorrectLogin.getString("email"),
                        testCorrectLogin.getString("password"))))
                .andExpect(status().isOk());
    }

}
