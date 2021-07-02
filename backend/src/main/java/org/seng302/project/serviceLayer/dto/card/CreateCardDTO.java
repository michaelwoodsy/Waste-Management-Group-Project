package org.seng302.project.serviceLayer.dto.card;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.seng302.project.serviceLayer.dto.validators.ValidMarketplaceSection;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * DTO for creating a card.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCardDTO {

    @NotNull
    private Integer creatorId;

    @NotEmpty
    @ValidMarketplaceSection
    private String section;

    @NotEmpty
    private String title;
    private String description;
    private List<Integer> keywordIds;

}
