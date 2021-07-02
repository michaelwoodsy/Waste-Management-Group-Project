package org.seng302.project.serviceLayer.dto.card;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.seng302.project.serviceLayer.dto.validators.ValidMarketplaceSection;
import org.seng302.project.webLayer.authentication.AppUserDetails;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * DTO for creating a card.
 */
@Data
@NoArgsConstructor
public class CreateCardDTO {

    @NotEmpty
    private Integer creatorId;
    @NotEmpty
    @ValidMarketplaceSection
    private String section;
    @NotEmpty
    private String title;
    private String description;
    private List<Integer> keywordIds;
    private AppUserDetails appUser;

}
