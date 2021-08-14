package org.seng302.project.repository_layer.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.ConformationToken;
import org.seng302.project.repository_layer.model.Sale;
import org.seng302.project.repository_layer.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DataJpaTest
class ConformationTokenRepositoryTest extends AbstractInitializer {

    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ConformationTokenRepository conformationTokenRepository;

    ConformationToken token;

    @BeforeEach
    void setup() {
        this.initialise();
        User user = this.getTestUser();
        addressRepository.save(user.getHomeAddress());
        user = userRepository.save(user);
        token = new ConformationToken("TOKEN", user);
    }

    /**
     * Basic test to show that the entity can be saved.
     */
    @Test
    void conformationToken_savingEntity_noError() {
        assertDoesNotThrow(() -> conformationTokenRepository.save(token));
    }

    /**
     * Basic test to show that the entity can be saved and is actually stored.
     */
    @Test
    void conformationToken_savingEntity_isSaved() {
        Assertions.assertEquals(0, conformationTokenRepository.findAll().size());
        conformationTokenRepository.save(token);
        Optional<ConformationToken> foundToken = conformationTokenRepository.findByToken(token.getToken());
        Assertions.assertTrue(foundToken.isPresent());
        Assertions.assertEquals(token.getToken(), foundToken.get().getToken());
        Assertions.assertEquals(token.getId(), foundToken.get().getId());
        Assertions.assertEquals(token.getCreated(), foundToken.get().getCreated());
        Assertions.assertEquals(token.getUser().getId(), foundToken.get().getUser().getId());
        Assertions.assertEquals(token.getUser().getEmail(), foundToken.get().getUser().getEmail());
    }
}
