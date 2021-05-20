package org.seng302.project.controller;


import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.seng302.project.exceptions.NoUserExistsException;
import org.seng302.project.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class CardControllerTest {
    private User user;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private InventoryItemController inventoryItemController;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private AddressRepository addressRepository;

    private void createLoggedInUser() throws Exception {
        JSONObject testAddress = new JSONObject();
        testAddress.put("country", "New Zealand");

        JSONObject testUserJson = new JSONObject();
        testUserJson.put("firstName", "Jim");
        testUserJson.put("lastName", "Smith");
        testUserJson.put("email", "jimsmith@gmail.com");
        testUserJson.put("dateOfBirth", "1999-04-27");
        testUserJson.put("homeAddress", testAddress);
        testUserJson.put("password", "1337-H%nt3r2");

        testUserJson.put("middleName", "");
        testUserJson.put("nickname", "");
        testUserJson.put("bio", "");
        testUserJson.put("phoneNumber", "");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/users")
                .content(testUserJson.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void createAndRetrieveTestCard() throws Exception {


        createLoggedInUser();

        User loggedInUser = userRepository.findByEmail("jimsmith@gmail.com").get(0);

        JSONObject testCardJson = new JSONObject();
        testCardJson.put("creatorId", loggedInUser.getId());
        testCardJson.put("section", "ForSale");
        testCardJson.put("title", "1982 Lada Samara");
        testCardJson.put("description",
                "Beige, suitable for a hen house. Fair condition. Some rust. As is, where is. Will swap for budgerigar.");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/cards")
                .content(testCardJson.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("jimsmith@gmail.com", "1337-H%nt3r2")))
                .andExpect(status().isCreated());

        Card retrievedCard = cardRepository.findAllBySection("ForSale").get(0);

        Assertions.assertNotNull(retrievedCard.getCreator().getId());
        Assertions.assertEquals("Jim", retrievedCard.getCreator().getFirstName());
        Assertions.assertEquals("Smith", retrievedCard.getCreator().getLastName());

        Assertions.assertEquals("ForSale", retrievedCard.getSection());
        Assertions.assertEquals("1982 Lada Samara", retrievedCard.getTitle());
        Assertions.assertEquals("Beige, suitable for a hen house. Fair condition. Some rust. As is, where is. Will swap for budgerigar.",
                retrievedCard.getDescription());
        Assertions.assertTrue(retrievedCard.getCreated().isBefore(LocalDateTime.now()) || retrievedCard.getCreated().isEqual(LocalDateTime.now()));
        Assertions.assertTrue(retrievedCard.getCreated().isAfter(LocalDateTime.now().minusSeconds(5)));
        Assertions.assertTrue(retrievedCard.getDisplayPeriodEnd().isEqual(retrievedCard.getCreated().plusDays(14)));
    }

    @Test
    public void createTestCardWithoutRequiredFields() {


        Exception exception = Assertions.assertThrows(NoUserExistsException.class, () -> {

        });
    }
}
