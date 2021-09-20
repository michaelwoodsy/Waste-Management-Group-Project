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

import java.util.List;


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
     * @param periodStart The date to start the report in the form "yyyy-MM-dd"
     * @param periodEnd The date to end the report in the form "yyyy-MM-dd"
     * @param granularity The granularity for the report e.g. "monthly", "weekly"
     * @param appUser    The user that made the request.
     * @return  a list of GetSalesReportDTOs containing stats and sales from the requested time period
     */
    @GetMapping("/businesses/{businessId}/sales")
    public List<GetSalesReportDTO> getSalesReport(
            @PathVariable int businessId,
            @RequestParam("periodStart") String periodStart,
            @RequestParam("periodEnd") String periodEnd,
            @RequestParam("granularity") String granularity,
            @AuthenticationPrincipal AppUserDetails appUser) {

        return salesReportService.getSalesReport(businessId, periodStart, periodEnd, granularity, appUser);
    }
}
