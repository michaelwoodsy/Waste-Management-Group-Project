package org.seng302.project.service_layer.service;

import org.seng302.project.service_layer.dto.sales_report.GetSalesReportDTO;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SalesReportService {

    private static final Logger logger = LoggerFactory.getLogger(SalesReportService.class.getName());

    @Autowired
    public SalesReportService() {

    }

    /**
     * Gets a sales report
     * @param businessId Business to get the sale report for
     * @param periodStart The date to start the report
     * @param periodEnd The date to end the report
     * @param granularity The granularity for the report e.g. "month", "week"
     * @param appUser    The user that made the request.
     * @return  a list of GetSalesReportDTOs containing stats and sales from the requested time period
     */
    public List<GetSalesReportDTO> getSalesReport(Integer businessId, LocalDate periodStart, LocalDate periodEnd,
                                            String granularity, AppUserDetails appUser) {

        logger.info("Request to get a sales report for business with id {}", businessId);

        //TODO: implement functionality
        //If granularity = "month" add a GetSalesReportDTO to the list for each month in the date range

        return List.of(new GetSalesReportDTO(LocalDate.now(), LocalDate.now(), List.of()));
    }
}
