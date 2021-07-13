package org.seng302.project.serviceLayer.service;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repositoryLayer.model.Image;
import org.seng302.project.repositoryLayer.model.Message;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.dto.user.CreateUserDTO;
import org.seng302.project.serviceLayer.dto.user.LoginCredentialsDTO;
import org.seng302.project.serviceLayer.dto.user.UserLoginResponseDTO;
import org.seng302.project.serviceLayer.exceptions.register.ExistingRegisteredEmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * UserService unit tests
 */
@SpringBootTest
class UserServiceTest extends AbstractInitializer {

    private User testUser;

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        this.initialise();
        testUser = this.getTestUser();
    }


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

    /**
     * Tries Successfully creating a user
     */
    @Test
    public void tryCreatingUserSuccess() throws Exception {
        //doReturn(new UserLoginResponseDTO(1)).when(userService.login(any(LoginCredentialsDTO.class)));

        CreateUserDTO dto = new CreateUserDTO(
                testUser.getFirstName(),
                testUser.getLastName(),
                testUser.getMiddleName(),
                testUser.getNickname(),
                testUser.getBio(),
                testUser.getEmail(),
                testUser.getDateOfBirth(),
                testUser.getPhoneNumber(),
                testUser.getHomeAddress(),
                testUser.getPassword());

        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(testUser);

        userService.createUser(dto);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();

        System.out.println(savedUser);
    }


    /**
     * Tries creating an existing user checks that a ExistingRegisteredEmailException is thrown
     */
    @Test
    public void tryCreatingExistingUser() {
        CreateUserDTO dto = new CreateUserDTO(
                testUser.getFirstName(),
                testUser.getLastName(),
                testUser.getMiddleName(),
                testUser.getNickname(),
                testUser.getBio(),
                testUser.getEmail(),
                testUser.getDateOfBirth(),
                testUser.getPhoneNumber(),
                testUser.getHomeAddress(),
                testUser.getPassword());

        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(List.of(testUser));

        Assertions.assertThrows(ExistingRegisteredEmailException.class, () -> userService.createUser(dto));
    }

    /**
     * Tries creating user with an invalid email
     */
    @Test
    public void createUserInvalidEmail() {
        CreateUserDTO dto = new CreateUserDTO(
                testUser.getFirstName(),
                testUser.getLastName(),
                testUser.getMiddleName(),
                testUser.getNickname(),
                testUser.getBio(),
                "invalid@",
                testUser.getDateOfBirth(),
                testUser.getPhoneNumber(),
                testUser.getHomeAddress(),
                testUser.getPassword());

        Assertions.assertThrows(InternalAuthenticationServiceException.class, () -> userService.createUser(dto));
    }

    /**
     * Tries creating user with an invalid password
     */
    @Test
    public void createUserInvalidPassword() {
        CreateUserDTO dto = new CreateUserDTO(
                testUser.getFirstName(),
                testUser.getLastName(),
                testUser.getMiddleName(),
                testUser.getNickname(),
                testUser.getBio(),
                testUser.getEmail(),
                testUser.getDateOfBirth(),
                testUser.getPhoneNumber(),
                testUser.getHomeAddress(),
                "BadPassword");

        Assertions.assertThrows(InternalAuthenticationServiceException.class, () -> userService.createUser(dto));
    }
}
