package org.seng302.project.service_layer.dto.message;

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
