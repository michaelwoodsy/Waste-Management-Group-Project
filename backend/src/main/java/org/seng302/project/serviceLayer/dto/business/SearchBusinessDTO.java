package org.seng302.project.serviceLayer.dto.business;

import lombok.Data;
import org.seng302.project.repositoryLayer.model.types.BusinessType;


/**
 * DTO for a request to search for businesses
 */
@Data
public class SearchBusinessDTO {

    private String searchQuery;

    private BusinessType businessType;

    public SearchBusinessDTO(String searchQuery, BusinessType businessType) {
        this.searchQuery = searchQuery;
        this.businessType = businessType;
    }
}
