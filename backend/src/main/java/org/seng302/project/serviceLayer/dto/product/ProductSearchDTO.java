package org.seng302.project.serviceLayer.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

/**
 * DTO for a request to search a business's products
 */
@Data
@AllArgsConstructor
public class ProductSearchDTO {

    @NonNull
    private boolean matchingId;
    @NonNull
    private boolean matchingName;
    @NonNull
    private boolean matchingDescription;
    @NonNull
    private boolean matchingManufacturer;

}
