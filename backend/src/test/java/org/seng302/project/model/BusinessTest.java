package org.seng302.project.model;

import org.junit.jupiter.api.Test;
import org.seng302.project.model.types.BusinessType;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests related to the Business class.
 */
public class BusinessTest {

    /**
     * Basic test to check Business class functionality.
     */
    @Test
    public void testCreateBusiness() {
        Business testBusiness = new Business("Test Business", "This business is a test.", "5 Lab Test Ave, New Zealand",
                "Retail Trade", 1);

        assertNull(testBusiness.getId());
        assertEquals("Test Business", testBusiness.getName());
        assertEquals("This business is a test.", testBusiness.getDescription());
        assertEquals("5 Lab Test Ave, New Zealand", testBusiness.getAddress());
        assertEquals("Retail Trade", testBusiness.getBusinessType());
        assertEquals(1, testBusiness.getPrimaryAdministratorId());
        assertTrue(testBusiness.getCreated().isBefore(LocalDateTime.now()));
        assertTrue(testBusiness.getCreated().isAfter(LocalDateTime.now().minusSeconds(1)));
        assertTrue(BusinessType.checkType(testBusiness.getBusinessType()));
    }

    /**
     * Test to ensure only valid business types are accepted.
     */
    @Test
    public void testBusinessTypes() {
        assertTrue(BusinessType.checkType("Accommodation and Food Services"));
        assertTrue(BusinessType.checkType("Retail Trade"));
        assertTrue(BusinessType.checkType("Charitable organisation"));
        assertTrue(BusinessType.checkType("Non-profit organisation"));
        assertFalse(BusinessType.checkType("Fake business type"));
    }

}