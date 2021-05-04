package org.seng302.project.model;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.seng302.project.model.types.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests related to the Business class.
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BusinessTest {

    @Autowired
    private BusinessRepository businessRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AddressRepository addressRepository;

    /**
     * Basic test to check Business class functionality.
     */
    @Test
    @Order(1)
    public void testCreateBusiness() {
        Address testAddress = new Address("5", "Lab Test Ave", "Christchurch", null, "New Zealand", null);
        Business testBusiness = new Business("Test Business", "This business is a test.", testAddress,
                "Retail Trade", 1);

        assertNull(testBusiness.getId());
        assertEquals("Test Business", testBusiness.getName());
        assertEquals("This business is a test.", testBusiness.getDescription());
        assertEquals(testAddress, testBusiness.getAddress());
        assertEquals("Retail Trade", testBusiness.getBusinessType());
        assertEquals(1, testBusiness.getPrimaryAdministratorId());
        assertTrue(testBusiness.getCreated().isBefore(LocalDateTime.now()) || testBusiness.getCreated().isEqual(LocalDateTime.now()));
        assertTrue(testBusiness.getCreated().isAfter(LocalDateTime.now().minusSeconds(5)));
        assertTrue(BusinessType.checkType(testBusiness.getBusinessType()));
    }

    /**
     * Test to ensure only valid business types are accepted.
     */
    @Test
    @Order(2)
    public void testBusinessTypes() {
        assertTrue(BusinessType.checkType("Accommodation and Food Services"));
        assertTrue(BusinessType.checkType("Retail Trade"));
        assertTrue(BusinessType.checkType("Charitable organisation"));
        assertTrue(BusinessType.checkType("Non-profit organisation"));
        assertFalse(BusinessType.checkType("Fake business type"));
    }

    /**
     * Test to save Business to repository and then retrieve
     */
    @Test
    @Order(3)
    public void testBusinessRepository() {
        Address testAddress = new Address("5", "Lab Test Ave", "Christchurch", null, "New Zealand", null);
        Business testBusiness = new Business("Test Business", "This business is a test.", testAddress,
                "Retail Trade", 1);
        addressRepository.save(testAddress);
        businessRepository.save(testBusiness);
        Business retrievedBusiness = businessRepository.findByName("Test Business").get(0);

        assertNotNull(retrievedBusiness.getId());
        assertEquals("Test Business", retrievedBusiness.getName());
        assertEquals("This business is a test.", retrievedBusiness.getDescription());
        assertEquals(testAddress, retrievedBusiness.getAddress());
        assertEquals("Retail Trade", retrievedBusiness.getBusinessType());
        assertEquals(1, retrievedBusiness.getPrimaryAdministratorId());
        assertTrue(retrievedBusiness.getCreated().isBefore(LocalDateTime.now()));
        assertTrue(retrievedBusiness.getCreated().isAfter(LocalDateTime.now().minusSeconds(5)));
        assertTrue(BusinessType.checkType(retrievedBusiness.getBusinessType()));
    }

    /**
     * Tests adding a User to Business admin list
     */
    @Test
    @Order(4)
    public void testUserBusinessRelation() {
        Business testBusiness = businessRepository.findByName("Test Business").get(0);
        Address testUserAddress = new Address(null, null, null, null, "New Zealand", null);
        User testUser = new User("John", "Smith", "Josh", "Jonny",
                "Likes long walks on the beach", "jonnyj99@gmail.com",
                "1999-04-27", "+64 3 555 0129", testUserAddress,
                "1337-H%nt3r2");
        addressRepository.save(testUserAddress);
        userRepository.save(testUser);

        assertEquals(0, testBusiness.getAdministrators().size());
        assertEquals(0, testUser.getBusinessesAdministered().size());

        testUser = userRepository.findByEmail("jonnyj99@gmail.com").get(0);
        testBusiness = businessRepository.findByName("Test Business").get(0);

        testBusiness.addAdministrator(testUser);
        businessRepository.save(testBusiness);

        User retrievedUser = userRepository.findByEmail("jonnyj99@gmail.com").get(0);
        Business retrievedBusiness = businessRepository.findByName("Test Business").get(0);

        assertEquals(1, retrievedBusiness.getAdministrators().size());
        assertEquals(1, retrievedUser.getBusinessesAdministered().size());
        assertTrue(retrievedBusiness.userIsAdmin(retrievedUser.getId()));
        assertTrue(retrievedUser.businessIsAdministered(retrievedBusiness.getId()));
        assertEquals("John", retrievedBusiness.getAdministrators().get(0).getFirstName());
        assertEquals("Test Business", retrievedUser.getBusinessesAdministered().get(0).getName());
    }

}