package org.seng302.project.service_layer.service;

import org.seng302.project.repository_layer.model.Business;
import org.seng302.project.repository_layer.model.Sale;
import org.seng302.project.repository_layer.model.enums.ReportGranularity;

import org.seng302.project.repository_layer.repository.SaleHistoryRepository;
import org.seng302.project.repository_layer.specification.SalesReportSpecifications;
import org.seng302.project.service_layer.dto.sales_report.GetSaleDTO;
import org.seng302.project.service_layer.dto.sales_report.GetSalesReportDTO;
import org.seng302.project.service_layer.exceptions.BadRequestException;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SalesReportService {

    private static final Logger logger = LoggerFactory.getLogger(SalesReportService.class.getName());

    private final BusinessService businessService;
    private final SaleHistoryRepository saleHistoryRepository;

    @Autowired
    public SalesReportService(BusinessService businessService, SaleHistoryRepository saleHistoryRepository) {
        this.businessService = businessService;
        this.saleHistoryRepository = saleHistoryRepository;
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
     * Gets all sales a business had within the given date range
     * @param businessId Id of the business to get sales for
     * @param periodStartDate LocalDate of the period start date
     * @param periodEndDate LocalDate of the period end date
     * @return A list of sales from the given period, for the given business
     */
    List<Sale> getSalesWithinPeriod(Integer businessId, LocalDate periodStartDate, LocalDate periodEndDate) {
        Specification<Sale> saleSpec = Specification.where(null);
        saleSpec = saleSpec.and(Specification.where(SalesReportSpecifications.fromBusiness(businessId)));

        saleSpec = saleSpec.and(Specification.where(SalesReportSpecifications.soldAfter(periodStartDate.atStartOfDay())));
        saleSpec = saleSpec.and(Specification.where(SalesReportSpecifications.soldBefore(periodEndDate.atTime(23, 59))));

        return saleHistoryRepository.findAll(saleSpec);
    }


    /**
     * Generates a sales report with granularity 'all'
     * i.e. just a list of all sales in the period
     * @param businessId Id of the business to get the sale report for
     * @param periodStartDate LocalDate of the report start date
     * @param periodEndDate LocalDate of the report end date
     * @return a list of sales (with stats) for the requested period
     */
    private List<GetSalesReportDTO> getAllReport(Integer businessId, LocalDate periodStartDate, LocalDate periodEndDate) {
        List<Sale> salesWithinPeriod = getSalesWithinPeriod(businessId, periodStartDate, periodEndDate);

        List<GetSaleDTO> resultList = new ArrayList<>();

        for (Sale sale : salesWithinPeriod) {
            GetSaleDTO dto = new GetSaleDTO(sale);
            if (sale.getReview() != null){
                dto.attachReview(sale.getReview());
            }
            resultList.add(dto);
        }

        return List.of(new GetSalesReportDTO(periodStartDate, periodEndDate, resultList));
    }


    /**
     * Generates a sales report with granularity 'daily'
     * i.e. just a list of all sales in the period
     * @param businessId Id of the business to get the sale report for
     * @param periodStartDate LocalDate of the report start date
     * @param periodEndDate LocalDate of the report end date
     * @return a list of sales (with stats) for the requested period, separated by day
     */
    private List<GetSalesReportDTO> getDailyReport(Integer businessId, LocalDate periodStartDate, LocalDate periodEndDate) {
        List<Sale> salesWithinPeriod = getSalesWithinPeriod(businessId, periodStartDate, periodEndDate);

        List<GetSalesReportDTO> resultList = new ArrayList<>();
        Map<LocalDate, List<GetSaleDTO>> salesForDays = new HashMap<>();

        for (Sale sale : salesWithinPeriod) {
            if (salesForDays.containsKey(sale.getDateSold().toLocalDate())) {
                salesForDays.get(sale.getDateSold().toLocalDate()).add(new GetSaleDTO(sale));
            } else {
                List<GetSaleDTO> sales = new ArrayList<>();
                GetSaleDTO dto = new GetSaleDTO(sale);
                if (sale.getReview() != null){
                    dto.attachReview(sale.getReview());
                }
                sales.add(dto);
                salesForDays.put(sale.getDateSold().toLocalDate(), sales);
            }
        }

        for (Map.Entry<LocalDate, List<GetSaleDTO>> entry : salesForDays.entrySet()) {
            List<GetSaleDTO> sales = entry.getValue();
            LocalDate day = sales.get(0).getDateSold().toLocalDate();
            resultList.add(new GetSalesReportDTO(day, day, sales));
        }

        return resultList;
    }


    /**
     * Generates a sales report with granularity 'weekly'
     * i.e. just a list of all sales in the period
     * @param businessId Id of the business to get the sale report for
     * @param periodStartDate LocalDate of the report start date
     * @param periodEndDate LocalDate of the report end date
     * @return a list of sales (with stats) for the requested period, separated by week
     */
    private List<GetSalesReportDTO> getWeeklyReport(Integer businessId, LocalDate periodStartDate, LocalDate periodEndDate) {
        List<GetSalesReportDTO> resultList = new ArrayList<>();
        LocalDate currentStartDate = periodStartDate;

        LocalDate currentEndDate = currentStartDate.plusDays(6);
        while (currentEndDate.isBefore(periodEndDate)) {

            List<Sale> salesInWeek = getSalesWithinPeriod(businessId, currentStartDate, currentEndDate);
            GetSalesReportDTO weekDTO = new GetSalesReportDTO(currentStartDate, currentEndDate, List.of());
            weekDTO.attachSales(salesInWeek);
            resultList.add(weekDTO);
            currentStartDate = currentEndDate.plusDays(1);
            currentEndDate = currentStartDate.plusDays(6);
        }

        List<Sale> salesInWeek = getSalesWithinPeriod(businessId, currentStartDate, periodEndDate);
        GetSalesReportDTO weekDTO = new GetSalesReportDTO(currentStartDate, periodEndDate, List.of());
        weekDTO.attachSales(salesInWeek);
        resultList.add(weekDTO);

        return resultList;
    }

    /**
     * Generates a sales report with granularity 'monthly'
     * i.e. just a list of all sales in the period
     * @param businessId Id of the business to get the sale report for
     * @param periodStartDate LocalDate of the report start date
     * @param periodEndDate LocalDate of the report end date
     * @return a list of sales (with stats) for the requested period, separated by month
     */
    private List<GetSalesReportDTO> getMonthlyReport(Integer businessId, LocalDate periodStartDate, LocalDate periodEndDate) {
        List<GetSalesReportDTO> resultList = new ArrayList<>();
        LocalDate currentStartDate = periodStartDate.with(TemporalAdjusters.firstDayOfMonth());

        LocalDate currentEndDate = currentStartDate.with(TemporalAdjusters.lastDayOfMonth());
        while (currentEndDate.isBefore(periodEndDate)) {

            List<Sale> salesInMonth = getSalesWithinPeriod(businessId, currentStartDate, currentEndDate);
            GetSalesReportDTO monthDTO = new GetSalesReportDTO(currentStartDate, currentEndDate, List.of());
            monthDTO.attachSales(salesInMonth);
            resultList.add(monthDTO);
            currentStartDate = currentEndDate.plusDays(1);
            currentEndDate = currentStartDate.with(TemporalAdjusters.lastDayOfMonth());
        }

        List<Sale> salesInMonth = getSalesWithinPeriod(businessId, currentStartDate, periodEndDate);
        GetSalesReportDTO monthDTO = new GetSalesReportDTO(currentStartDate, periodEndDate, List.of());
        monthDTO.attachSales(salesInMonth);
        resultList.add(monthDTO);

        return resultList;
    }


    /**
     * Generates a sales report with granularity 'yearly'
     * i.e. just a list of all sales in the period
     * @param businessId Id of the business to get the sale report for
     * @param periodStartDate LocalDate of the report start date
     * @param periodEndDate LocalDate of the report end date
     * @return a list of sales (with stats) for the requested period, separated by year
     */
    private List<GetSalesReportDTO> getYearlyReport(Integer businessId, LocalDate periodStartDate, LocalDate periodEndDate) {
        List<GetSalesReportDTO> resultList = new ArrayList<>();
        LocalDate currentStartDate = periodStartDate.with(TemporalAdjusters.firstDayOfYear());

        LocalDate currentEndDate = currentStartDate.with(TemporalAdjusters.lastDayOfYear());
        while (currentEndDate.isBefore(periodEndDate)) {

            List<Sale> salesInYear = getSalesWithinPeriod(businessId, currentStartDate, currentEndDate);
            GetSalesReportDTO yearDTO = new GetSalesReportDTO(currentStartDate, currentEndDate, List.of());
            yearDTO.attachSales(salesInYear);
            resultList.add(yearDTO);
            currentStartDate = currentEndDate.plusDays(1);
            currentEndDate = currentStartDate.with(TemporalAdjusters.lastDayOfYear());
        }

        List<Sale> salesInYear = getSalesWithinPeriod(businessId, currentStartDate, periodEndDate);
        GetSalesReportDTO yearDTO = new GetSalesReportDTO(currentStartDate, periodEndDate, List.of());
        yearDTO.attachSales(salesInYear);
        resultList.add(yearDTO);

        return resultList;
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

        if (periodEndDate.isBefore(periodStartDate)) {
            String message = "Report end date should be after the report start date.";
            logger.warn(message);
            throw new BadRequestException(message);
        }

        if (!ReportGranularity.checkGranularity(granularity)) { //if getGranularity(granularity) == null
            String message = granularity + " is not a valid granularity";
            logger.warn(message);
            throw new BadRequestException(message);
        }

        ReportGranularity reportGranularity = ReportGranularity.getGranularity(granularity);

        List<GetSalesReportDTO> result = new ArrayList<>();

        switch (reportGranularity) { //Can ignore warning here because of above comment
            case ALL:
                result = getAllReport(businessId, periodStartDate, periodEndDate);
                break;
            case DAY:
                result = getDailyReport(businessId, periodStartDate, periodEndDate);
                break;
            case WEEK:
                result = getWeeklyReport(businessId, periodStartDate, periodEndDate);
                break;
            case MONTH:
                result = getMonthlyReport(businessId, periodStartDate, periodEndDate);
                break;
            case YEAR:
                result = getYearlyReport(businessId, periodStartDate, periodEndDate);
                break;
        }

        return result;
    }
}
