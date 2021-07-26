package org.seng302.project.repositoryLayer.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class SaleListingId implements Serializable {

    private Integer id;
    private Integer businessId;

//    /**
//     * Gets a hashcode for the ProductId
//     * @return the product ids hashcode
//     */
//    @Override
//    public int hashCode() {
//        final int prime = 31;
//        int result = 1;
//        result = prime * result + ((business == null) ? 0 : business.getId().hashCode());
//        result = prime * result + ((id == null) ? 0 : id.hashCode());
//        return result;
//    }
//
//    /**
//     * Returns true if the two product ids have the same id and businessId
//     * @param other Other id to compare
//     * @return boolean, true if they are the same
//     */
//    @Override
//    public boolean equals(Object other) {
//        if (this == other)
//            return true;
//        if (other == null)
//            return false;
//        if (getClass() != other.getClass())
//            return false;
//
//        SaleListingId otherId = (SaleListingId) other;
//        return this.businessId.equals(otherId.getBusinessId())
//                && this.id.equals(otherId.getId());
//    }


}