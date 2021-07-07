package org.seng302.project.serviceLayer.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.seng302.project.repositoryLayer.model.Address;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.AddressRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests related to the DGAA
 */
@SpringBootTest
class DGAATest {

    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final AddressRepository addressRepository = Mockito.mock(AddressRepository.class);
    private final ArrayList<User> userList = new ArrayList<>();
    private final String dgaaRole = "defaultGlobalApplicationAdmin";
    private DGAAChecker dgaaChecker;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private SpringEnvironment springEnvironment;
    private String defaultEmail = "admin";
    private String defaultPassword = "password";

    @BeforeEach
    void init() {
        dgaaChecker = new DGAAChecker(addressRepository, userRepository, springEnvironment, passwordEncoder);
        dgaaChecker.setUserRepository(userRepository);
        dgaaChecker.setAddressRepository(addressRepository);
        userList.clear();

        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    userList.add(user);
                    return null;
                });
        Mockito.when(userRepository.findByRole(dgaaRole))
                .thenReturn(userList);

        if (System.getenv("DGAA_EMAIL") != null) {
            defaultEmail = System.getenv("DGAA_EMAIL");
        }
        if (System.getenv("DGAA_PASSWORD") != null) {
            defaultPassword = System.getenv("DGAA_PASSWORD");
        }
    }

    /**
     * Test that no DGAA is found before one has been created
     */
    @Test
    void testDGAACheckerWithNonexistentDGAA() {
        Assertions.assertTrue(userRepository.findByRole(dgaaRole).isEmpty());
        Assertions.assertFalse(dgaaChecker.dgaaExists());
    }

    /**
     * Tests that when a DGAA is created, it is found
     */
    @Test
    void testDGAACheckerWithExistingDGAA() {
        Address dgaaAddress = new Address("", "", "", "", "New Zealand", "");

        User dgaa = new User("Admin", "Admin", "Admin", "Admin",
                "The DGAA", "dgaa@resale.com",
                "1999-04-27", "+64 3 555 0129", dgaaAddress,
                "1AmTheDGAA");

        dgaa.setRole(dgaaRole);
        userRepository.save(dgaa);

        Assertions.assertFalse(userRepository.findByRole(dgaaRole).isEmpty());
        Assertions.assertTrue(dgaaChecker.dgaaExists());

    }

    /**
     * Test that the default global application admin is created with username and password set by environment.
     */
    @Test
    void testCreateDefaultAdmin() {
        User defaultAdmin = dgaaChecker.createDGAA();
        assertEquals(defaultEmail, defaultAdmin.getEmail());
        assertTrue(passwordEncoder.matches(defaultPassword, defaultAdmin.getPassword()));
        assertEquals(dgaaRole, defaultAdmin.getRole());
    }

    /**
     * Tests that a new DGAA is successfully added to user repository when not present.
     */
    @Test
    void testCreateDefaultAdminAndAddToRepository() {
        dgaaChecker.dgaaCheck();
        Assertions.assertFalse(userRepository.findByRole(dgaaRole).isEmpty());
    }

}
