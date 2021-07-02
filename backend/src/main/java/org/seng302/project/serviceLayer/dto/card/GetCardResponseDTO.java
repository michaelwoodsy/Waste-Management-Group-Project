package org.seng302.project.serviceLayer.dto.card;

import lombok.Data;
import org.seng302.project.repositoryLayer.model.Card;
import org.seng302.project.repositoryLayer.model.Keyword;
import org.seng302.project.repositoryLayer.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Response DTO for sending back a card
 */
@Data
public class GetCardResponseDTO {

    private Integer id;
    private User creator;
    private String section;
    private LocalDateTime created;
    private LocalDateTime displayPeriodEnd;
    private String title;
    private String description;
    private List<Keyword> keywords;

    public GetCardResponseDTO(Card card) {
        this.id = card.getId();
        this.creator = card.getCreator();
        this.section = card.getSection();
        this.created = card.getCreated();
        this.displayPeriodEnd = card.getDisplayPeriodEnd();
        this.title = card.getTitle();
        this.description = card.getDescription();
        this.keywords = new ArrayList(card.getKeywords());
    }

}
