package org.seng302.project.serviceLayer.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO for responses from a request to create a message
 */
@Data
@AllArgsConstructor
public class CreateMessageResponseDTO {

    private Integer messageId;
}
