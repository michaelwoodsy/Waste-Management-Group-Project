package org.seng302.project.repository_layer.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.project.AbstractInitializer;

import java.time.LocalDateTime;

/**
 * Unit tests for User class
 */
class UserTest extends AbstractInitializer {

    User testUser;

    @BeforeEach
    void setup() {
        testUser = this.getTestUser();
    }

    /**
     * Creates the test user from the API.
     * Then checks all of the attributes of the new User.
     * (Except the businessesAdministered attribute)
     */
    @Test
    void createTestUser() {
        User testUser = new User("John", "Smith", "Arthur", "Jonny",
                "Likes long walks on the beach", "johnsmith9999@gmail.com",
                "1999-04-27", "+64 3 555 0129", null,
                "1337-H%nt3r2");

        Assertions.assertNull(testUser.getId());
        Assertions.assertEquals("John", testUser.getFirstName());
        Assertions.assertEquals("Smith", testUser.getLastName());
        Assertions.assertEquals("Arthur", testUser.getMiddleName());
        Assertions.assertEquals("Jonny", testUser.getNickname());
        Assertions.assertEquals("Likes long walks on the beach", testUser.getBio());
        Assertions.assertEquals("johnsmith9999@gmail.com", testUser.getEmail());
        Assertions.assertEquals("1999-04-27", testUser.getDateOfBirth());
        Assertions.assertEquals("+64 3 555 0129", testUser.getPhoneNumber());
        Assertions.assertNull(testUser.getHomeAddress());
        Assertions.assertEquals("1337-H%nt3r2", testUser.getPassword());
        Assertions.assertEquals("user", testUser.getRole());
        Assertions.assertTrue(testUser.getCreated().isAfter(LocalDateTime.now().minusSeconds(5)));
    }

    /**
     * Creates the test user and tests the isGAA method in the user model.
     */
    @Test
    void testIsGAAMethod() {
        User testUser = new User("John", "Smith", "Arthur", "Jonny",
                "Likes long walks on the beach", "johnsmith9999@gmail.com",
                "1999-04-27", "+64 3 555 0129", null,
                "1337-H%nt3r2");
        //Role is set to "user" when created so they shouldn't be a GAA
        Assertions.assertFalse(testUser.isGAA());

        testUser.setRole("globalApplicationAdmin");
        Assertions.assertTrue(testUser.isGAA());

        testUser.setRole("defaultGlobalApplicationAdmin");
        Assertions.assertTrue(testUser.isGAA());

        testUser.setRole("user");
        Assertions.assertFalse(testUser.isGAA());
    }

    /**
     * Test that a liked sale listing can be added to a user
     */
    @Test
    void testAddLikedListing() {
        SaleListing listing = this.getSaleListings().get(0);
        LikedSaleListing likedSaleListing = new LikedSaleListing(testUser, listing);

        Assertions.assertEquals(0, testUser.getLikedSaleListings().size());
        testUser.addLikedListing(likedSaleListing);
        Assertions.assertEquals(1, testUser.getLikedSaleListings().size());
    }

    /**
     * Test that a liked sale listing can be remvoed from a user
     */
    @Test
    void testRemoveLikedListing() {
        SaleListing listing = this.getSaleListings().get(0);
        LikedSaleListing likedSaleListing = new LikedSaleListing(testUser, listing);
        testUser.addLikedListing(likedSaleListing);

        Assertions.assertEquals(1, testUser.getLikedSaleListings().size());
        testUser.removeLikedListing(likedSaleListing);
        Assertions.assertEquals(0, testUser.getLikedSaleListings().size());
    }

}
