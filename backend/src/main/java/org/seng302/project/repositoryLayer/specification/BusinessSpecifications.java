package org.seng302.project.repositoryLayer.specification;

import org.seng302.project.repositoryLayer.model.Business;
import org.springframework.data.jpa.domain.Specification;

/**
 * Class containing specifications used for searching businesses
 */
public class BusinessSpecifications {

    /**
     * Private constructor for this utility class to hide the implicit public one.
     * Recommendation from SonarLint.
     */
    private BusinessSpecifications() {
        throw new IllegalStateException("Utility class, BusinessSpecifications, shouldn't be instantiated");
    }

    /**
     * Specification to find a business based on a queried name.
     * Matches exact names.
     *
     * @param name name to search by.
     * @return new specification to use when querying repository.
     */
    public static Specification<Business> hasName(String name) {
        return ((root, query, builder) ->
                                builder.like(builder.lower(root.get("name")), name));
    }

    /**
     * Specification to find a business based on a queried name.
     * Matches similar names.
     *
     * @param name name to search by.
     * @return new specification to use when querying repository.
     */
    public static Specification<Business> containsName(String name) {
        return ((root, query, builder) ->
                builder.like(builder.lower(root.get("name")), '%' + name + '%'));
    }
}
