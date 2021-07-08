package org.seng302.project.repositoryLayer.specification;


import org.seng302.project.repositoryLayer.model.Product;
import org.springframework.data.jpa.domain.Specification;

/**
 * Class containing specifications that we can use to
 * search for products in the database.
 */
public class ProductSpecifications {

    /**
     * Private constructor for this utility class to hide the implicit public one.
     * Recommendation from SonarLint.
     */
    private ProductSpecifications() {
        throw new IllegalStateException("Utility class, ProductSpecifications, shouldn't be instantiated");
    }

    /**
     * Specification to find products based on a queried id
     * Matches exact ids.
     *
     * @param productId string to search by.
     * @return new specification to use when querying repository.
     */
    public static Specification<Product> hasId(String productId) {

        return ((root, query, builder) ->
                builder.like(builder.lower(root.get("id")), productId));

    }

    /**
     * Specification to find products based on a queried id
     * Matches similar ids.
     *
     * @param productId string to search by.
     * @return new specification to use when querying repository.
     */
    public static Specification<Product> containsId(String productId) {

        return ((root, query, builder) ->
                builder.like(builder.lower(root.get("id")), '%' + productId + '%'));

    }

    /**
     * Specification to find products based on a queried name
     * Matches exact names.
     *
     * @param productName string to search by.
     * @return new specification to use when querying repository.
     */
    public static Specification<Product> hasName(String productName) {

        return ((root, query, builder) ->
                builder.like(builder.lower(root.get("name")), productName));

    }

    /**
     * Specification to find products based on a queried name
     * Matches similar names.
     *
     * @param productName string to search by.
     * @return new specification to use when querying repository.
     */
    public static Specification<Product> containsName(String productName) {

        return ((root, query, builder) ->
                builder.like(builder.lower(root.get("name")), '%' + productName + '%'));

    }

    /**
     * Specification to find products based on a queried description
     * Matches exact descriptions.
     *
     * @param productDescription string to search by.
     * @return new specification to use when querying repository.
     */
    public static Specification<Product> hasDescription(String productDescription) {

        return ((root, query, builder) ->
                builder.like(builder.lower(root.get("description")), productDescription));

    }

    /**
     * Specification to find products based on a queried description
     * Matches similar descriptions.
     *
     * @param productDescription string to search by.
     * @return new specification to use when querying repository.
     */
    public static Specification<Product> containsDescription(String productDescription) {

        return ((root, query, builder) ->
                builder.like(builder.lower(root.get("description")), '%' + productDescription + '%'));

    }

    /**
     * Specification to find products based on a queried manufacturer
     * Matches exact manufacturers.
     *
     * @param productManufacturer string to search by.
     * @return new specification to use when querying repository.
     */
    public static Specification<Product> hasManufacturer(String productManufacturer) {

        return ((root, query, builder) ->
                builder.like(builder.lower(root.get("manufacturer")), productManufacturer));

    }

    /**
     * Specification to find products based on a queried manufacturer
     * Matches similar manufacturers.
     *
     * @param productManufacturer string to search by.
     * @return new specification to use when querying repository.
     */
    public static Specification<Product> containsManufacturer(String productManufacturer) {

        return ((root, query, builder) ->
                builder.like(builder.lower(root.get("manufacturer")), '%' + productManufacturer + '%'));

    }
}
