package org.seng302.project.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;


/**
 * Card repository which acts as an interface to the database for manipulating cards.
 */
@RepositoryRestResource
public interface CardRepository extends JpaRepository<Card, Integer> {

    List<Card> findAllBySection(@Param("section") String section);

    /**
     * Finds all cards created by a user.
     * @param user The user to find all cards for.
     * @return List of cards created by the user.
     */
    List<Card> findAllByCreator(@Param("creator") User user);
}