package org.seng302.project.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


/**
 * Card repository which acts as an interface to the database for manipulating users.
 */
@RepositoryRestResource
public interface CardRepository extends JpaRepository<Card, Integer> {

}