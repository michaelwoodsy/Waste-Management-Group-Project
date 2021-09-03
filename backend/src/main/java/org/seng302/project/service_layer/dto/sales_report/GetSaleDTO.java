package org.seng302.project.service_layer.dto.sales_report;

import lombok.Data;
import org.seng302.project.repository_layer.model.ProductArchive;
import org.seng302.project.repository_layer.model.Sale;
import org.seng302.project.service_layer.dto.business.GetBusinessDTO;

import java.time.LocalDateTime;

/**
 * Response for a sale.
 */
@Data
public class GetSaleDTO {

    private GetBusinessDTO business;
    private Integer oldListingId;
    private Double price;
    private String moreInfo;
    private LocalDateTime dateSold;
    private Integer quantity;
    private String productId;
    private String productName;
    private String productDescription;
    private String productManufacturer;
    private String currencyCountry;

    public GetSaleDTO(Sale sale) {
        this.business = new GetBusinessDTO(sale.getBusiness());
        this.oldListingId = sale.getOldListingId();
        this.price = sale.getPrice();
        this.moreInfo = sale.getMoreInfo();
        this.dateSold = sale.getDateSold();
        this.quantity = sale.getQuantity();
        ProductArchive product = sale.getInventoryItem().getProduct();
        this.productId = product.getProductId();
        this.productName = product.getName();
        this.productDescription = product.getDescription();
        this.productManufacturer = product.getManufacturer();
        this.currencyCountry = product.getCurrencyCountry();
    }
}
