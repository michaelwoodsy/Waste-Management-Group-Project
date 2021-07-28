package org.seng302.project.repository_layer.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.types.BusinessType;
import org.seng302.project.service_layer.dto.address.AddressDTO;
import org.seng302.project.service_layer.dto.business.PostBusinessDTO;

/**
 * Test class for unit testing methods of Business class.
 */
class BusinessTest extends AbstractInitializer {

    private User testUser;
    private User testUserBusinessAdmin;
    private User testSystemAdmin;
    private Business testBusiness;

    @BeforeEach
    void setup() {
        this.testUser = this.getTestUser();
        this.testUserBusinessAdmin = this.getTestUserBusinessAdmin();
        this.testSystemAdmin = this.getTestSystemAdmin();
        this.testBusiness = this.getTestBusiness();
    }

    /**
     * Tests that only valid business types are valid.
     */
    @Test
    void testBusinessTypes() {
        Assertions.assertTrue(BusinessType.checkType("Accommodation and Food Services"));
        Assertions.assertTrue(BusinessType.checkType("Retail Trade"));
        Assertions.assertTrue(BusinessType.checkType("Charitable organisation"));
        Assertions.assertTrue(BusinessType.checkType("Non-profit organisation"));
        Assertions.assertFalse(BusinessType.checkType("Fake business type"));
    }

    /**
     * Tests that userIsAdmin method returns false when given ID of user who is not an admin.
     */
    @Test
    void userIsAdmin_withNotAdmin_false() {
        Assertions.assertFalse(testBusiness.userIsAdmin(testUser.getId()));
    }

    /**
     * Tests that userIsAdmin method returns true when given ID of user who is a business admin.
     */
    @Test
    void userIsAdmin_withBusinessAdmin_true() {
        Assertions.assertTrue(testBusiness.userIsAdmin(testUserBusinessAdmin.getId()));
    }

    /**
     * Tests that userCanDoAction method returns false when given a user who is not a GAA or business admin.
     */
    @Test
    void testUserCanDoAction_withNotAdmin_false() {
        Assertions.assertFalse(testBusiness.userCanDoAction(testUser));
    }

    /**
     * Tests that userCanDoAction method returns true when given a business admin.
     */
    @Test
    void testUserCanDoAction_withBusinessAdmin_true() {
        Assertions.assertTrue(testBusiness.userCanDoAction(testUserBusinessAdmin));
    }

    /**
     * Tests that userCanDoAction method returns true when given a GAA.
     */
    @Test
    void testUserCanDoAction_withSystemAdmin_true() {
        Assertions.assertTrue(testBusiness.userCanDoAction(testSystemAdmin));
    }

    /**
     * Tests that business details are successfully updated
     */
    @Test
    void updateBusiness_changesBusinessDetails() {
        PostBusinessDTO requestDTO = new PostBusinessDTO(
                testBusiness.getName(),
                testBusiness.getDescription(),
                new AddressDTO(testBusiness.getAddress()),
                testBusiness.getBusinessType(),
                testBusiness.getPrimaryAdministratorId()
        );

        String newName = "New Business Name";
        String newDescription = "A new description for the business";
        String newCountry = "Australia";

        requestDTO.setName(newName);
        requestDTO.setDescription(newDescription);
        requestDTO.getAddress().setCountry(newCountry);

        testBusiness.updateBusiness(requestDTO);

        Assertions.assertEquals(newName, testBusiness.getName());
        Assertions.assertEquals(newDescription, testBusiness.getDescription());
        Assertions.assertEquals(newCountry, testBusiness.getAddress().getCountry());
    }
}