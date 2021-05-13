package org.seng302.project.model;

import org.junit.Test;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Tests related to the DGAA
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DGAATest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AddressRepository addressRepository;

    private DGAAChecker dgaaChecker;

    @BeforeEach
    public void setup() {
        dgaaChecker = DGAAChecker.getInstance(userRepository);
    }

    /**
     * Test that no DGAA is found before one has been created
     */
    @Test
    @Order(1)
    public void testDGAACheckerWithNonexistentDGAA() {
        Assertions.assertTrue(userRepository.findByRole("defaultGlobalApplicationAdmin").isEmpty());
        Assertions.assertFalse(dgaaChecker.dgaaExists());
    }

    /**
     * Tests that when a DGAA is created, it is found
     */
    @Test
    @Order(2)
    public void testDGAACheckerWithExistingDGAA() {
        Address dgaaAddress = new Address("", "", "", "", "New Zealand", "");

        User dgaa = new User("Admin", "Admin", "Admin", "Admin",
                "The DGAA", "dgaa@resale.com",
                "1999-04-27", "+64 3 555 0129", dgaaAddress,
                "1AmTheDGAA");

        dgaa.setRole("defaultGlobalApplicationAdmin");
        addressRepository.save(dgaa.getHomeAddress());
        userRepository.save(dgaa);

        Assertions.assertFalse(userRepository.findByRole("defaultGlobalApplicationAdmin").isEmpty());
        Assertions.assertTrue(dgaaChecker.dgaaExists());

    }

}
