package org.seng302.project;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.seng302.project.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Class for testing main application functionality.
 * Includes tests for MainApplicationRunner
 */
@SpringBootTest
public class MainTest {

    @Autowired
    private static MainApplicationRunner mainApplicationRunner;
    @Autowired
    private static BCryptPasswordEncoder passwordEncoder;
    private static String defaultEmail;
    private static String defaultPassword;

    @BeforeAll
    public static void init() {
        defaultEmail = System.getenv("DGAA_EMAIL") != null ? System.getenv("DGAA_EMAIL") : "admin";
        defaultPassword = System.getenv("DGAA_PASSWORD") != null ? System.getenv("DGAA_PASSWORD") : "password";
    }

    /**
     * Test that the default global application admin is created with username and password set by environment.
     */
    @Test
    public void testCreateDGAA() {
        User defaultAdmin = mainApplicationRunner.createDGAA();
        assertEquals(defaultEmail, defaultAdmin.getEmail());
        assertTrue(passwordEncoder.matches(defaultPassword, defaultAdmin.getPassword()));
    }

}
