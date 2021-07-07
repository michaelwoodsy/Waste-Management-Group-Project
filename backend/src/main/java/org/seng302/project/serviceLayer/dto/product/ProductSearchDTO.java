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
    private boolean matchingId;
    @NotNull
    private boolean matchingName;
    @NotNull
    private boolean matchingDescription;
    @NotNull
    private boolean matchingManufacturer;

}
