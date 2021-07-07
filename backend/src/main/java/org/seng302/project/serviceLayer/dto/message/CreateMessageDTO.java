package org.seng302.project.serviceLayer.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * DTO for POST requests to create messages
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMessageDTO {

    private Integer userId;
    private Integer cardId;
    @NotEmpty
    private String text;

}
