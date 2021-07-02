package org.seng302.project.serviceLayer.dto.card;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO object used as response for creating a card.
 */
@Data
@AllArgsConstructor
public class CreateCardResponseDTO {

    private Integer cardId;

}
