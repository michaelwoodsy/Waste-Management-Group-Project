package org.seng302.project.repository_layer.specification;

import org.seng302.project.repository_layer.model.Sale;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public final class SalesReportSpecifications {

    /**
     * Private constructor as SaleReportSpecifications is a utility class only containing static methods.
     * Therefore, it should not be instantiated.
     */
    private SalesReportSpecifications() {
        throw new IllegalStateException("Utility class, SalesReportSpecifications, shouldn't be instantiated");
    }

    /**
     * Creates a Specification object used to search sales by sold date before
     *
     * @param dateSold the date to search sales by
     * @return a specification object to search repository with
     */
    public static Specification<Sale> soldBefore(LocalDateTime dateSold) {
        return ((root, query, builder) ->
                builder.lessThanOrEqualTo(root.get("dateSold"), dateSold)
        );
    }

    /**
     * Creates a Specification object used to search sales by sold date after
     *
     * @param dateSold the date to search sales by
     * @return a specification object to search repository with
     */
    public static Specification<Sale> soldAfter(LocalDateTime dateSold) {
        return ((root, query, builder) ->
                builder.greaterThanOrEqualTo(root.get("dateSold"), dateSold)
        );
    }
}
