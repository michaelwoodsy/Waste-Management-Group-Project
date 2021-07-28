package org.seng302.project.service_layer.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO for a request to search a business's products
 */
@Data
@AllArgsConstructor
public class ProductSearchDTO {

    private Boolean matchingId;

    private Boolean matchingName;

    private Boolean matchingDescription;

    private Boolean matchingManufacturer;

}
