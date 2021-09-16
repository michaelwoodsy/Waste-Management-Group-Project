package org.seng302.project.service_layer.dto.sales_report;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.seng302.project.repository_layer.model.Sale;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Response for a sale report.
 */
@Data
@NoArgsConstructor
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

    /**
     * Takes a list of sales and attaches them to the
     * GetSalesReportDTO as a list of GetSaleDTOs
     * @param sales list of Sale objects
     */
    public void attachSales(List<Sale> sales) {
        List<GetSaleDTO> saleDTOS = new ArrayList<>();
        for (Sale sale : sales) {
            saleDTOS.add(new GetSaleDTO(sale));
        }

        this.sales = saleDTOS;
        this.totalPurchaseValue = this.sales.stream().mapToDouble(GetSaleDTO::getPrice).sum();
        this.purchaseCount = this.sales.size();
    }

}
