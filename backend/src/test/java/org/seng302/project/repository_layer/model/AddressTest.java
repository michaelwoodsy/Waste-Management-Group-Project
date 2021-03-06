package org.seng302.project.repository_layer.model;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.seng302.project.repository_layer.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Address class
 */
@SpringBootTest
class AddressTest {

    @Autowired
    private AddressRepository addressRepository;

    /**
     * Basic test to check Address class functionality. Also checks for a null address.
     */
    @Test
    @Order(1)
    void testCreateAddress() {
        Address testAddress = new Address("27", "Ilam Road", "Christchurch", "Canterbury", "New Zealand", "8041");
        Address testAddressNull = new Address();
        testAddressNull.setCountry("Australia");

        assertNull(testAddress.getId());
        assertNull(testAddressNull.getId());

        assertEquals("27", testAddress.getStreetNumber());
        assertNull(testAddressNull.getStreetNumber());

        assertEquals("Ilam Road", testAddress.getStreetName());
        assertNull(testAddressNull.getStreetName());

        assertEquals("Christchurch", testAddress.getCity());
        assertNull(testAddressNull.getCity());

        assertEquals("Canterbury", testAddress.getRegion());
        assertNull(testAddressNull.getRegion());

        assertEquals("New Zealand", testAddress.getCountry());
        assertEquals("Australia", testAddressNull.getCountry());

        assertEquals("8041", testAddress.getPostcode());
        assertNull(testAddressNull.getPostcode());

    }

    /**
     * Test to save Address to repository and then retrieve.
     */
    @Test
    @Order(2)
    void testAddressRepository() {
        Address testAddress = new Address("50", "Lab Test Ave", "Beijing", null, "China", null);
        addressRepository.save(testAddress);

        Address retrievedAddress = addressRepository.findByCountry("China").get(0);

        assertNotNull(retrievedAddress.getId());
        assertEquals("50", retrievedAddress.getStreetNumber());
        assertEquals("Lab Test Ave", retrievedAddress.getStreetName());
        assertEquals("Beijing", retrievedAddress.getCity());
        assertNull(retrievedAddress.getRegion());
        assertEquals("China", retrievedAddress.getCountry());
        assertNull(retrievedAddress.getPostcode());
    }
}
