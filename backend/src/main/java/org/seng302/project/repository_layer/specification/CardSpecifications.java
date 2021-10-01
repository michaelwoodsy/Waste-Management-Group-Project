package org.seng302.project.repository_layer.specification;

import org.seng302.project.repository_layer.model.Card;
import org.seng302.project.repository_layer.model.Keyword;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

/**
 * Class containing specifications used for searching cards
 */
public class CardSpecifications {
    /**
     * Private constructor for this utility class to hide the implicit public one.
     * Recommendation from SonarLint.
     */
    private CardSpecifications() {
        throw new IllegalStateException("Utility class, CardSpecifications, shouldn't be instantiated");
    }

    /**
     * Specification to find a card based on a section
     * @param section section to search by
     * @return new specification to use when querying repository.
     */
    public static Specification<Card> inSection(String section) {
        return ((root, query, builder) ->
                builder.like(root.get("section"), section));
    }

    /**
     * Specification to find a card based on a keywordId.
     *
     * @param keyword keyword to search by.
     * @return new specification to use when querying repository.
     */
    public static Specification<Card> hasKeyword(Keyword keyword) {
        return ((root, query, builder) ->
                builder.isMember(keyword, root.get("keywords")));
    }

    /**
     * Creates a Specification object used to filter cards that are still active
     *
     * @return a specification object to search repository with
     */
    public static Specification<Card> isActive() {
        return ((root, query, builder) ->
                builder.greaterThanOrEqualTo(root.get("displayPeriodEnd"), LocalDateTime.now())
        );
    }
}
