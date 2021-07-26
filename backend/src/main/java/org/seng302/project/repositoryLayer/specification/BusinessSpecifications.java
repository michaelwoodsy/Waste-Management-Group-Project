package org.seng302.project.repositoryLayer.specification;

import org.seng302.project.repositoryLayer.model.Business;
import org.seng302.project.repositoryLayer.model.types.BusinessType;
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

    /**
     * Creates a Specification object to search businesses by country exact matches
     *
     * @param country country name to search by
     * @return a specification object to search repository with
     */
    public static Specification<Business> hasCountry(String country) {
        return ((root, query, builder) ->
                builder.like(builder.lower(root.get("address").get("country")), country));
    }

    /**
     * Creates a Specification object to search businesses by country
     *
     * @param country country name to search by
     * @return a specification object to search repository with
     */
    public static Specification<Business> containsCountry(String country) {
        return ((root, query, builder) ->
                builder.like(builder.lower(root.get("address").get("country")), "%" + country + "%"));
    }

    /**
     * Creates a Specification object to search businesses by businessType
     * @param businessType This is the businessType to search by
     * @return a specification object to search repository with
     */
    public static Specification<Business> hasBusinessType(String businessType){
        return ((root, query, builder) ->
                builder.like(root.get("businessType"), businessType));
    }
}
