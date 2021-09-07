package org.seng302.project.service_layer.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.Business;
import org.seng302.project.repository_layer.model.User;
import org.seng302.project.repository_layer.repository.BusinessRepository;
import org.seng302.project.repository_layer.repository.UserRepository;
import org.seng302.project.service_layer.exceptions.NotAcceptableException;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;


@DataJpaTest
class SalesReportServiceTest extends AbstractInitializer {

    private User testUser;
    private User owner;
    private Business business;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private UserRepository userRepository;

    private SalesReportService salesReportService;

    @BeforeEach
    public void setup() {
        testUser = this.getTestUser();
        testUser = userRepository.save(testUser);
        owner = this.getTestUserBusinessAdmin();
        owner = userRepository.save(owner);

        business = this.getTestBusiness();
        business = businessRepository.save(business);
    }



    //TODO: tests for getSalesReport method
    //success
    //forbidden
    //invalid dates supplied e.g. start date after end date
    //invalid granularity supplied

    /**
     * Tests that a NotAcceptableException is thrown when
     * a nonexistent business is provided
     */
    @Test
    void getSalesReport_invalidBusiness_notAcceptableException() {
        AppUserDetails appUser = new AppUserDetails(testUser);
        String periodStart = LocalDate.now().minusDays(30).toString();
        String periodEnd = LocalDate.now().toString();

        Assertions.assertThrows(NotAcceptableException.class,
                () -> salesReportService.getSalesReport(782, periodStart,
                        periodEnd, "week", appUser));
    }

    //TODO: test invalid date format throws BadRequestException
}
