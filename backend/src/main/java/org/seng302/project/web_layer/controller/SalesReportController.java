package org.seng302.project.web_layer.controller;


import org.seng302.project.service_layer.dto.sales_report.GetSalesReportDTO;
import org.seng302.project.service_layer.service.SalesReportService;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;


/**
 * Rest controller for sale reports.
 */
@RestController
public class SalesReportController {

    private final SalesReportService salesReportService;

    @Autowired
    public SalesReportController(SalesReportService salesReportService) {
        this.salesReportService = salesReportService;

    }

    /**
     * Gets a sales report for a business.
     *
     * @param businessId Business to get the sale report for
     * @param periodStart The date to start the report
     * @param periodEnd The date to end the report
     * @param appUser    The user that made the request.
     * @return  a GetSalesReportDTO containing stats and sales from the requested time period
     */
    @GetMapping("/businesses/{businessId}/salesReport")
    public GetSalesReportDTO getSalesReport(
            @PathVariable int businessId,
            @RequestParam("periodStart") LocalDate periodStart,
            @RequestParam("periodEnd") LocalDate periodEnd,
            @AuthenticationPrincipal AppUserDetails appUser) {

        return salesReportService.getSalesReport(businessId, periodStart, periodEnd, appUser);
    }
}
