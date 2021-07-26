package org.seng302.project.serviceLayer.dto.saleListings;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Response DTO for business entities.
 */
@Data
@AllArgsConstructor
public class SearchSaleListingsDTO {
    private String searchQuery;
    private boolean matchProductName;
    private boolean matchBusinessName;
    private boolean matchBusinessLocation;
    private boolean matchBusinessType;
    private Double priceRangeLower;
    private Double priceRangeUpper;
    private String closingDateLower;
    private String closingDateUpper;
    private String sortBy;
    private Integer pageNumber;
}
