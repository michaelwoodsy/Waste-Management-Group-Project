package org.seng302.project.service_layer.service;

import org.seng302.project.repository_layer.model.Business;
import org.seng302.project.repository_layer.model.enums.ReportGranularity;
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

    private final BusinessService businessService;

    @Autowired
    public SalesReportService(BusinessService businessService) {
        this.businessService = businessService;
    }

    /**
     * Used by the getSalesReport method to parse the report start and end dates
     * Throws BadRequestException when the date is an incorrect format
     * @param dateString A date that should be in the form "yyyy-MM-dd"
     * @return a LocalDate object of the date
     */
    private LocalDate parseDate(String dateString) {
        try {
            return LocalDate.parse(dateString);
        } catch (DateTimeParseException parseException) {
            BadRequestException badRequestException =  new BadRequestException(
                    "Date: " + dateString + " is not in the correct format of yyyy-MM-dd");
            logger.warn(badRequestException.getMessage());
            throw badRequestException;
        }
    }

    /**
     * Gets a sales report
     * @param businessId Business to get the sale report for
     * @param periodStart The date to start the report in the form "yyyy-MM-dd"
     * @param periodEnd The date to end the report in the form "yyyy-MM-dd"
     * @param granularity The granularity for the report e.g. "monthly", "weekly"
     * @param appUser    The user that made the request.
     * @return  a list of GetSalesReportDTOs containing stats and sales from the requested time period
     */
    public List<GetSalesReportDTO> getSalesReport(Integer businessId, String periodStart, String periodEnd,
                                            String granularity, AppUserDetails appUser) {

        logger.info("Request to get a sales report for business with id {}, from {} to {}",
                businessId, periodStart, periodEnd);

        Business business =  businessService.checkBusiness(businessId);
        businessService.checkUserCanDoBusinessAction(appUser, business);

        LocalDate periodStartDate = parseDate(periodStart);
        LocalDate periodEndDate =  parseDate(periodEnd);

        if (periodEndDate.compareTo(periodStartDate) < 0) {
            String message = "Report end date should be after the report start date.";
            logger.warn(message);
            throw new BadRequestException(message);
        }

        if (!ReportGranularity.checkGranularity(granularity)) {
            String message = granularity + " is not a valid granularity";
            logger.warn(message);
            throw new BadRequestException(message);
        }

        ReportGranularity reportGranularity = ReportGranularity.getGranularity(granularity);

        //TODO: implement functionality
        //If granularity = "monthly" add a GetSalesReportDTO to the list for each month in the date range

        return List.of(new GetSalesReportDTO(LocalDate.now(), LocalDate.now(), List.of()));
    }
}
