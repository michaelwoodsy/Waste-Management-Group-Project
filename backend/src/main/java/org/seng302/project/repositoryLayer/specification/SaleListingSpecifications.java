package org.seng302.project.repositoryLayer.specification;

import org.seng302.project.repositoryLayer.model.SaleListing;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public final class SaleListingSpecifications {

    /**
     * Private constructor as SaleListingSpecifications is a utility class only containing static methods.
     * Therefore it should not be instantiated.
     */
    private SaleListingSpecifications() {
        throw new IllegalStateException("Utility class, SaleListingSpecifications, shouldn't be instantiated");
    }

    /**
     * Creates a Specification object used to search listings by exact match product names
     *
     * @param name product name to search sale listings by
     * @return a specification object to search repository with
     */
    public static Specification<SaleListing> hasProductName(String name) {
        return ((root, query, builder) ->
                builder.like(root.get("inventoryItem").get("product").get("name"), name)
        );
    }

    /**
     * Creates a Specification object used to search listings by product names
     *
     * @param name product name to search sale listings by
     * @return a specification object to search repository with
     */
    public static Specification<SaleListing> containsProductName(String name) {
        return ((root, query, builder) ->
                builder.like(root.get("inventoryItem").get("product").get("name"), "%" + name + "%")
        );
    }

    /**
     * Creates a Specification object used to search sale listings by price less than
     *
     * @param price price to search sale listings by
     * @return a specification object to search repository with
     */
    public static Specification<SaleListing> priceLessThan(Double price) {
        return ((root, query, builder) ->
                builder.lessThanOrEqualTo(root.get("price"), price)
        );
    }

    /**
     * Creates a Specification object used to search sale listings by price greater than
     *
     * @param price price to search sale listings by
     * @return a specification object to search repository with
     */
    public static Specification<SaleListing> priceGreaterThan(Double price) {
        return ((root, query, builder) ->
                builder.greaterThanOrEqualTo(root.get("price"), price)
        );
    }

    /**
     * Creates a Specification object used to search sale listings by closing date before
     *
     * @param date date to search sale listing by
     * @return a specification object to search repository with
     */
    public static Specification<SaleListing> closesBefore(LocalDateTime date) {
        return ((root, query, builder) ->
                builder.lessThanOrEqualTo(root.get("closes"), date)
        );
    }

    /**
     * Creates a Specification object used to search sale listings by closing date after
     *
     * @param date date to search sale listing by
     * @return a specification object to search repository with
     */
    public static Specification<SaleListing> closesAfter(LocalDateTime date) {
        return ((root, query, builder) ->
                builder.greaterThanOrEqualTo(root.get("closes"), date)
        );
    }

}
