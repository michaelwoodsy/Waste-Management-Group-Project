package org.seng302.project.service_layer.service;

import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.*;
import org.seng302.project.repository_layer.repository.*;
import org.seng302.project.service_layer.dto.sale_listings.GetSaleListingDTO;
import org.seng302.project.service_layer.dto.sale_listings.PostSaleListingDTO;
import org.seng302.project.service_layer.dto.sale_listings.SearchSaleListingsDTO;
import org.seng302.project.service_layer.exceptions.BadRequestException;
import org.seng302.project.service_layer.exceptions.ForbiddenException;
import org.seng302.project.service_layer.exceptions.InvalidDateException;
import org.seng302.project.service_layer.exceptions.NotAcceptableException;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;


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

        this.lostPasswordService = new LostPasswordService(this.conformationTokenRepository);
    }

    /**
     * Before each test, setup four sale listings with different parameters
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
}
