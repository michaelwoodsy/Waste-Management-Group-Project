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

    /**
     * Creates a Specification object used to search sales by business
     *
     * @param businessId the id of the business to find sales of
     * @return a specification object to search repository with
     */
    public static Specification<Sale> fromBusiness(Integer businessId) {
        return ((root, query, builder) ->
                builder.equal(root.get("business").get("id"), businessId)
        );
    }

    /**
     * Creates a Specification object used to search sales by user who purchased them
     *
     * @param userId ID of the user to find sales purchased by
     * @return a Specification object to search repository with
     */
    public static Specification<Sale> purchasedByUser(Integer userId) {
        return ((root, query, builder) ->
                builder.equal(root.get("buyer").get("id"), userId)
        );
    }
}
