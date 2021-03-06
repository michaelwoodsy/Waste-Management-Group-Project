package org.seng302.project.service_layer.dto.keyword;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * DTO for adding a new keyword to the system keyword list.
 */
@Data
@NoArgsConstructor
public class AddKeywordDTO {

    @NotEmpty(message = "Keyword name is a required field.")
    @Size(min = 3, max = 25, message = "Keyword must be between 3 and 25 characters.")
    private String name;

    public AddKeywordDTO(String name) {
        this.name = name;
    }

}
