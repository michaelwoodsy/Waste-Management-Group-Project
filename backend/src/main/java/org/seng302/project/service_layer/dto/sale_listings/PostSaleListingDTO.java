package org.seng302.project.service_layer.dto.sale_listings;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * DTO for creating SaleListing entities
 */
@Data
@AllArgsConstructor
public class PostSaleListingDTO {

    @NotNull(message = "Inventory item id is a mandatory field.")
    Integer inventoryItemId;

    @NotNull(message = "Price is a mandatory field.")
    @Min(value = 0, message = "Price must be greater than or equal to zero")
    Double price;
    String moreInfo;
    String closes;

    @NotNull(message = "Quantity is a mandatory field.")
    @Min(value = 0, message = "Quantity must be greater than or equal to zero")
    Integer quantity;

}
