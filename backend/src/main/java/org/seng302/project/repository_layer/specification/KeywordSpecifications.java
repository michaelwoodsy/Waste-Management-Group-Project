package org.seng302.project.repository_layer.specification;


import org.seng302.project.repository_layer.model.Keyword;
import org.springframework.data.jpa.domain.Specification;

/**
 * Class containing specifications that we can use to
 * search for keywords in the database.
 */
public class KeywordSpecifications {

    /**
     * Private constructor for this utility class to hide the implicit public one.
     * Recommendation from SonarLint.
     */
    private KeywordSpecifications() {
        throw new IllegalStateException("Utility class, KeywordSpecifications, shouldn't be instantiated");
    }


    /**
     * Specification to find keywords based on a partial keyword.
     * Matches similar keywords.
     *
     * @param partialKeyword string to search by.
     * @return new specification to use when querying repository.
     */
    public static Specification<Keyword> containsPartialKeyword(String partialKeyword) {

        return ((root, query, builder) ->
                builder.like(builder.lower(root.get("name")), '%' + partialKeyword + '%'));

    }
}
