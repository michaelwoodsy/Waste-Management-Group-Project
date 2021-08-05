package org.seng302.project.repository_layer.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.InterestedUserNotification;
import org.seng302.project.repository_layer.model.PurchaserNotification;
import org.seng302.project.repository_layer.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class UserNotificationRepositoryTest extends AbstractInitializer {

    @Autowired
    private UserNotificationRepository userNotificationRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AddressRepository addressRepository;

    private User testUser;

    @BeforeEach
    void setup() {
        testUser = userRepository.save(getTestUser());
        testUser.setHomeAddress(addressRepository.save(getTestUser().getHomeAddress()));
    }

    /**
     * Checks the PurchaseNotification entity can be saved to the UserNotification repository.
     */
    @Test
    void purchaseNotification_whenSaved_noError() {
        PurchaserNotification notification = new PurchaserNotification(
                testUser, getSaleListings().get(0), testUser.getHomeAddress());
        Assertions.assertDoesNotThrow(() -> userNotificationRepository.save(notification));
    }

    /**
     * Checks the PurchaseNotification entity can be saved to the UserNotification repository.
     */
    @Test
    void interestedUserNotification_whenSaved_noError() {
        InterestedUserNotification notification = new InterestedUserNotification(
                getTestUser(), getSaleListings().get(0));
        Assertions.assertDoesNotThrow(() -> userNotificationRepository.save(notification));
    }

}
