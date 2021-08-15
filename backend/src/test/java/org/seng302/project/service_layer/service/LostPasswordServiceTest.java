package org.seng302.project.service_layer.service;

import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.*;
import org.seng302.project.repository_layer.repository.*;
import org.seng302.project.service_layer.dto.user.ChangePasswordDTO;
import org.seng302.project.service_layer.exceptions.NotAcceptableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import java.time.LocalDateTime;
import java.util.List;


@DataJpaTest
class LostPasswordServiceTest extends AbstractInitializer {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final ConformationTokenRepository conformationTokenRepository;

    private final LostPasswordService lostPasswordService;

    User testUser;
    ConformationToken conformationToken;

    @Autowired
    LostPasswordServiceTest(AddressRepository addressRepository,
                            UserRepository userRepository,
                            ConformationTokenRepository conformationTokenRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.conformationTokenRepository = conformationTokenRepository;

        this.lostPasswordService = new LostPasswordService(
                this.conformationTokenRepository,
                this.userRepository,
                passwordEncoder);
    }

    /**
     * Before each test, setup user and conformation token
     */
    @BeforeEach
    void setup() {
        this.initialise();
        testUser = this.getTestUser();
        addressRepository.save(testUser.getHomeAddress());
        testUser.setId(null);
        testUser = userRepository.save(testUser);

        conformationToken = new ConformationToken("123456789", testUser);
        conformationTokenRepository.save(conformationToken);
    }

    /**
     * Tests that a NotAcceptableException is thrown when someone tries validating a token that does not exist.
     */
    @Test
    void validateToken_doesNotExist_NotAcceptableException() {
        Assertions.assertThrows(NotAcceptableException.class,
                () -> lostPasswordService.validateToken("NotAToken"));

    }

    /**
     * Tests the successful case of validating a token and returning the users email
     */
    @Test
    void validateToken_success() {
        JSONObject responseJSON = lostPasswordService.validateToken(conformationToken.getToken());
        Assertions.assertNotNull(responseJSON);
        String email = responseJSON.get("email").toString();
        Assertions.assertEquals(testUser.getEmail(), email);
    }

    /**
     * Tests that a NotAcceptableException is thrown when someone tries editing a password with a token that does not exist.
     */
    @Test
    void editPassword_tokenDoesNotExist_NotAcceptableException() {
        Assertions.assertThrows(NotAcceptableException.class,
                () -> lostPasswordService.validateToken("NotAToken"));
    }

    /**
     * Tests the successful case of editing a users password
     */
    @Test
    void editPassword_success() {
        String newPassword = "NewPassword123";
        ChangePasswordDTO dto = new ChangePasswordDTO(conformationToken.getToken(), newPassword);
        lostPasswordService.changePassword(dto);

        Optional<User> editedUser = userRepository.findById(testUser.getId());
        Assertions.assertTrue(editedUser.isPresent());
        Assertions.assertTrue(passwordEncoder.matches(newPassword, editedUser.get().getPassword()));
    }

    /**
     * Tests that a confirmation token is successfully deleted after an hour
     */
    @Test
    void deleteTokenAfter1Hour_success() {
        Assertions.assertEquals(1, conformationTokenRepository.findAll().size());

        lostPasswordService.removeConfirmationTokenAfter1Hr();

        // To confirm it doesn't remove tokens if they have not been active for over an hour
        Assertions.assertEquals(1, conformationTokenRepository.findAll().size());

        conformationToken.setCreated(LocalDateTime.now().minusHours(2));
        lostPasswordService.removeConfirmationTokenAfter1Hr();

        // Now token should be removed
        Assertions.assertEquals(0, conformationTokenRepository.findAll().size());
    }
}
