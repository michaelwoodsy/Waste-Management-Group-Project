package org.seng302.project.repository_layer.specification;

import org.seng302.project.repository_layer.model.Sale;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class SalesReportSpecifications {

    private SalesReportSpecifications() {
        throw new IllegalStateException("Utility class, SalesReportSpecifications, shouldn't be instantiated");
    }

    public static Specification<Sale> soldBefore(LocalDateTime dateSold) {
        return ((root, query, builder) ->
                builder.lessThanOrEqualTo(root.get("dateSold"), dateSold)
        );
    }

    public static Specification<Sale> soldAfter(LocalDateTime dateSold) {
        return ((root, query, builder) ->
                builder.greaterThanOrEqualTo(root.get("dateSold"), dateSold)
        );
    }
}
