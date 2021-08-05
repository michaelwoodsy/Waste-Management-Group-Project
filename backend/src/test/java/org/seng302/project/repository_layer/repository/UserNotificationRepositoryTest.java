package org.seng302.project.repository_layer.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.PurchaserNotification;
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

    @Test
    void purchaseNotification_whenSaved_noError() {
        userRepository.save(getTestUser());
        addressRepository.save(getTestUser().getHomeAddress());

        PurchaserNotification notification = new PurchaserNotification(
                getTestUser(), getSaleListings().get(0), getTestUser().getHomeAddress());
        Assertions.assertDoesNotThrow(() -> userNotificationRepository.save(notification));
    }

}
