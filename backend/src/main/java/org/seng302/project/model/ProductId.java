package org.seng302.project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ProductId implements Serializable {
    private String id;
    private Integer businessId;

    // default constructor
    public ProductId(String id, Integer businessId) {
        this.businessId = businessId;
        this.id = id;
    }

    /**
     * Gets a hashcode for the ProductId
     * @return the product ids hashcode
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((businessId == null) ? 0 : businessId.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    /**
     * Returns true if the two product ids have the same id and businessId
     * @param other Other id to compare
     * @return boolean, true if they are the same
     */
    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null)
            return false;
        if (getClass() != other.getClass())
            return false;

        ProductId otherId = (ProductId) other;
        return this.businessId.equals(otherId.getBusinessId())
                && this.id.equals(otherId.getId());
    }


}