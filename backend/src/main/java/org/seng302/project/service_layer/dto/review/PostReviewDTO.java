package org.seng302.project.service_layer.dto.review;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * DTO for creating a sale review
 */
@AllArgsConstructor
@Data
public class PostReviewDTO {

    @NotNull(message = "Rating is a mandatory field.")
    @Range(min = 1, max = 5, message = "Rating must be between 1 and 5")
    private Integer rating;

    private String reviewMessage;
}
