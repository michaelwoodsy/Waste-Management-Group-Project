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
import org.seng302.project.serviceLayer.dto.user.PostUserDTO;
import org.seng302.project.serviceLayer.dto.user.PutUserDTO;
import org.seng302.project.serviceLayer.exceptions.ForbiddenException;
import org.seng302.project.serviceLayer.exceptions.NoUserExistsException;
import org.seng302.project.serviceLayer.exceptions.dgaa.ForbiddenDGAAActionException;
import org.seng302.project.serviceLayer.exceptions.register.ExistingRegisteredEmailException;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * UserService unit tests
 * Checking for validity in user details is done through the DTO so its testing is in UserControllerTest
 */
@SpringBootTest
class UserServiceTest extends AbstractInitializer {

    private User testUser;
    private User otherUser;
    private User testAdmin;

    private PostUserDTO testPostUserDTO;
    private PutUserDTO testPutUserDTO;

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AddressRepository addressRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        this.initialise();
        testUser = this.getTestUser();
        otherUser = this.getTestOtherUser();
        testAdmin = this.getTestSystemDGAA();

        testPostUserDTO = new PostUserDTO(
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

        testPutUserDTO = new PutUserDTO(
                testUser.getId(),
                otherUser.getFirstName(),
                otherUser.getLastName(),
                otherUser.getMiddleName(),
                otherUser.getNickname(),
                otherUser.getBio(),
                "new.email@newemail.com",
                otherUser.getDateOfBirth(),
                otherUser.getPhoneNumber(),
                otherUser.getHomeAddress(),
                "NewSecurePassword123",
                "password");

        mocks();
    }

    /**
     * Mocks used for more than one test
     */
    void mocks() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
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
        given(userRepository.save(Mockito.any(User.class))).willReturn(testUser);

        userService.createUser(testPostUserDTO);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        //Called when checking DGA at startup as well
        Mockito.verify(userRepository, atLeastOnce()).save(userArgumentCaptor.capture());

        ArgumentCaptor<Address> addressArgumentCaptor = ArgumentCaptor.forClass(Address.class);
        //Called when checking DGA at startup as well
        Mockito.verify(addressRepository, atLeastOnce()).save(addressArgumentCaptor.capture());

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

        Assertions.assertTrue(passwordEncoder.matches(testPostUserDTO.getPassword(), createdUser.getPassword()));
    }


    /**
     * Tries creating an existing user checks that a ExistingRegisteredEmailException is thrown
     */
    @Test
    void tryCreatingExistingUser() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(List.of(testUser));

        Assertions.assertThrows(ExistingRegisteredEmailException.class, () -> userService.createUser(testPostUserDTO));
    }

    /**
     * Tries Successfully editing a user
     */
    @Test
    void tryEditingUserSuccess() {
        //given(userRepository.save(Mockito.any(User.class))).willReturn(testUser);
        when(userRepository.findByEmail(testPutUserDTO.getEmail())).thenReturn(Collections.emptyList());

        userService.editUser(testPutUserDTO);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        //Called when checking DGA at startup as well
        Mockito.verify(userRepository, atLeastOnce()).save(userArgumentCaptor.capture());

        ArgumentCaptor<Address> addressArgumentCaptor = ArgumentCaptor.forClass(Address.class);
        //Called when checking DGA at startup as well
        Mockito.verify(addressRepository, atLeastOnce()).save(addressArgumentCaptor.capture());

        User editedUser = userArgumentCaptor.getValue();

        Assertions.assertEquals(testPutUserDTO.getFirstName(), editedUser.getFirstName());
        Assertions.assertEquals(testUser.getLastName(), editedUser.getLastName());
        Assertions.assertEquals(testUser.getMiddleName(), editedUser.getMiddleName());
        Assertions.assertEquals(testUser.getNickname(), editedUser.getNickname());
        Assertions.assertEquals(testUser.getBio(), editedUser.getBio());
        Assertions.assertEquals(testUser.getEmail(), editedUser.getEmail());
        Assertions.assertEquals(testUser.getDateOfBirth(), editedUser.getDateOfBirth());
        Assertions.assertEquals(testUser.getPhoneNumber(), editedUser.getPhoneNumber());
        Assertions.assertEquals(testUser.getHomeAddress().getCountry(), editedUser.getHomeAddress().getCountry());
        Assertions.assertEquals(testUser.getRole(), editedUser.getRole());

        Assertions.assertTrue(passwordEncoder.matches(testPutUserDTO.getNewPassword(), editedUser.getPassword()));
    }

    /**
     * Tries editing a user and checks that a NoUserExistsException is thrown as the user with ID does not exist
     */
    @Test
    void tryEditUserExistingEmail() {
        when(userRepository.findByEmail(testPutUserDTO.getEmail())).thenReturn(List.of(testUser));
        testPutUserDTO.setId(otherUser.getId());
        when(userRepository.findById(testPutUserDTO.getId())).thenReturn(Optional.of(otherUser));

        Assertions.assertThrows(ExistingRegisteredEmailException.class, () -> userService.editUser(testPutUserDTO));
    }

    /**
     * Tries editing a user and checks that a ExistingRegisteredEmailException is thrown
     */
    @Test
    void tryEditUserNonExistentId() {
        when(userRepository.findByEmail(testPutUserDTO.getEmail())).thenReturn(List.of(testUser));
        when(userRepository.findById(testPutUserDTO.getId())).thenReturn(Optional.empty());

        Assertions.assertThrows(NoUserExistsException.class, () -> userService.editUser(testPutUserDTO));
    }

    /**
     * tests method checkForbidden which checks if the logged in user is able to perform actions on the
     * user with id userId's account
     * This test checks that a ForbiddenException is thrown
     */
    @Test
    void testCheckForbidden_ForbiddenRequest() {
        Integer userId = 1000;
        AppUserDetails appUser = new AppUserDetails(testUser);
        when(userRepository.findByEmail(appUser.getUsername())).thenReturn(List.of(testUser));
        Assertions.assertThrows(ForbiddenException.class, () -> userService.checkForbidden(userId, appUser));
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
