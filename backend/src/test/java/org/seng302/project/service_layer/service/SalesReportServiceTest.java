package org.seng302.project.service_layer.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.*;
import org.seng302.project.repository_layer.repository.*;
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
    private final AddressRepository addressRepository;
    private final ReviewRepository reviewRepository;

    @Autowired
    public SalesReportServiceTest(BusinessRepository businessRepository,
                                  UserRepository userRepository,
                                  SaleHistoryRepository saleHistoryRepository,
                                  AddressRepository addressRepository,
                                  ReviewRepository reviewRepository) {
        this.businessRepository = businessRepository;
        this.userRepository = userRepository;
        this.saleHistoryRepository = saleHistoryRepository;
        this.addressRepository = addressRepository;
        this.reviewRepository = reviewRepository;

        ProductCatalogueService productCatalogueService = Mockito.mock(ProductCatalogueService.class);
        BusinessNotificationRepository businessNotificationRepository = Mockito.mock(BusinessNotificationRepository.class);
        BusinessService businessService = new BusinessService(businessRepository, addressRepository, userRepository,
                reviewRepository, productCatalogueService, businessNotificationRepository);
        this.salesReportService = new SalesReportService(businessService, this.saleHistoryRepository);
    }


    @BeforeEach
    public void setup() {
        testUser = this.getTestUser();
        testUser.setHomeAddress(null);
        testUser = userRepository.save(testUser);
        owner = this.getTestUserBusinessAdmin();
        addressRepository.save(owner.getHomeAddress());
        owner = userRepository.save(owner);

        business = this.getTestBusiness();
        business.setPrimaryAdministratorId(owner.getId());
        addressRepository.save(business.getAddress());
        business = businessRepository.save(business);

        Product product = new Product("PROD", "Product", "Just a product", "Me",
                2.00, business.getId());
        InventoryItem inventoryItem = new InventoryItem(product, 2, 0.50, 1.00,
                "2021-05-21", "2021-11-21", "2021-11-15", "2021-11-21");
        SaleListing saleListing = new SaleListing(business, inventoryItem, 1.00, "",
                LocalDateTime.now().minusDays(1), 2);

        saleHistoryRepository.deleteAll();

        soldToday = new Sale(saleListing);
        soldToday.setDateSold(LocalDateTime.now().minusHours(2));
        soldToday.setMoreInfo("Sold today");
        saleHistoryRepository.save(soldToday);

        alsoSoldToday = new Sale(saleListing);
        alsoSoldToday.setDateSold(LocalDateTime.now().minusHours(3));
        alsoSoldToday.setMoreInfo("Also sold today");
        saleHistoryRepository.save(alsoSoldToday);

        soldYesterday = new Sale(saleListing);
        soldYesterday.setDateSold(LocalDateTime.now().minusDays(1));
        soldYesterday.setMoreInfo("Sold yesterday");
        saleHistoryRepository.save(soldYesterday);

        soldLastWeek = new Sale(saleListing);
        soldLastWeek.setMoreInfo("Sold last week");
        soldLastWeek.setDateSold(LocalDateTime.now().minusDays(8));
        saleHistoryRepository.save(soldLastWeek);

        soldLastMonth = new Sale(saleListing);
        soldLastMonth.setMoreInfo("Sold last month");
        soldLastMonth.setDateSold(LocalDateTime.now().minusMonths(1));
        saleHistoryRepository.save(soldLastMonth);

        soldLastYear = new Sale(saleListing);
        soldLastYear.setMoreInfo("Sold last year");
        soldLastYear.setDateSold(LocalDateTime.now().minusYears(1));
        saleHistoryRepository.save(soldLastYear);
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
        Assertions.assertEquals(1, salesReport.size());
        Assertions.assertEquals(3, salesReport.get(0).getSales().size());
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

        //expect 2 items: (soldToday, alsoSoldToday) and soldYesterday
        Assertions.assertEquals(2, salesReport.size());
    }


    /**
     * Tests that getting a sales report with 'weekly' granularity
     * gets the expected sales
     */
    @Test
    void getSalesReport_weeklyGranularity_success() {
        AppUserDetails appUser = new AppUserDetails(owner);
        String periodStart = LocalDate.now().minusDays(13).toString();
        String periodEnd = LocalDate.now().toString();
        Integer businessId = business.getId();

        List<GetSalesReportDTO> salesReport = salesReportService.getSalesReport(businessId, periodStart,
                periodEnd, "weekly", appUser);

        //expect 2 items:  soldLastWeek and (soldToday, alsoSoldToday, soldYesterday)
        Assertions.assertEquals(2, salesReport.size());

        //Checking for the (soldToday, alsoSoldToday, soldYesterday) week
        Assertions.assertEquals(3, salesReport.get(1).getSales().size());

        Assertions.assertEquals(soldLastWeek.getMoreInfo(), salesReport.get(0).getSales().get(0).getMoreInfo());
    }


    /**
     * Tests that getting a sales report with 'monthly' granularity
     * gets the expected sales
     */
    @Test
    void getSalesReport_monthlyGranularity_success() {
        AppUserDetails appUser = new AppUserDetails(owner);
        String periodStart = LocalDate.now().minusMonths(1).toString();
        String periodEnd = LocalDate.now().toString();
        Integer businessId = business.getId();

        List<GetSalesReportDTO> salesReport = salesReportService.getSalesReport(businessId, periodStart,
                periodEnd, "monthly", appUser);

        //expect 2 items because spanning this month and last month
        Assertions.assertEquals(2, salesReport.size());
    }


    /**
     * Tests that getting a sales report with 'yearly' granularity
     * gets the expected sales
     */
    @Test
    void getSalesReport_yearlyGranularity_success() {
        AppUserDetails appUser = new AppUserDetails(owner);
        String periodStart = LocalDate.now().minusYears(1).toString();
        String periodEnd = LocalDate.now().toString();
        Integer businessId = business.getId();

        List<GetSalesReportDTO> salesReport = salesReportService.getSalesReport(businessId, periodStart,
                periodEnd, "yearly", appUser);

        //expect 2 items because spanning this year and last year
        Assertions.assertEquals(2, salesReport.size());

        Assertions.assertEquals(soldLastYear.getMoreInfo(), salesReport.get(0).getSales().get(0).getMoreInfo());
    }
}
