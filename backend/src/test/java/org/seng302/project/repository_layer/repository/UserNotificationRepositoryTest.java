package org.seng302.project.repository_layer.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.Business;
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

    @Autowired
    private BusinessRepository businessRepository;

    private User testUser;
    private Business testBusiness;

    @BeforeEach
    void setup() {
        // Save test user and associated entities
        testUser = getTestUser();
        testUser.setHomeAddress(addressRepository.save(testUser.getHomeAddress()));
        testUser = userRepository.save(testUser);

        // Save test business and associated entities
        testBusiness = getTestBusiness();
        testBusiness.setAddress(addressRepository.save(testBusiness.getAddress()));
        testBusiness.setPrimaryAdministratorId(testUser.getId());
        testBusiness = businessRepository.save(testBusiness);
    }

    /**
     * Checks the PurchaseNotification entity can be saved to the UserNotification repository.
     */
    @Test
    void purchaseNotification_whenSaved_noError() {
        PurchaserNotification notification = new PurchaserNotification(
                testUser, getSaleListings().get(0), testBusiness);
        Assertions.assertDoesNotThrow(() -> userNotificationRepository.save(notification));
    }

    /**
     * Checks the PurchaseNotification entity can be saved to the UserNotification repository.
     */
    @Test
    void interestedUserNotification_whenSaved_noError() {
        InterestedUserNotification notification = new InterestedUserNotification(
                testUser, getSaleListings().get(0));
        Assertions.assertDoesNotThrow(() -> userNotificationRepository.save(notification));
    }

}
