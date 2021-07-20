package org.seng302.project.repositoryLayer.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.seng302.project.repositoryLayer.repository.AdminNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

/**
 * Unit tests for UserNotification class
 */
@SpringBootTest
class AdminNotificationTest {

    @Autowired
    private AdminNotificationRepository adminNotificationRepository;

    /**
     * Creates an example test UserNotification
     * Then checks all of the attributes of the new notification.
     */
    @Test
    void createTestCard() {

        AdminNotification testNotification = new AdminNotification("A new Keyword has been created, it needs to be reviewed");

        Assertions.assertNull(testNotification.getId());
        Assertions.assertEquals("A new Keyword has been created, it needs to be reviewed", testNotification.getMessage());
        Assertions.assertTrue(testNotification.getCreated().isBefore(LocalDateTime.now()) ||
                testNotification.getCreated().isEqual(LocalDateTime.now()));
        Assertions.assertTrue(testNotification.getCreated().isAfter(LocalDateTime.now().minusSeconds(5)));

    }
}
