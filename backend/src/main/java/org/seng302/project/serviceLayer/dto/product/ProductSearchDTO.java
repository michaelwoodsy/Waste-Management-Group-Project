package org.seng302.project.serviceLayer.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * DTO for a request to search a business's products
 */
@Data
@AllArgsConstructor
public class ProductSearchDTO {

    @NotNull
    private Boolean matchingId;
    @NotNull
    private Boolean matchingName;
    @NotNull
    private Boolean matchingDescription;
    @NotNull
    private Boolean matchingManufacturer;

}
