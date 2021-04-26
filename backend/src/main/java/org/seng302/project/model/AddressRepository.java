package org.seng302.project.model;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for Address objects in the database.
 */
public interface AddressRepository extends JpaRepository<Address, Integer> {

}