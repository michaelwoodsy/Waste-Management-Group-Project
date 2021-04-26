package org.seng302.project.controller;

import org.json.JSONArray;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.project.model.Address;
import org.seng302.project.model.AddressRepository;
import org.seng302.project.model.User;
import org.seng302.project.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

/**
 * Unit tests for SearchController
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SearchControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private MockMvc mvc;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

    }

    /**
     * Creates, stores and returns test users
     */
    public ArrayList<Integer> generateTesUsers() {
        ArrayList<Integer> testUserIds = new ArrayList<>();

        //Populate database with test users
        Address testAddress1 = new Address();
        User testUser1 = new User("John", "Smith", "Hector", "Jonny",
                "Likes long walks on the beach", "john99@gmail.com", "1999-04-27",
                "+64 3 555 0129", testAddress1, "1337-H%nt3r2");
        testUser1.setPassword(passwordEncoder.encode(testUser1.getPassword()));
        addressRepository.save(testAddress1);
        userRepository.save(testUser1);
        testUserIds.add(testUser1.getId());

        Address testAddress2 = new Address();
        User testUser2 = new User("Hector", "Jones", "Micheal", "",
                "Likes swimming with dolphins", "hectordolphin77@gmail.com", "2000-04-24",
                "+64 3 123 1234", testAddress2, "%3ll1Ng");
        testUser2.setPassword(passwordEncoder.encode(testUser2.getPassword()));
        addressRepository.save(testAddress2);
        userRepository.save(testUser2);
        testUserIds.add(testUser2.getId());

        Address testAddress3 = new Address();
        User testUser3 = new User("Joshua", "Hector", "James", "Josh",
                "Likes eating cake", "cakeman44@gmail.com", "2000-03-16",
                "+64 3 321 1234", testAddress3, "637*hng%#");
        testUser3.setPassword(passwordEncoder.encode(testUser3.getPassword()));
        addressRepository.save(testAddress3);
        userRepository.save(testUser3);
        testUserIds.add(testUser3.getId());

        Address testAddress4 = new Address();
        User testUser4 = new User("Hector-Anne", "Berry", "Rose", "Hector",
                "Likes riding horses", "horsechick72@gmail.com", "2000-06-26",
                "+64 3 321 4321", testAddress4, "#2564nhgr");
        testUser4.setPassword(passwordEncoder.encode(testUser4.getPassword()));
        addressRepository.save(testAddress4);
        userRepository.save(testUser4);
        testUserIds.add(testUser4.getId());

        Address testAddress5 = new Address();
        User testUser5 = new User("Sarah", "Bealing", "Maynita", "Chester",
                "Likes SENG302", "smb272@gmail.com", "2000-05-21",
                "+64 3 012 3456", testAddress5, "&@%^mnhde");
        testUser5.setPassword(passwordEncoder.encode(testUser5.getPassword()));
        addressRepository.save(testAddress5);
        userRepository.save(testUser5);
        testUserIds.add(testUser5.getId());

        return testUserIds;
    }

    /**
     * Tries searching with searchQuery 'Hector'.
     * We expect a 200 response code and 4 search results.
     */
    @Test
    public void trySearchUsers() throws Exception {
        ArrayList<Integer> expectedUserIds = generateTesUsers();
        expectedUserIds.remove(4); //Only interested in first 4 (out of 5) test users

        RequestBuilder searchUsersRequest = MockMvcRequestBuilders
                .get("/users/search?searchQuery=Hector") //Running in Swagger API gives this URL
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("john99@gmail.com", "1337-H%nt3r2"));

        MvcResult searchUsersResponse = this.mvc.perform(searchUsersRequest)
                .andExpect(MockMvcResultMatchers.status().isOk()) // We expect a 200 response
                .andReturn();

        String searchResultString = searchUsersResponse.getResponse().getContentAsString();
        JSONArray searchResultArray = new JSONArray(searchResultString);

        //Check there are 4 results
        Assertions.assertEquals(expectedUserIds.size(), searchResultArray.length());

        ArrayList<Integer> resultUserIds = new ArrayList<>();
        resultUserIds.add(searchResultArray.getJSONObject(0).getInt("id"));
        resultUserIds.add(searchResultArray.getJSONObject(1).getInt("id"));
        resultUserIds.add(searchResultArray.getJSONObject(2).getInt("id"));
        resultUserIds.add(searchResultArray.getJSONObject(3).getInt("id"));

        //Check result ids are same as expected ids
        Assertions.assertTrue(resultUserIds.containsAll(expectedUserIds)
                && expectedUserIds.containsAll(resultUserIds));
    }

}
