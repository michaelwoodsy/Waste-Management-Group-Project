package org.seng302.project.service_layer.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.*;
import org.seng302.project.repository_layer.repository.AddressRepository;
import org.seng302.project.repository_layer.repository.BusinessRepository;
import org.seng302.project.repository_layer.repository.SaleHistoryRepository;
import org.seng302.project.repository_layer.repository.UserRepository;
import org.seng302.project.service_layer.dto.sales_report.GetSalesReportDTO;
import org.seng302.project.service_layer.exceptions.BadRequestException;
import org.seng302.project.service_layer.exceptions.ForbiddenException;
import org.seng302.project.service_layer.exceptions.NotAcceptableException;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@DataJpaTest
class SalesReportServiceTest extends AbstractInitializer {

    private User testUser;
    private User owner;
    private Business business;
    private Sale soldToday;
    private Sale alsoSoldToday;
    private Sale soldYesterday;
    private Sale soldLastWeek;
    private Sale soldLastMonth;
    private Sale soldLastYear;

    private final SaleHistoryRepository saleHistoryRepository;
    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;
    private final SalesReportService salesReportService;

    @Autowired
    public SalesReportServiceTest(BusinessRepository businessRepository,
                                  UserRepository userRepository, SaleHistoryRepository saleHistoryRepository) {
        this.businessRepository = businessRepository;
        this.userRepository = userRepository;
        this.saleHistoryRepository = saleHistoryRepository;
        AddressRepository addressRepository = Mockito.mock(AddressRepository.class);
        ProductCatalogueService productCatalogueService = Mockito.mock(ProductCatalogueService.class);
        BusinessService businessService = new BusinessService(businessRepository, addressRepository, userRepository, productCatalogueService);
        this.salesReportService = new SalesReportService(businessService, this.saleHistoryRepository);
    }


    @BeforeEach
    public void setup() {
        testUser = this.getTestUser();
        testUser = userRepository.save(testUser);
        owner = this.getTestUserBusinessAdmin();
        owner = userRepository.save(owner);

        business = this.getTestBusiness();
        business.setPrimaryAdministratorId(owner.getId());
        business = businessRepository.save(business);

        Product product = new Product("PROD", "Product", "Just a product", "Me",
                2.00, business.getId());
        InventoryItem inventoryItem = new InventoryItem(product, 2, 0.50, 1.00,
                "2021-05-21", "2021-11-21", "2021-11-15", "2021-11-21");
        SaleListing saleListing = new SaleListing(business, inventoryItem, 1.00, "",
                LocalDateTime.now().minusDays(1), 2);

        soldToday = new Sale(saleListing);
        soldToday.setDateSold(LocalDateTime.now().minusHours(2));
        saleHistoryRepository.save(soldToday);

        alsoSoldToday = new Sale(saleListing);
        alsoSoldToday.setDateSold(LocalDateTime.now().minusHours(3));
        saleHistoryRepository.save(alsoSoldToday);

        soldYesterday = new Sale(saleListing);
        soldYesterday.setDateSold(LocalDateTime.now().minusDays(1));
        saleHistoryRepository.save(soldYesterday);
    }


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
     * Tests that a BadRequestException is thrown when
     * getting a sales report and
     * an invalid date format is used
     */
    @Test
    void getSalesReport_invalidDateFormat_badRequestException() {
        AppUserDetails appUser = new AppUserDetails(owner);
        String periodStart = "07/01/21";
        String periodEnd = "07/11/21";
        Integer businessId = business.getId();

        Assertions.assertThrows(BadRequestException.class,
                () -> salesReportService.getSalesReport(businessId, periodStart,
                        periodEnd, "weekly", appUser));
    }

    /**
     * Tests that a BadRequestException is thrown when
     * getting a sales report and
     * an invalid date range is given
     */
    @Test
    void getSalesReport_invalidDateRange_badRequestException() {
        AppUserDetails appUser = new AppUserDetails(owner);
        String periodStart = "2021-08-27";
        String periodEnd = "2021-06-27";
        Integer businessId = business.getId();

        Assertions.assertThrows(BadRequestException.class,
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
     * Tests that a BadRequestException is thrown when
     * getting a sales report and
     * an invalid granularity is given
     */
    @Test
    void getSalesReport_invalidGranularity_badRequestException() {
        AppUserDetails appUser = new AppUserDetails(owner);
        String periodStart = LocalDate.now().minusDays(30).toString();
        String periodEnd = LocalDate.now().toString();
        Integer businessId = business.getId();

        Assertions.assertThrows(BadRequestException.class,
                () -> salesReportService.getSalesReport(businessId, periodStart,
                        periodEnd, "day", appUser));
    }


    /**
     * Tests that getting a sales report with 'all' granularity
     * gets the expected sales
     */
    @Test
    void getSalesReport_allGranularity_success() {
        AppUserDetails appUser = new AppUserDetails(owner);
        String periodStart = LocalDate.now().minusDays(3).toString();
        String periodEnd = LocalDate.now().toString();
        Integer businessId = business.getId();

        List<GetSalesReportDTO> salesReport = salesReportService.getSalesReport(businessId, periodStart,
                periodEnd, "all", appUser);

        //expect 3 items: soldToday, alsoSoldToday and soldYesterday
        Assertions.assertEquals(3, salesReport.size());

    }

    /**
     * Tests that getting a sales report with 'daily' granularity
     * gets the expected sales
     */
    @Test
    void getSalesReport_dailyGranularity_success() {
        AppUserDetails appUser = new AppUserDetails(owner);
        String periodStart = LocalDate.now().minusDays(3).toString();
        String periodEnd = LocalDate.now().toString();
        Integer businessId = business.getId();

        List<GetSalesReportDTO> salesReport = salesReportService.getSalesReport(businessId, periodStart,
                periodEnd, "daily", appUser);

        //TODO: check response
        //expect 2 items: (soldToday, alsoSoldToday) and soldYesterday
    }

    /**
     * Tests that getting a sales report with 'weekly' granularity
     * gets the expected sales
     */
    @Test
    void getSalesReport_weeklyGranularity_success() {

    }

    /**
     * Tests that getting a sales report with 'monthly' granularity
     * gets the expected sales
     */
    @Test
    void getSalesReport_monthlyGranularity_success() {

    }

    /**
     * Tests that getting a sales report with 'yearly' granularity
     * gets the expected sales
     */
    @Test
    void getSalesReport_yearlyGranularity_success() {

    }
}
