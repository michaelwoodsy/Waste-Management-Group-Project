package org.seng302.project.controller;


//import org.json.JSONArray;
//import org.json.JSONObject;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.seng302.project.exceptions.*;
//import org.seng302.project.model.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.RequestBuilder;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
//import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureMockMvc
//public class CardControllerTest {
//
//    // Test users
//    private User user;
//    private final String userEmail = "basicUser@gmail.com";
//    private final String userPassword = "123";
//
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private WebApplicationContext context;
//
//    @Autowired
//    private BCryptPasswordEncoder passwordEncoder;
//
//    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    private CardRepository cardRepository;
//
//
//    /**
//     * Creates the user if it's not already created.
//     * If it is already created, the user is returned.
//     * @return User
//     */
//    private User createUser(User wantedUser) {
//        if (userRepository.findByEmail(wantedUser.getEmail()).size() > 0) {
//            // Already exists, return it
//            return(userRepository.findByEmail(wantedUser.getEmail()).get(0));
//        } else {
//            // User doesn't exist, save it to repository
//            wantedUser.setPassword(passwordEncoder.encode(wantedUser.getPassword()));
//            userRepository.save(wantedUser);
//            return wantedUser;
//        }
//    }
//
//    private Card createCard(String section) {
//        return new Card(user, section, "Test Card", "Test card description");
//    }
//
//    @BeforeEach
//    public void initialise() {
//        mockMvc = MockMvcBuilders
//                .webAppContextSetup(context)
//                .apply(springSecurity())
//                .build();
//
//        // Create the user
//        user = createUser(new User("John", "Smith", "Bob", "Jonny",
//                "Likes long walks on the beach", userEmail, "1999-04-27",
//                "+64 3 555 0129", null, userPassword));
//    }
//
//    @AfterEach
//    public void tearDown() {
//        cardRepository.deleteAll();
//        userRepository.delete(user);
//    }
//
//    @Test
//    public void checkUnauthenticatedRequest() throws Exception {
//        mockMvc.perform(get("/cards/{id}", 1))
//                .andExpect(status().isUnauthorized());
//    }
//
//    /**
//     * Test GETting card from endpoint
//     */
//    @Test
//    public void testGetSingleCard() throws Exception {
//        Card testCard = createCard("ForSale");
//        cardRepository.save(testCard);
//
//        MvcResult returnedCardResult = mockMvc.perform(get("/cards/{id}", testCard.getId())
//                .with(httpBasic(userEmail, userPassword)))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        String returnedCardString = returnedCardResult.getResponse().getContentAsString();
//        JSONObject returnedCard = new JSONObject(returnedCardString);
//
//        assertEquals(testCard.getSection(), returnedCard.get("section"));
//        assertEquals(testCard.getTitle(), returnedCard.get("title"));
//        assertEquals(testCard.getDescription(), returnedCard.get("description"));
//        assertNotNull(returnedCard.get("created"));
//        assertNotNull(returnedCard.get("displayPeriodEnd"));
//    }
//
//    /**
//     * Test getting card that does not exist
//     */
//    @Test
//    public void testGetCardDoesNotExist() throws Exception {
//        //Make sure the card repository is empty
//        cardRepository.deleteAll();
//
//        RequestBuilder getCardRequest = MockMvcRequestBuilders
//                .get("/cards/{id}/", 1)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)
//                .with(httpBasic(userEmail, userPassword));
//
//        MvcResult getCardResponse = this.mockMvc.perform(getCardRequest)
//                .andExpect(MockMvcResultMatchers.status().isNotAcceptable()) // We expect a 406 response
//                .andReturn();
//
//        String returnedExceptionString = getCardResponse.getResponse().getContentAsString();
//        Assertions.assertEquals(new NoCardExistsException(1).getMessage(), returnedExceptionString);
//    }
//}

//
//
//package org.seng302.project.controller;
//
//
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.project.exceptions.NoCardExistsException;
import org.seng302.project.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CardController.class)
public class CardControllerTest {
    private User testUser;
    private Card testCard;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardRepository cardRepository;

    @MockBean
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        // Create mock user
        testUser = new User("John", "Smith", "Bob", "Jonny",
                "Likes long walks on the beach", "test@gmail.com", "1999-04-27",
                "+64 3 555 0129", null, "");
        testUser.setId(1);
        given(userRepository.findByEmail("test@gmail.com")).willReturn(List.of(testUser));

        // Create mock card
        testCard = new Card(testUser, "ForSale", "Test Card", "Test card description");
        testCard.setId(1);
        given(cardRepository.findById(testCard.getId())).willReturn(Optional.of(testCard));
    }

    @Test
    public void checkUnauthenticatedRequest() throws Exception {
        mockMvc.perform(get("/cards/{id}", testCard.getId()))
                .andExpect(status().isUnauthorized());
    }

    /**
    * Test GETting card from endpoint
    */
    @Test
    public void testGetSingleCard() throws Exception {
        // Make the request
        MvcResult returnedCardResult = mockMvc.perform(get("/cards/{id}", testCard.getId())
                .with(user(testUser.getEmail())))
                .andExpect(status().isOk())
                .andReturn();

        // Check the repository was called
        verify(cardRepository, times(1)).findById(testCard.getId());

        String returnedCardString = returnedCardResult.getResponse().getContentAsString();
        JSONObject returnedCard = new JSONObject(returnedCardString);

        assertEquals(testCard.getSection(), returnedCard.get("section"));
        assertEquals(testCard.getTitle(), returnedCard.get("title"));
        assertEquals(testCard.getDescription(), returnedCard.get("description"));
        assertNotNull(returnedCard.get("created"));
        assertNotNull(returnedCard.get("displayPeriodEnd"));
    }

    /**
     * Test getting card that does not exist
     */
    @Test
    public void testGetCardDoesNotExist() throws Exception {
        //Make sure the card repository is empty
        cardRepository.deleteAll();

        RequestBuilder getCardRequest = MockMvcRequestBuilders
                .get("/cards/{id}/", 999)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(testUser.getEmail()));

        MvcResult getCardResponse = this.mockMvc.perform(getCardRequest)
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable()) // We expect a 406 response
                .andReturn();

        String returnedExceptionString = getCardResponse.getResponse().getContentAsString();
        assertEquals(new NoCardExistsException(999).getMessage(), returnedExceptionString);
    }

    /**
     * If the request is made by an unauthorized user, a 401 should be returned.
     */
    @Test
    void unauthenticatedUserReturns401() throws Exception {
        mockMvc.perform(get("/cards"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Checks a 400 response is returned if the "section" parameter isn't supplied.
     */
    @Test
    void missingSectionParamReturns400() throws Exception {
        mockMvc.perform(get("/cards")
                .with(user(testUser.getEmail())))
                .andExpect(status().isBadRequest());
    }

    /**
     * Checks a 400 response is returned if the "section" parameter isn't valid.
     */
    @Test
    void invalidSectionParamReturns400() throws Exception {
        mockMvc.perform(get("/cards")
                .with(user(testUser.getEmail()))
                .param("section", "beans"))
                .andExpect(status().isBadRequest());
    }

    /**
     * Given an authenticated user makes the request with a valid section parameter
     * a 200 response should be received.
     */
    @Test
    void validSectionParameterReturns200() throws Exception {
        mockMvc.perform(get("/cards")
                .with(user(testUser.getEmail()))
                .param("section", "ForSale"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/cards")
                .with(user(testUser.getEmail()))
                .param("section", "Wanted"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/cards")
                .with(user(testUser.getEmail()))
                .param("section", "Exchange"))
                .andExpect(status().isOk());
    }


}
