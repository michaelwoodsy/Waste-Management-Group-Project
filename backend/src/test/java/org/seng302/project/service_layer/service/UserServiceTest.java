package org.seng302.project.service_layer.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.Address;
import org.seng302.project.repository_layer.model.User;
import org.seng302.project.repository_layer.repository.AddressRepository;
import org.seng302.project.repository_layer.repository.LikedSaleListingRepository;
import org.seng302.project.repository_layer.repository.UserRepository;
import org.seng302.project.service_layer.dto.address.AddressDTO;
import org.seng302.project.service_layer.dto.user.PostUserDTO;
import org.seng302.project.service_layer.dto.user.PutUserDTO;
import org.seng302.project.service_layer.exceptions.ForbiddenException;
import org.seng302.project.service_layer.exceptions.NoUserExistsException;
import org.seng302.project.service_layer.exceptions.dgaa.ForbiddenDGAAActionException;
import org.seng302.project.service_layer.exceptions.register.ExistingRegisteredEmailException;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * UserService unit tests
 * Checking for validity in user details is done through the DTO so its testing is in UserControllerTest
 */
class UserServiceTest extends AbstractInitializer {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final UserService userService;
    private User testUser;
    private User otherUser;
    private User testAdmin;
    private PostUserDTO testPostUserDTO;
    private PutUserDTO testPutUserDTO;

    UserServiceTest() {
        userRepository = Mockito.mock(UserRepository.class);
        addressRepository = Mockito.mock(AddressRepository.class);
        LikedSaleListingRepository likedSaleListingRepository = Mockito.mock(LikedSaleListingRepository.class);
        AuthenticationManager authenticationManager = Mockito.mock(AuthenticationManager.class);
        userService = new UserService(
                userRepository,
                addressRepository,
                likedSaleListingRepository,
                authenticationManager,
                passwordEncoder
        );
    }

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
                new AddressDTO(testUser.getHomeAddress()),
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
                new AddressDTO(otherUser.getHomeAddress()),
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
        //Mock the findAll call to return a page
        Page<User> users = Mockito.mock(Page.class);
        Mockito.when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(users);


        userService.searchUsers("Tom", 0, "");

        // check the repository was called once
        verify(userRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    /**
     * Checks the user repository is called the correct amount of times when it's a simple and quoted query string
     */
    @Test
    void searchUsers_quotedNameQuery_notUseContainsSpec() {
        //Mock the findAll call to return a page
        Page<User> users = Mockito.mock(Page.class);
        Mockito.when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(users);

        userService.searchUsers("\"Tom\"", 0, "");

        // check the repository was called once
        verify(userRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
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

    /**
     * Test that getting a user by email address returns the correct user
     */
    @Test
    void getUserByEmail_validEmail_returnsUser() {
        Mockito.when(userRepository.findByEmail(testUser.getEmail()))
                .thenReturn(List.of(testUser));

        User user = userService.getUserByEmail(testUser.getEmail());
        Assertions.assertEquals(testUser, user);
    }

    /**
     * Test that getting a user by email address with invalid email returns null
     */
    @Test
    void getUserByEmail_invalidEmail_returnsNull() {
        Mockito.when(userRepository.findByEmail(testUser.getEmail()))
                .thenReturn(Collections.emptyList());

        User user = userService.getUserByEmail(testUser.getEmail());
        Assertions.assertNull(user);
    }
}
