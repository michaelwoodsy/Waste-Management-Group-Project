package org.seng302.project.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


/**
 * Card repository which acts as an interface to the database for manipulating cards.
 */
@RepositoryRestResource
public interface CardRepository extends JpaRepository<Card, Integer> {

    List<Card> findAllBySection(@Param("section") String section);

    @Modifying
    @Transactional
    void deleteByCreatedBeforeOrCreatedEquals(LocalDateTime expiryDate);

}