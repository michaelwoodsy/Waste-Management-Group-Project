package org.seng302.project.serviceLayer.dto.business;

import lombok.Data;
import org.seng302.project.serviceLayer.dto.validators.ValidBusinessType;


/**
 * DTO for a request to search for businesses
 */
@Data
public class SearchBusinessDTO {

    private String searchQuery;

    @ValidBusinessType
    private String businessType;

    public SearchBusinessDTO(String searchQuery, String businessType) {
        this.searchQuery = searchQuery;
        this.businessType = businessType;
    }
}
