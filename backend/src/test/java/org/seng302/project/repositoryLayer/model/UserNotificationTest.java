package org.seng302.project.repositoryLayer.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

/**
 * Unit tests for UserNotification class
 */
@SpringBootTest
class UserNotificationTest {

    /**
     * Creates an example test UserNotification
     * Then checks all of the attributes of the new notification.
     */
    @Test
    void createTestCard() {
        User testAssignedUser = new User("John", "Smith", "Arthur", "Jonny",
                "Likes long walks on the beach", "johnsmith9999@gmail.com",
                "1999-04-27", "+64 3 555 0129", null,
                "1337-H%nt3r2");

        UserNotification testNotification = new UserNotification("Your user profile needs updating!", testAssignedUser);

        Assertions.assertNull(testNotification.getId());
        Assertions.assertNull(testNotification.getUser().getId());
        Assertions.assertEquals("John", testNotification.getUser().getFirstName());
        Assertions.assertEquals("Smith", testNotification.getUser().getLastName());
        Assertions.assertEquals("Your user profile needs updating!", testNotification.getMessage());
        Assertions.assertTrue(testNotification.getCreated().isBefore(LocalDateTime.now()) ||
                testNotification.getCreated().isEqual(LocalDateTime.now()));
        Assertions.assertTrue(testNotification.getCreated().isAfter(LocalDateTime.now().minusSeconds(5)));

    }
}
