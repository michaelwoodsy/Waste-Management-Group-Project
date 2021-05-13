package org.seng302.project.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.project.MainApplicationRunner;
import org.seng302.project.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Class for testing DefaultAdmin utility class.
 */
@SpringBootTest
public class DefaultAdminTest {

    @Autowired
    private MainApplicationRunner mainApplicationRunner;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    private String defaultEmail = "admin";
    private String defaultPassword = "password";

    @BeforeEach
    public void init() {
        if (System.getenv("DGAA_EMAIL") != null) {
            defaultEmail = System.getenv("DGAA_EMAIL");
        }
        if (System.getenv("DGAA_PASSWORD") != null) {
            defaultPassword = System.getenv("DGAA_PASSWORD");
        }
    }

    /**
     * Test that the default global application admin is created with username and password set by environment.
     */
    @Test
    public void testCreateDefaultAdmin() {
        User defaultAdmin = mainApplicationRunner.createDGAA();
        assertEquals(defaultEmail, defaultAdmin.getEmail());
        assertTrue(passwordEncoder.matches(defaultPassword, defaultAdmin.getPassword()));
        assertEquals("defaultGlobalApplicationAdmin", defaultAdmin.getRole());
    }

}
