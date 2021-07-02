package org.seng302.project.repositoryLayer.specification;

import org.seng302.project.repositoryLayer.model.Card;
import org.springframework.data.jpa.domain.Specification;

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
     * Specification to find a card based on a keywordId.
     *
     * @param keywordId keyword to search by.
     * @return new specification to use when querying repository.
     */
    public static Specification<Card> hasKeyword(Integer keywordId) {
        return ((root, query, builder) ->
                builder.isMember(keywordId, root.get("keywordIds")));
    }
}
