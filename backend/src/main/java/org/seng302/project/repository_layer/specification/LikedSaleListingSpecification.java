package org.seng302.project.repository_layer.specification;

import org.seng302.project.repository_layer.model.LikedSaleListing;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class LikedSaleListingSpecification {

    /**
     * Private constructor as sonarlint was complaining about an implicit public constructor
     */
    private LikedSaleListingSpecification() {
        throw new IllegalStateException("This is a utility class");
    }

    /**
     * Creates a Specification object used to search liked listings by products closing date.
     *
     * @param dateTime product name to search sale listings by
     * @return a specification object to search repository with
     */
    public static Specification<LikedSaleListing> saleListingClosesBefore(LocalDateTime dateTime) {
        return ((root, query, builder) ->
            builder.lessThanOrEqualTo(root.get("listing").get("closes"), dateTime)
        );
    }
}
