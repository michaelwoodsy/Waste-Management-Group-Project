package org.seng302.project.serviceLayer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;

/**
 * UserService unit tests
 */
@SpringBootTest
public class UserServiceTest extends AbstractInitializer {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

//    @BeforeEach
//    void setup() {
//        when(userRepository).
//    }

    /**
     * Tests the searchUsers calls the repository
     */
    @Test
    void searchUsers_simple_callsRepository() {
        // Mock the repository returning 1500 users
        List<User> manyUsers = new ArrayList<>(Collections.nCopies(1500, getTestUser()));
    }


}
