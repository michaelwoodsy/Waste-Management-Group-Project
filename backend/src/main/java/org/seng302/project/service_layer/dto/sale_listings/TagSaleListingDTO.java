package org.seng302.project.service_layer.dto.sale_listings;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.seng302.project.service_layer.dto.validators.ValidTag;

import javax.validation.constraints.NotEmpty;

/**
 * DTO for requests to change tag on a liked sale listing
 */
@Data
@AllArgsConstructor
public class TagSaleListingDTO {

    @NotEmpty(message = "Tag is a required field.")
    @ValidTag
    private String tag;

}
