package org.seng302.project.repository_layer.repository;

import org.seng302.project.repository_layer.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository for Address objects in the database.
 */
public interface AddressRepository extends JpaRepository<Address, Integer> {

    /**
     * Allows finding a Address by country.
     *
     * @param country country of address.
     * @return list of addresses with matching country.
     */
    List<Address> findByCountry(@Param("country") String country);

}