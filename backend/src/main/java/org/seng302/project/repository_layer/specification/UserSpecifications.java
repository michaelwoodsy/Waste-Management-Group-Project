package org.seng302.project.repository_layer.specification;

import org.seng302.project.repository_layer.model.User;
import org.springframework.data.jpa.domain.Specification;

/**
 * Class containing specifications that we can
 * search for users in the database with.
 */
public class UserSpecifications {

    /**
     * Private constructor for this utility class to hide the implicit public one.
     * Recommendation from SonarLint.
     */
    private UserSpecifications() {
        throw new IllegalStateException("Utility class, UserSpecifications, shouldn't be instantiated");
    }

    /**
     * Specification to find a user based on a queried name.
     * Matches exact names.
     *
     * @param name name to search by.
     * @return new specification to use when querying repository.
     */
    public static Specification<User> hasName(String name) {
        return ((root, query, builder) ->
                builder.or(
                        builder.or(
                                builder.like(builder.lower(root.get("firstName")), name),
                                builder.like(builder.lower(root.get("lastName")), name)),
                        builder.or(
                                builder.like(builder.lower(root.get("middleName")), name),
                                builder.like(builder.lower(root.get("nickname")), name)
                        )));
    }

    /**
     * Specification to find a user based on a queried name.
     * Matches similar names.
     *
     * @param name name to search by.
     * @return new specification to use when querying repository.
     */
    public static Specification<User> containsName(String name) {
        return ((root, query, builder) ->
                builder.or(
                        builder.or(
                                builder.like(builder.lower(root.get("firstName")), '%' + name + '%'),
                                builder.like(builder.lower(root.get("lastName")), '%' + name + '%')),
                        builder.or(
                                builder.like(builder.lower(root.get("middleName")), '%' + name + '%'),
                                builder.like(builder.lower(root.get("nickname")), '%' + name + '%')
                        )));
    }

}
