package org.seng302.project.service_layer.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.Business;
import org.seng302.project.repository_layer.model.User;
import org.seng302.project.repository_layer.repository.BusinessRepository;
import org.seng302.project.repository_layer.repository.UserRepository;
import org.seng302.project.service_layer.exceptions.ForbiddenException;
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

    /**
     * Tests that a NotAcceptableException is thrown when
     * getting a sales report and
     * a nonexistent business is provided
     */
    @Test
    void getSalesReport_invalidBusiness_notAcceptableException() {
        AppUserDetails appUser = new AppUserDetails(owner);
        String periodStart = LocalDate.now().minusDays(30).toString();
        String periodEnd = LocalDate.now().toString();

        Assertions.assertThrows(NotAcceptableException.class,
                () -> salesReportService.getSalesReport(782, periodStart,
                        periodEnd, "weekly", appUser));
    }

    /**
     * Tests that a NotAcceptableException is thrown when
     * getting a sales report and
     * an invalid date format is used
     */
    @Test
    void getSalesReport_invalidDateFormat_notAcceptableException() {
        AppUserDetails appUser = new AppUserDetails(owner);
        String periodStart = "07/01/21";
        String periodEnd = "07/11/21";
        Integer businessId = business.getId();

        Assertions.assertThrows(NotAcceptableException.class,
                () -> salesReportService.getSalesReport(businessId, periodStart,
                        periodEnd, "weekly", appUser));
    }

    /**
     * Tests that a NotAcceptableException is thrown when
     * getting a sales report and
     * an invalid date range is given
     */
    @Test
    void getSalesReport_invalidDateRange_notAcceptableException() {
        AppUserDetails appUser = new AppUserDetails(owner);
        String periodStart = "2021-08-27";
        String periodEnd = "2021-06-27";
        Integer businessId = business.getId();

        Assertions.assertThrows(NotAcceptableException.class,
                () -> salesReportService.getSalesReport(businessId, periodStart,
                        periodEnd, "weekly", appUser));
    }

    /**
     * Tests that a ForbiddenException is thrown when a user
     * that is not an admin tries to get a sales report
     */
    @Test
    void getSalesReport_notAdmin_forbiddenException() {
        AppUserDetails appUser = new AppUserDetails(testUser);
        String periodStart = LocalDate.now().minusDays(30).toString();
        String periodEnd = LocalDate.now().toString();
        Integer businessId = business.getId();

        Assertions.assertThrows(ForbiddenException.class,
                () -> salesReportService.getSalesReport(businessId, periodStart,
                        periodEnd, "weekly", appUser));
    }

    /**
     * Tests that a NotAcceptableException is thrown when
     * getting a sales report and
     * an invalid granularity is given
     */
    @Test
    void getSalesReport_invalidGranularity_notAcceptableException() {
        AppUserDetails appUser = new AppUserDetails(owner);
        String periodStart = LocalDate.now().minusDays(30).toString();
        String periodEnd = LocalDate.now().toString();
        Integer businessId = business.getId();

        Assertions.assertThrows(ForbiddenException.class,
                () -> salesReportService.getSalesReport(businessId, periodStart,
                        periodEnd, "day", appUser));
    }
}
