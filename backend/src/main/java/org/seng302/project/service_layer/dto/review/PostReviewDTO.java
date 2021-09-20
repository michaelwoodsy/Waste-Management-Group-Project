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

    @NotNull
    @Range(min = 0, max = 5, message = "Rating must be between 0 and 5")
    private Integer rating;

    private String reviewMessage;
}
