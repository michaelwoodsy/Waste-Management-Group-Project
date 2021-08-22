package org.seng302.project.service_layer.service;

import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.*;
import org.seng302.project.repository_layer.repository.*;
import org.seng302.project.service_layer.dto.user.ChangePasswordDTO;
import org.seng302.project.service_layer.exceptions.BadRequestException;
import org.seng302.project.service_layer.util.SpringEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import java.time.LocalDateTime;


@DataJpaTest
class LostPasswordServiceTest extends AbstractInitializer {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final ConformationTokenRepository conformationTokenRepository;

    private final LostPasswordService lostPasswordService;

    private final EmailService emailService;

    User testUser;
    ConformationToken conformationToken;

    @Autowired
    LostPasswordServiceTest(AddressRepository addressRepository,
                            UserRepository userRepository,
                            ConformationTokenRepository conformationTokenRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.conformationTokenRepository = conformationTokenRepository;
        this.emailService = Mockito.mock(EmailService.class);
        SpringEnvironment springEnvironment = Mockito.mock(SpringEnvironment.class);

        this.lostPasswordService = new LostPasswordService(
                this.conformationTokenRepository,
                this.userRepository,
                passwordEncoder,
                this.emailService, springEnvironment);
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

        conformationToken = new ConformationToken(testUser);
        conformationTokenRepository.save(conformationToken);
    }

    /**
     * Tests that a BadRequestException is thrown when someone tries validating a token that does not exist.
     */
    @Test
    void validateToken_doesNotExist_BadRequestException() {
        Assertions.assertThrows(BadRequestException.class,
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
     * Tests that a BadRequestException is thrown when someone tries editing a password with a token that does not exist.
     */
    @Test
    void editPassword_tokenDoesNotExist_BadRequestException() {
        Assertions.assertThrows(BadRequestException.class,
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

    /**
     * Tests that the sendPasswordResetEmail creates a new Confirmation Token for the right user
     */
    @Test
    void sendPasswordResetEmail_createsToken() {
        //Remove the token saved in the setup() method
        conformationTokenRepository.deleteAll();
        lostPasswordService.sendPasswordResetEmail(testUser.getEmail());

        Optional<ConformationToken> newToken = conformationTokenRepository.findByUser(testUser);
        Assertions.assertTrue(newToken.isPresent());
        Assertions.assertEquals(testUser.getEmail(), newToken.get().getUser().getEmail());
    }

    /**
     * Tests that the sendPasswordResetEmail sends the user an email
     */
    @Test
    void sendPasswordResetEmail_sendsEmail() {
        lostPasswordService.sendPasswordResetEmail(testUser.getEmail());

        ArgumentCaptor<String> emailAddress = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subject = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> text = ArgumentCaptor.forClass(String.class);
        Mockito.verify(emailService).sendEmail(emailAddress.capture(), subject.capture(),
                text.capture());

        Assertions.assertEquals(testUser.getEmail(), emailAddress.getValue());
        Assertions.assertEquals("Resale: Reset your password", subject.getValue());
    }

    /**
     * Tests the sendPasswordResetEmail method throws a BadRequestException when an invalid email is provided
     */
    @Test
    void sendPasswordResetEmail_invalidEmail() {
        Assertions.assertThrows(BadRequestException.class,
                () -> lostPasswordService.sendPasswordResetEmail(""));
    }
}
