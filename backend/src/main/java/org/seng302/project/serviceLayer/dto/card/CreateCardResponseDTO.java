package org.seng302.project.serviceLayer.dto.card;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO object used as response for creating a card.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCardResponseDTO {

    private Integer cardId;

}
