package org.seng302.project.service_layer.dto.sales_report;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * Response for a sale report.
 */
@Data
public class GetSalesReportDTO {

    private List<GetSaleDTO> sales;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private Double totalPurchaseValue;
    private Integer purchaseCount;

    public GetSalesReportDTO(
            LocalDate periodStart,
            LocalDate periodEnd,
            List<GetSaleDTO> sales) {
        this.sales = sales;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.totalPurchaseValue = sales.stream().mapToDouble(GetSaleDTO::getPrice).sum();
        this.purchaseCount = sales.size();
    }

}
