package org.seng302.project.serviceLayer.service;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repositoryLayer.model.Keyword;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.mockito.Mockito.*;

/**
 * UserService unit tests
 */
@SpringBootTest
class UserServiceTest extends AbstractInitializer {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    /**
     * Checks the user repository is called the correct amount of times when a simple and non-quoted search
     */
    @Test
    void searchUsers_singleNameQuery_usesContainsSpec() {
        // Mock the findAll method to return an empty list
        when(userRepository.findAll()).thenReturn(List.of());

        userService.searchUsers("Tom");

        // findAll will be called twice if a simple and non quoted search is made, check the repository was called twice
        verify(userRepository, times(2)).findAll(any(Specification.class));
    }

    /**
     * Checks the user repository is called the correct amount of times when it's a simple and quoted query string
     */
    @Test
    void searchUsers_quotedNameQuery_notUseContainsSpec() {
        // Mock the findAll method to return an empty list
        when(userRepository.findAll()).thenReturn(List.of());

        userService.searchUsers("\"Tom\"");

        // check the repository was called once
        verify(userRepository, times(1)).findAll(any(Specification.class));
    }
}
