package org.seng302.project.service_layer.service;

import org.seng302.project.service_layer.dto.sales_report.GetSalesReportDTO;
import org.seng302.project.service_layer.exceptions.BadRequestException;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class SalesReportService {

    private static final Logger logger = LoggerFactory.getLogger(SalesReportService.class.getName());

    private final UserService userService;
    private final BusinessService businessService;

    @Autowired
    public SalesReportService(UserService userService, BusinessService businessService) {
        this.userService = userService;
        this.businessService = businessService;
    }

    /**
     * Gets a sales report
     * @param businessId Business to get the sale report for
     * @param periodStart The date to start the report in the form "yyyy-MM-dd"
     * @param periodEnd The date to end the report in the form "yyyy-MM-dd"
     * @param granularity The granularity for the report e.g. "month", "week"
     * @param appUser    The user that made the request.
     * @return  a list of GetSalesReportDTOs containing stats and sales from the requested time period
     */
    public List<GetSalesReportDTO> getSalesReport(Integer businessId, String periodStart, String periodEnd,
                                            String granularity, AppUserDetails appUser) {

        logger.info("Request to get a sales report for business with id {}, from {} to {}",
                businessId, periodStart, periodEnd);

        //Convert periodStart and periodEnd to LocalDate
        try {
            LocalDate periodStartDate = LocalDate.parse(periodStart);
            LocalDate periodEndDate = LocalDate.parse(periodEnd);
        } catch (DateTimeParseException parseException) {
            throw new BadRequestException("Date format should be yyyy-MM-dd");
        }


        businessService.checkBusiness(businessId);

        //TODO: implement functionality
        //If granularity = "month" add a GetSalesReportDTO to the list for each month in the date range

        return List.of(new GetSalesReportDTO(LocalDate.now(), LocalDate.now(), List.of()));
    }
}
