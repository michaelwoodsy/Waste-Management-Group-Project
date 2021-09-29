package org.seng302.project.service_layer.dto.review;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.seng302.project.repository_layer.model.Review;
import org.seng302.project.repository_layer.model.Sale;
import org.seng302.project.service_layer.dto.sales_report.GetSaleDTO;
import org.seng302.project.service_layer.dto.user.GetUserDTO;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class GetReviewDTO {

    private Integer reviewId;
    private GetSaleDTO sale;
    private GetUserDTO user;
    private Integer rating;
    private String reviewMessage;
    private String reviewResponse;
    private LocalDateTime created;

    public GetReviewDTO(Review review) {
        this.reviewId = review.getReviewId();
        this.sale = null;
        this.user = new GetUserDTO(review.getUser());
        this.rating = review.getRating();
        this.reviewMessage = review.getReviewMessage();
        this.reviewResponse = review.getReviewResponse();
        this.created = review.getCreated();
    }

    /**
     * Attaches a Sale object to the review DTO
     *
     * @param sale the Sale object to attach
     */
    public void attachSale(Sale sale) {
        this.sale = new GetSaleDTO(sale);
    }

}
