package org.seng302.project.repositoryLayer.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * NewKeywordNotification class for notifications that inform system administrators
 * of new keywords.
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
@PrimaryKeyJoinColumn(name = "new_keyword_notification_id")
public class NewKeywordNotification extends AdminNotification {

    private Keyword keyword;

    /**
     * Constructor for creating a new NewKeywordNotification object.
     *
     * @param keyword                     Keyword this notification is about.
     */
    public NewKeywordNotification(Keyword keyword) {
        super(String.format("New keyword created with the name: %s", keyword.getName()));
        this.keyword = keyword;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "keyword_id")
    public Keyword getKeyword() {
        return this.keyword;
    }
}
