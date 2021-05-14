package org.seng302.project.controller;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.project.exceptions.NoUserExistsException;
import org.seng302.project.exceptions.dgaa.DGAARevokeAdminSelfException;
import org.seng302.project.exceptions.dgaa.ForbiddenDGAAActionException;
import org.seng302.project.model.Address;
import org.seng302.project.model.AddressRepository;
import org.seng302.project.model.User;
import org.seng302.project.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

/**
 * Unit tests for DGAA methods in UserController.
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
public class DGAAControllerTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Integer testUserId;
    private Address testAddress;

    @Autowired
    public AddressRepository addressRepository;

    @Autowired
    private MockMvc mockMvc;

    private String dgaaEmail = "admin";
    private String dgaaPassword = "password";

    /**
     * Creates a user that we try adding/revoking admin rights of.
     * Reads the environment variables for DGAA email & password
     */
    @BeforeEach
    public void setup() {
        testUser = new User("Albert", "Brown", "", "", "", "albert.brown@gmail.com",
                "2000-05-21", "+64 3 555 0129", null, "1337-H%nt3r2");
        testAddress = new Address("", "", "", "", "New Zealand","");
        addressRepository.save(testAddress);
        testUserId = userRepository.save(testUser).getId();

        if (System.getenv("DGAA_EMAIL") != null) {
            dgaaEmail = System.getenv("DGAA_EMAIL");
        }
        if (System.getenv("DGAA_PASSWORD") != null) {
            dgaaPassword = System.getenv("DGAA_PASSWORD");
        }

    }

    /**
     * Tests that the DGAA can give someone admin privileges
     * Expect 200 response
     */
    @Test
    public void testDGAAAddAdmin() throws Exception {

        RequestBuilder putAdminRequest = MockMvcRequestBuilders
                .post("/users/{id}/makeadmin", testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic(dgaaEmail, dgaaPassword));

        this.mockMvc.perform(putAdminRequest)
                .andExpect(MockMvcResultMatchers.status().isOk()) // We expect a 200 response
                .andReturn();

        User retrievedUser = userRepository.findByEmail("albert.brown@gmail.com").get(0);
        Assertions.assertEquals("globalApplicationAdmin", retrievedUser.getRole());
    }

    /**
     * Tests that someone who is not a DGAA cannot give someone admin privileges
     * Expect 403 response
     */
    @Test
    public void testNotDGAAAddAdmin() throws Exception {
        testUser.setRole("user");
        testUserId = userRepository.save(testUser).getId();

        RequestBuilder putAdminRequest = MockMvcRequestBuilders
                .post("/users/{id}/makeadmin", testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("albert.brown@gmail.com", "1337-H%nt3r2"));

        MvcResult putAdminResponse = this.mockMvc.perform(putAdminRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 403 response
                .andReturn();

        String returnedExceptionString = putAdminResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new ForbiddenDGAAActionException().getMessage(), returnedExceptionString);
    }

    /**
     * Tests that a DGAA cannot give admin rights to a user that doesn't exist
     * Expect a 406 response
     */
    @Test
    public void testDGAAAddAdminNonexistentUser() throws Exception {

        if (userRepository.findById(128).isPresent()) {
            userRepository.deleteById(128);
        }

        RequestBuilder putAdminRequest = MockMvcRequestBuilders
                .post("/users/{id}/makeadmin", 128)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic(dgaaEmail, dgaaPassword));

        MvcResult putAdminResponse = this.mockMvc.perform(putAdminRequest)
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable()) // We expect a 406 response
                .andReturn();

        String returnedExceptionString = putAdminResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new NoUserExistsException(128).getMessage(), returnedExceptionString);

    }

    /**
     * Tests that someone who is a DGAA can revoke someone's admin privileges
     * Expect 200 response
     */
    @Test
    public void testDGAARevokeAdmin() throws Exception {
        RequestBuilder putAdminRequest = MockMvcRequestBuilders
                .post("/users/{id}/revokeadmin", testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic(dgaaEmail, dgaaPassword));

        this.mockMvc.perform(putAdminRequest)
                .andExpect(MockMvcResultMatchers.status().isOk()) // We expect a 200 response
                .andReturn();

        User retrievedUser = userRepository.findByEmail("albert.brown@gmail.com").get(0);
        Assertions.assertEquals("user", retrievedUser.getRole());

    }

    /**
     * Tests that someone who is not a DGAA cannot revoke someone's admin privileges
     * Expect 403 response
     */
    @Test
    public void testNotDGAARevokeAdmin() throws Exception {
        RequestBuilder putAdminRequest = MockMvcRequestBuilders
                .post("/users/{id}/revokeadmin", testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("albert.brown@gmail.com", "1337-H%nt3r2"));

        MvcResult putAdminResponse = this.mockMvc.perform(putAdminRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 403 response
                .andReturn();

        String returnedExceptionString = putAdminResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new ForbiddenDGAAActionException().getMessage(), returnedExceptionString);
    }


    /**
     * Tests that a DGAA cannot revoke admin rights from a user that doesn't exist
     * Expect a 406 response
     */
    @Test
    public void testDGAARevokeAdminNonexistentUser() throws Exception {

        if (userRepository.findById(128).isPresent()) {
            userRepository.deleteById(128);
        }

        RequestBuilder putAdminRequest = MockMvcRequestBuilders
                .post("/users/{id}/revokeadmin", 128)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic(dgaaEmail, dgaaPassword));

        MvcResult putAdminResponse = this.mockMvc.perform(putAdminRequest)
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable()) // We expect a 406 response
                .andReturn();

        String returnedExceptionString = putAdminResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new NoUserExistsException(128).getMessage(), returnedExceptionString);
    }

    /**
     * Tests that DGAA cannot revoke their own admin privileges
     * Expect 409 response
     */
    @Test
    public void testDGAARevokeAdminSelf() throws Exception {
        RequestBuilder putAdminRequest = MockMvcRequestBuilders
                .post("/users/{id}/revokeadmin", testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic(dgaaEmail, dgaaPassword));

        MvcResult putAdminResponse = this.mockMvc.perform(putAdminRequest)
                .andExpect(MockMvcResultMatchers.status().isConflict()) // We expect a 409 response
                .andReturn();

        String returnedExceptionString = putAdminResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new DGAARevokeAdminSelfException().getMessage(), returnedExceptionString);
    }
}
