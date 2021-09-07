package org.seng302.project.service_layer.dto.contact;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.seng302.project.service_layer.dto.validators.user.ValidEmail;

import javax.validation.constraints.NotEmpty;

/**
 * DTO for contacting resale.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostContactDTO {
    @NotEmpty(message = "MissingData: Email is a mandatory field")
    @ValidEmail
    private String email;
    @NotEmpty(message = "MissingData: Message is a mandatory field")
    private String message;
}
