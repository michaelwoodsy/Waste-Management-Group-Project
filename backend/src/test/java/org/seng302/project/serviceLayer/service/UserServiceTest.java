package org.seng302.project.serviceLayer.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repositoryLayer.model.*;
import org.seng302.project.repositoryLayer.repository.AddressRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.dto.user.CreateUserDTO;
import org.seng302.project.serviceLayer.exceptions.ForbiddenActionException;
import org.seng302.project.serviceLayer.exceptions.dgaa.ForbiddenDGAAActionException;
import org.seng302.project.serviceLayer.exceptions.register.ExistingRegisteredEmailException;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * UserService unit tests
 * Checking for validity in user details is done through the DTO so its testing is in UserControllerTest
 */
@SpringBootTest
class UserServiceTest extends AbstractInitializer {

    private User testUser;
    private User testAdmin;

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AddressRepository addressRepository;

    @BeforeEach
    void setup() {
        this.initialise();
        testUser = this.getTestUser();
        testAdmin = this.getTestSystemDGAA();
        testUser.setId(1);
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
    void tryCreatingUserSuccess() {

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

        given(userRepository.save(Mockito.any(User.class))).willReturn(testUser);

        userService.createUser(dto);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        //Called when checking DGA at startup as well
        Mockito.verify(userRepository, times(2)).save(userArgumentCaptor.capture());

        ArgumentCaptor<Address> addressArgumentCaptor = ArgumentCaptor.forClass(Address.class);
        //Called when checking DGA at startup as well
        Mockito.verify(addressRepository, times(2)).save(addressArgumentCaptor.capture());

        User createdUser = userArgumentCaptor.getValue();

        Assertions.assertEquals(testUser.getFirstName(), createdUser.getFirstName());
        Assertions.assertEquals(testUser.getLastName(), createdUser.getLastName());
        Assertions.assertEquals(testUser.getMiddleName(), createdUser.getMiddleName());
        Assertions.assertEquals(testUser.getNickname(), createdUser.getNickname());
        Assertions.assertEquals(testUser.getBio(), createdUser.getBio());
        Assertions.assertEquals(testUser.getEmail(), createdUser.getEmail());
        Assertions.assertEquals(testUser.getDateOfBirth(), createdUser.getDateOfBirth());
        Assertions.assertEquals(testUser.getPhoneNumber(), createdUser.getPhoneNumber());
        Assertions.assertEquals(testUser.getHomeAddress().getCountry(), createdUser.getHomeAddress().getCountry());
        Assertions.assertEquals(testUser.getRole(), createdUser.getRole());
    }


    /**
     * Tries creating an existing user checks that a ExistingRegisteredEmailException is thrown
     */
    @Test
    void tryCreatingExistingUser() {
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
     * tests method checkForbidden which checks if the logged in user is able to perform actions on the
     * user with id userId's account
     * This test checks that a ForbiddenActionException is thrown
     */
    @Test
    void testCheckForbidden_ForbiddenRequest() {
        Integer userId = 1000;
        AppUserDetails appUser = new AppUserDetails(testUser);
        when(userRepository.findByEmail(appUser.getUsername())).thenReturn(List.of(testUser));
        Assertions.assertThrows(ForbiddenActionException.class, () -> userService.checkForbidden(userId, appUser));
    }

    /**
     * tests method checkForbidden which checks if the logged in user is able to perform actions on the
     * user with id userId's account
     * This test checks that the user is allowed to make the request (no exception is thrown)
     */
    @Test
    void testCheckForbidden_AllowedRequest() {
        AppUserDetails appUser = new AppUserDetails(testUser);
        when(userRepository.findByEmail(appUser.getUsername())).thenReturn(List.of(testUser));
        Assertions.assertDoesNotThrow(() -> userService.checkForbidden(testUser.getId(), appUser));
    }

    /**
     * tests method checkForbidden which checks if the logged in user is able to perform actions on the
     * user with id userId's account
     * This test checks that the Admin user is allowed to make the request (no exception is thrown)
     */
    @Test
    void testCheckForbidden_AllowedRequestDGAA() {
        AppUserDetails appUser = new AppUserDetails(testAdmin);
        when(userRepository.findByEmail(appUser.getUsername())).thenReturn(List.of(testAdmin));
        Assertions.assertDoesNotThrow(() -> userService.checkForbidden(testUser.getId(), appUser));
    }

    /**
     * tests method checkRequesterIsDGAA which checks the requester is a DGAA
     * This test checks that the user is not a DGAA and a ForbiddenDGAAActionException is thrown
     */
    @Test
    void testCheckDGAA_notDGAA() {
        AppUserDetails appUser = new AppUserDetails(testUser);
        when(userRepository.findByEmail(appUser.getUsername())).thenReturn(List.of(testUser));
        Assertions.assertThrows(ForbiddenDGAAActionException.class, () -> userService.checkRequesterIsDGAA(appUser));
    }

    /**
     * tests method checkRequesterIsDGAA which checks the requester is a DGAA
     * This test checks that the user is a DGAA (no exception is thrown)
     */
    @Test
    void testCheckDGAA_isDGAA() {
        AppUserDetails appUser = new AppUserDetails(testAdmin);
        when(userRepository.findByEmail(appUser.getUsername())).thenReturn(List.of(testAdmin));
        Assertions.assertDoesNotThrow(() -> userService.checkRequesterIsDGAA(appUser));
    }
}
